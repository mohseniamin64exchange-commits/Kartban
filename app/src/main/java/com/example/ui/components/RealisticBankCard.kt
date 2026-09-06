package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BankCardEntity
import com.example.data.IranianBankHelper

@Composable
fun RealisticBankCard(
    card: BankCardEntity,
    personName: String,
    isSelected: Boolean,
    onToggleSelect: (Boolean) -> Unit,
    onCopyIban: (String) -> Unit,
    onCopyCardNumber: (String) -> Unit,
    onCopyAccountNumber: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val meta = remember(card.bankType, card.bankName) {
        IranianBankHelper.getBankMetaByType(card.bankType.ifEmpty { card.bankName })
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.45f)
            .shadow(
                elevation = if (isSelected) 14.dp else 8.dp,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("realistic_card_${card.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(meta.colorStart, meta.colorEnd)
                    )
                )
                .padding(16.dp)
        ) {
            // Background subtle circles overlay
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .align(Alignment.TopStart)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header: Checkbox & Bank Name / Emblem
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Right Header: Bank Emblem & Bank Titles
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = meta.logoText,
                                    color = meta.colorStart,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = card.bankName.ifEmpty { meta.name },
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = meta.enName,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 8.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }

                    // Left Checkbox for multi-select
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = onToggleSelect,
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.White,
                            checkmarkColor = meta.colorStart,
                            uncheckedColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("card_checkbox_${card.id}")
                    )
                }

                // Middle Section: Metallic Chip & Card Kind
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Smart Chip Graphic
                    Box(
                        modifier = Modifier
                            .size(width = 38.dp, height = 26.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFFFDE047), Color(0xFFCA8A04))
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    )

                    if (card.cardKind == "personal") {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "کارت شخصی",
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Stacked Numbers Section (Card Number, Account Number, IBAN)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    // 1. Card Number Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.22f))
                            .clickable { onCopyCardNumber(card.cardNumber) }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = IranianBankHelper.formatCardNumber(card.cardNumber),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.1.sp,
                            style = TextStyle(textDirection = TextDirection.Ltr)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("کارت", color = Color.White.copy(alpha = 0.65f), fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Card Number",
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    // 2. Account Number Row
                    if (card.accountNumber.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.22f))
                                .clickable {
                                    if (onCopyAccountNumber != null) {
                                        onCopyAccountNumber(card.accountNumber)
                                    } else {
                                        onCopyCardNumber(card.accountNumber)
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = card.accountNumber,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace,
                                style = TextStyle(textDirection = TextDirection.Ltr)
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("حساب", color = Color.White.copy(alpha = 0.65f), fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Account Number",
                                    tint = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }

                    // 3. IBAN / Sheba Row
                    if (card.iban.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.22f))
                                .clickable { onCopyIban(card.iban) }
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = IranianBankHelper.formatIban(card.iban),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace,
                                style = TextStyle(textDirection = TextDirection.Ltr)
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("شبا", color = Color.White.copy(alpha = 0.65f), fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy IBAN",
                                    tint = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }

                // Bottom Section: Person Name & Expiry/CVV2 if personal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = personName,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (card.cardKind == "personal" && (card.cvv2.isNotBlank() || card.expiryDate.isNotBlank())) {
                        Text(
                            text = listOfNotNull(
                                if (card.cvv2.isNotBlank()) "CVV2: ${card.cvv2}" else null,
                                if (card.expiryDate.isNotBlank()) "Exp: ${card.expiryDate}" else null
                            ).joinToString(" | "),
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
