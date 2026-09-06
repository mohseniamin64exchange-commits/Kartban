package com.kartyar.kartyar

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties

class MainActivity : FlutterFragmentActivity() {
    private val channelName = "ir.kartyar/security"
    private val keyAlias = "kartyar_personal_card_aes_v1"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channelName).setMethodCallHandler { call, result ->
            try {
                when (call.method) {
                    "setSecureScreen" -> {
                        val arguments = call.arguments as? Map<*, *>
                        val enabled = arguments?.get("enabled") as? Boolean ?: false
                        runOnUiThread {
                            if (enabled) window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                            else window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                        }
                        result.success(null)
                    }
                    "isDeviceSecure" -> {
                        val manager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                        result.success(manager.isDeviceSecure)
                    }
                    "ensurePersonalCardKey" -> {
                        val arguments = call.arguments as? Map<*, *>
                        ensureKey(arguments?.get("requireAuthentication") as? Boolean ?: true)
                        result.success(null)
                    }
                    "deletePersonalCardKey" -> {
                        val store = keyStore()
                        if (store.containsAlias(keyAlias)) store.deleteEntry(keyAlias)
                        result.success(null)
                    }
                    "encryptPersonalCardSecret" -> result.success(encrypt(call.arguments as Map<*, *>))
                    "decryptPersonalCardSecret" -> result.success(decrypt(call.arguments as Map<*, *>))
                    else -> result.notImplemented()
                }
            } catch (error: Exception) {
                result.error("SECURITY_FAILURE", error.message ?: "Security operation failed", null)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTaskDescription(android.app.ActivityManager.TaskDescription("کارت‌یار"))
    }

    private fun keyStore(): KeyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    private fun ensureKey(requireAuthentication: Boolean): SecretKey {
        val store = keyStore()
        (store.getKey(keyAlias, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val builder = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setRandomizedEncryptionRequired(true)

        if (requireAuthentication) {
            builder.setUserAuthenticationRequired(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                builder.setUserAuthenticationParameters(
                    30,
                    KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL
                )
            } else {
                @Suppress("DEPRECATION")
                builder.setUserAuthenticationValidityDurationSeconds(30)
            }
        }
        generator.init(builder.build())
        return generator.generateKey()
    }

    private fun encrypt(arguments: Map<*, *>): Map<String, Any> {
        require(arguments["isPersonalCard"] == true) { "Customer sensitive data is rejected" }
        val plaintext = arguments["plaintext"] as? ByteArray ?: error("Missing plaintext")
        val associatedData = arguments["associatedData"] as? ByteArray
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, ensureKey(true))
        if (associatedData != null) cipher.updateAAD(associatedData)
        val ciphertext = cipher.doFinal(plaintext)
        plaintext.fill(0)
        return mapOf("ciphertext" to ciphertext, "iv" to cipher.iv, "version" to 1)
    }

    private fun decrypt(arguments: Map<*, *>): ByteArray {
        require(arguments["isPersonalCard"] == true) { "Customer sensitive data is rejected" }
        val ciphertext = arguments["ciphertext"] as? ByteArray ?: error("Missing ciphertext")
        val iv = arguments["iv"] as? ByteArray ?: error("Missing iv")
        val associatedData = arguments["associatedData"] as? ByteArray
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, ensureKey(true), GCMParameterSpec(128, iv))
        if (associatedData != null) cipher.updateAAD(associatedData)
        return cipher.doFinal(ciphertext)
    }
}
