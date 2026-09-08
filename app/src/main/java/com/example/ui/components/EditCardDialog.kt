package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BankCardEntity
import com.example.data.IranianBankHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCardDialog(
    card: BankCardEntity,
    personName: String,
    onDismiss: () -> Unit,
    onSubmit: (BankCardEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedBank by remember {
        mutableStateOf(
            IranianBankHelper.getBankMetaByType(card.bankType.ifEmpty { card.bankName })
        )
    }
    var bankDropdownExpanded by remember { mutableStateOf(false) }

    var cardNumber by remember { mutableStateOf(card.cardNumber) }
    var accountNumber by remember { mutableStateOf(card.accountNumber) }
    var iban by remember { mutableStateOf(if (card.iban.isNotBlank()) card.iban else "IR") }
    var cardKind by remember { mutableStateOf(card.cardKind) } // customer or personal

    var cvv2 by remember { mutableStateOf(card.cvv2) }
    var expiryDate by remember { mutableStateOf(card.expiryDate) }
    var cardNotes by remember { mutableStateOf(card.notes) }
    var isDefault by remember { mutableStateOf(card.isDefault) }

    // Auto-detect bank when card number changes and reaches 6 digits
    LaunchedEffect(cardNumber) {
        val digits = IranianBankHelper.normalizeCardNumber(cardNumber)
        if (digits.length >= 6) {
            val detected = IranianBankHelper.detectBankByCardNumber(digits)
            if (detected != null) {
                selectedBank = detected
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
                .testTag("edit_card_bottom_sheet"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Navy Gradient Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF164D98), Color(0xFF0D2E69))
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color(0xFF9DC6FF),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ویرایش کارت ($personName)",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("edit_card_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = Color.White
                        )
                    }
                }
            }

            // Bank Selector Dropdown
            Text(
                text = "بانک صادرکننده:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { bankDropdownExpanded = true }
                        .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RectangularBankLogo(bankTypeKey = selectedBank.typeKey)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = selectedBank.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                    }
                }

                DropdownMenu(
                    expanded = bankDropdownExpanded,
                    onDismissRequest = { bankDropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    IranianBankHelper.defaultBanks.forEach { bank ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RectangularBankLogo(bankTypeKey = bank.typeKey)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(bank.name)
                                }
                            },
                            onClick = {
                                selectedBank = bank
                                bankDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Card Number Input
            val normalizedCard = IranianBankHelper.normalizeCardNumber(cardNumber)
            val cardValidation = IranianBankHelper.validateCardNumber(normalizedCard)
            val isCardLength16 = normalizedCard.length == 16
            val isCardInvalid = isCardLength16 && !cardValidation.isValid

            val cleanIban = IranianBankHelper.normalizeIban(iban)
            val isIbanTyped = cleanIban.isNotEmpty() && cleanIban != "IR"
            val ibanValidation = IranianBankHelper.validateIban(cleanIban, isOptional = true)
            val isIbanInvalid = isIbanTyped && cleanIban.length == 26 && !ibanValidation.isValid

            OutlinedTextField(
                value = cardNumber,
                onValueChange = { input ->
                    val clean = IranianBankHelper.normalizeCardNumber(input).take(16)
                    cardNumber = clean
                    if (clean.length >= 6) {
                        val detected = IranianBankHelper.detectBankByCardNumber(clean)
                        if (detected != null) {
                            selectedBank = detected
                        }
                    }
                },
                label = { Text("شماره ۱۶ رقمی کارت *") },
                placeholder = { Text("6037-xxxx-xxxx-xxxx") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = Color(0xFF164D98)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = isCardInvalid,
                supportingText = {
                    if (isCardInvalid) {
                        Text("شماره کارت معتبر نیست", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    } else if (normalizedCard.length in 1..15) {
                        Text("${normalizedCard.length} از ۱۶ رقم وارد شده", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("edit_card_number_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF164D98),
                    focusedLabelColor = Color(0xFF164D98)
                )
            )

            // Account Number Input
            OutlinedTextField(
                value = accountNumber,
                onValueChange = { accountNumber = IranianBankHelper.normalizeDigits(it) },
                label = { Text("شماره حساب (اختیاری)") },
                placeholder = { Text("مثلاً 0102030405...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color(0xFF164D98)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("edit_account_number_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF164D98),
                    focusedLabelColor = Color(0xFF164D98)
                )
            )

            // IBAN Input
            OutlinedTextField(
                value = iban,
                onValueChange = { input ->
                    val clean = IranianBankHelper.normalizeIban(input).take(26)
                    iban = clean
                },
                label = { Text("شماره شبا (اختیاری)") },
                placeholder = { Text("IR000000000000000000000000") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = Color(0xFF164D98)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                isError = isIbanInvalid,
                supportingText = {
                    if (isIbanInvalid) {
                        Text("شماره شبا معتبر نیست", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    } else if (isIbanTyped && cleanIban.length < 26) {
                        Text("${cleanIban.length} از ۲۶ نویسه وارد شده", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("edit_iban_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF164D98),
                    focusedLabelColor = Color(0xFF164D98)
                )
            )

            // Card Notes Input
            OutlinedTextField(
                value = cardNotes,
                onValueChange = { cardNotes = it },
                label = { Text("یادداشت این کارت (اختیاری)") },
                placeholder = { Text("مثلاً حساب حقوق، پس‌انداز، قسط...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Notes,
                        contentDescription = null,
                        tint = Color(0xFF164D98)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("edit_card_notes_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF164D98),
                    focusedLabelColor = Color(0xFF164D98)
                )
            )

            // Card Kind Selector (Customer vs Personal)
            Text(
                text = "نوع استفاده از کارت:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { cardKind = "customer" }
                        .testTag("edit_card_kind_customer")
                ) {
                    RadioButton(
                        selected = cardKind == "customer",
                        onClick = { cardKind = "customer" },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF164D98))
                    )
                    Text("کارت مشتری / دیگران", fontSize = 13.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { cardKind = "personal" }
                        .testTag("edit_card_kind_personal")
                ) {
                    RadioButton(
                        selected = cardKind == "personal",
                        onClick = { cardKind = "personal" },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF164D98))
                    )
                    Text("کارت شخصی من", fontSize = 13.sp)
                }
            }

            // Sensitive Fields (Only for Personal Cards)
            AnimatedVisibility(visible = cardKind == "personal") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFEF2F2), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "اطلاعات حساس (فقط برای کارت‌های شخصی مجاز است)",
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = cvv2,
                            onValueChange = { cvv2 = IranianBankHelper.normalizeCardNumber(it).take(4) },
                            label = { Text("CVV2") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("edit_cvv2_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = expiryDate,
                            onValueChange = { expiryDate = it.take(5) },
                            label = { Text("انقضا (ماه/سال)") },
                            placeholder = { Text("04/08") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("edit_expiry_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }
                }
            }

            // Default Card Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { isDefault = !isDefault }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isDefault,
                    onCheckedChange = { isDefault = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFD97706)
                    ),
                    modifier = Modifier.testTag("edit_card_default_checkbox")
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = if (isDefault) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = if (isDefault) Color(0xFFD97706) else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "تنظیم به عنوان کارت پیش‌فرض این شخص",
                    fontSize = 14.sp,
                    fontWeight = if (isDefault) FontWeight.Bold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action Buttons: Equal Width, Save: Navy, Cancel: Red
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("edit_card_cancel_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC2626),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "انصراف",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                val canSubmit = cardValidation.isValid && ibanValidation.isValid
                Button(
                    onClick = {
                        val cardVal = IranianBankHelper.validateCardNumber(cardNumber)
                        val ibanVal = IranianBankHelper.validateIban(iban, isOptional = true)
                        if (cardVal.isValid && ibanVal.isValid) {
                            val normalized = IranianBankHelper.normalizeCardNumber(cardNumber)
                            onSubmit(
                                card.copy(
                                    bankName = selectedBank.name,
                                    bankType = selectedBank.typeKey,
                                    cardNumber = normalized,
                                    accountNumber = IranianBankHelper.normalizeDigits(accountNumber),
                                    iban = IranianBankHelper.normalizeIban(iban),
                                    cardKind = cardKind,
                                    cvv2 = if (cardKind == "personal") cvv2 else "",
                                    expiryDate = if (cardKind == "personal") expiryDate else "",
                                    cardColorStart = selectedBank.colorStart.value.toString(16),
                                    cardColorEnd = selectedBank.colorEnd.value.toString(16),
                                    isDefault = isDefault,
                                    notes = cardNotes.trim()
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("edit_card_save_btn"),
                    shape = RoundedCornerShape(12.dp),
                    enabled = canSubmit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0A347A),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "ذخیره تغییرات",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
