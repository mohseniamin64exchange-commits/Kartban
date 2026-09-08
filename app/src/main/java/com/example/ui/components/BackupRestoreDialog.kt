package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.BackupParseResult
import com.example.data.BackupPayload
import com.example.ui.KartYarViewModel

@Composable
fun BackupRestoreDialog(
    viewModel: KartYarViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var pendingPayload by remember { mutableStateOf<BackupPayload?>(null) }
    var pendingPersonCount by remember { mutableStateOf(0) }
    var pendingCardCount by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Backup Create Document launcher
    val createDocLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.exportBackupToUri(context, uri) { success, msg ->
                if (!success) {
                    errorMessage = msg
                }
            }
        }
    }

    // Restore Open Document launcher
    val openDocLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.parseBackupFromUri(context, uri) { result ->
                when (result) {
                    is BackupParseResult.Success -> {
                        pendingPayload = result.payload
                        pendingPersonCount = result.personCount
                        pendingCardCount = result.cardCount
                    }
                    is BackupParseResult.Error -> {
                        errorMessage = result.message
                    }
                }
            }
        }
    }

    // Restore Confirmation Dialog
    if (pendingPayload != null) {
        val payload = pendingPayload!!
        AlertDialog(
            onDismissRequest = { pendingPayload = null },
            title = {
                Text("تأیید بازیابی پشتیبان", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "فایل پشتیبان با موفقیت بررسي شد:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "تعداد مخاطبین: $pendingPersonCount", fontWeight = FontWeight.Bold)
                            Text(text = "تعداد کارت‌ها: $pendingCardCount", fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = "اطلاعات موجود حذف نخواهند شد و کارت‌های تکراری به‌طور خودکار شناسایی و رد می‌شوند.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.restoreBackupPayload(payload) {
                            pendingPayload = null
                            onDismiss()
                        }
                    }
                ) {
                    Text("بازیابی و ادغام")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingPayload = null }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Error Alert
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("خطا در پشتیبان‌گیری/بازیابی", fontWeight = FontWeight.Bold) },
            text = { Text(errorMessage!!) },
            confirmButton = {
                TextButton(onClick = { errorMessage = null }) {
                    Text("تأیید")
                }
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("backup_restore_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SettingsBackupRestore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "پشتیبان‌گیری و بازیابی",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "بستن")
                    }
                }

                Text(
                    text = "می‌توانید اطلاعات کارت‌های خود را در قالب یک فایل پشتیبان ذخیره کرده یا آن را در دستگاه دیگری بازیابی کنید.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Export Backup Option
                Button(
                    onClick = {
                        val fileName = "kartban_backup_${System.currentTimeMillis() / 1000}.kartban"
                        createDocLauncher.launch(fileName)
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("export_backup_btn")
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("پشتیبان‌گیری و ذخیره فایل", fontWeight = FontWeight.Bold)
                }

                // Import Restore Option
                OutlinedButton(
                    onClick = {
                        openDocLauncher.launch(arrayOf("*/*", "application/json", "application/octet-stream"))
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("import_restore_btn")
                ) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("بازیابی از فایل پشتیبان", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
