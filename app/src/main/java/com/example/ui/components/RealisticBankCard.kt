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
    modifier: Modifier = Modifier
) {
    val meta = remember(card.bankType, card.bankName) {
        IranianBankHelper.getBankMetaByType(card.bankType.ifEmpty { card.bankName })
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.586f)
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
                .padding(18.dp)
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

                // Middle Section: Metallic Chip & Contactless
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Smart Chip Graphic
                    Box(
                        modifier = Modifier
                            .size(width = 42.dp, height = 30.dp)
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

                // Card Number Center Display (Clickable to copy)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.18f))
                        .clickable { onCopyCardNumber(card.cardNumber) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = IranianBankHelper.formatCardNumber(card.cardNumber),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.2.sp,
                        style = TextStyle(textDirection = TextDirection.Ltr)
                    )

                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Card Number",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Bottom Section: Person Name, Account, IBAN & Copy Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Person / Owner Name & Account Number
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = personName,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (card.accountNumber.isNotBlank()) {
                            Text(
                                text = "حساب: ${card.accountNumber}",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        if (card.iban.isNotBlank()) {
                            Text(
                                text = IranianBankHelper.formatIban(card.iban),
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                style = TextStyle(textDirection = TextDirection.Ltr)
                            )
                        }
                    }

                    // Copy IBAN Button
                    if (card.iban.isNotBlank()) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { onCopyIban(card.iban) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "کپی شبا",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
