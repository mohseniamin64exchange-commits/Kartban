package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BankCardEntity
import com.example.data.CardImageGenerator
import com.example.data.IranianBankHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareOptionsModal(
    personName: String,
    selectedCards: List<BankCardEntity>,
    onDismiss: () -> Unit,
    onShowToast: (String) -> Unit,
    onShareQr: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()

    fun buildShareText(): String {
        return selectedCards.joinToString("\n\n") { card ->
            val formattedCard = IranianBankHelper.formatCardNumber(card.cardNumber)
            val formattedIban = IranianBankHelper.formatIban(card.iban)
            buildString {
                append("${card.bankName}\n")
                append("نام صاحب کارت: $personName\n")
                append("شماره کارت: $formattedCard\n")
                if (card.iban.isNotBlank()) append("شماره شبا: $formattedIban\n")
                if (card.accountNumber.isNotBlank()) append("شماره حساب: ${card.accountNumber}")
            }
        }
    }

    fun sendSms() {
        val shareText = buildShareText()
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:")
                putExtra("sms_body", shareText)
            }
            context.startActivity(intent)
            onShowToast("برنامه پیامک باز شد")
        } catch (e: Exception) {
            // Fallback to chooser
            val fallback = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(fallback, "ارسال پیامک"))
        }
        onDismiss()
    }

    fun shareGeneric() {
        val shareText = buildShareText()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(intent, "اشتراک‌گذاری اطلاعات کارت"))
        onDismiss()
    }

    fun shareImage() {
        val cardToShare = selectedCards.firstOrNull()
        if (cardToShare != null) {
            val success = CardImageGenerator.shareCardImage(context, cardToShare, personName)
            if (success) {
                onShowToast("تصویر کارت برای اشتراک‌گذاری آماده شد")
            } else {
                onShowToast("خطا در ایجاد تصویر کارت")
            }
        }
        onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .testTag("share_options_modal"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "روش ارسال مشخصات کارت",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "${selectedCards.size} کارت انتخاب شده برای $personName",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 1. SMS Share Option
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { sendSms() },
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF16A34A),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Message, contentDescription = null, tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "ارسال پیامک (SMS)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = "فقط مشخصات کارت به‌صورت متن امن",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 2. Messenger / Apps Share Option
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { shareGeneric() },
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF2563EB),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "ارسال به پیام‌رسان‌ها (تلگرام، بله، واتساپ)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = "ارسال متن کامل مشخصات کارت به برنامه‌ها",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 3. Graphic Card Image Share Option (uses customized colors)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { shareImage() }
                    .testTag("share_card_image_btn"),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF7C3AED),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "ارسال تصویر گرافیکی کارت", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = "با رنگ، طرح و هویت بصری کارت (شخصی یا بانک)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 4. QR Code Share Option
            if (onShareQr != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDismiss()
                            onShareQr()
                        }
                        .testTag("share_card_qr_btn"),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFD97706),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "اشتراک‌گذاری با کد QR", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                text = "انتقال مستقیم اطلاعات به برنامه کارت‌بان بدون اینترنت",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("انصراف")
            }
        }
    }
}
