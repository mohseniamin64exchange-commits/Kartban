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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.IranianBankHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPersonAndCardDialog(
    initialPersonName: String = "",
    onDismiss: () -> Unit,
    onSubmit: (
        personName: String,
        personKind: String,
        bankName: String,
        bankType: String,
        cardNumber: String,
        accountNumber: String,
        iban: String,
        cardKind: String,
        cvv2: String,
        expiryDate: String
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var personName by remember { mutableStateOf(initialPersonName) }
    var personKind by remember { mutableStateOf("man") } // man, woman, store, company
    var selectedBank by remember { mutableStateOf(IranianBankHelper.defaultBanks.first()) }
    var bankDropdownExpanded by remember { mutableStateOf(false) }

    var cardNumber by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var iban by remember { mutableStateOf("IR") }
    var cardKind by remember { mutableStateOf("customer") } // customer or personal

    var cvv2 by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }

    // Auto-detect bank when card number reaches 6 digits
    LaunchedEffect(cardNumber) {
        val digits = cardNumber.filter { it.isDigit() }
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
                .testTag("add_card_bottom_sheet"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Navy Gradient Header Matching Main Screen Palette
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
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = Color(0xFF9DC6FF),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "افزودن شخص و کارت جدید",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }
            }

            val navyFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF164D98),
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                focusedContainerColor = Color(0xFF164D98).copy(alpha = 0.04f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedLabelColor = Color(0xFF164D98)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Person Name Input
                item {
                    OutlinedTextField(
                        value = personName,
                        onValueChange = { personName = it },
                        label = { Text("نام و نام خانوادگی / نام شرکت") },
                        placeholder = { Text("مثلاً علی رضایی یا فروشگاه آفتاب") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF164D98)) },
                        colors = navyFieldColors,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_person_name_input"),
                        singleLine = true
                    )
                }

                // 2. Person Kind Selection Chips (Man, Woman, Store, Company)
                item {
                    Text(
                        text = "نوع مخاطب:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "man" to ("مرد" to "👨"),
                            "woman" to ("زن" to "👩"),
                            "store" to ("فروشگاه" to "🏪"),
                            "company" to ("شرکت" to "🏢")
                        ).forEach { (key, pair) ->
                            val isSelected = personKind == key
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFF164D98) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF164D98) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { personKind = key }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = pair.second, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = pair.first,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Bank Picker Dropdown
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedBank.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("انتخاب بانک") },
                            leadingIcon = {
                                Box(modifier = Modifier.padding(start = 8.dp)) {
                                    RectangularBankLogo(bankTypeKey = selectedBank.typeKey)
                                }
                            },
                            trailingIcon = {
                                IconButton(onClick = { bankDropdownExpanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Bank", tint = Color(0xFF164D98))
                                }
                            },
                            colors = navyFieldColors,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { bankDropdownExpanded = true },
                            singleLine = true
                        )

                        DropdownMenu(
                            expanded = bankDropdownExpanded,
                            onDismissRequest = { bankDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            IranianBankHelper.defaultBanks.forEach { bank ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RectangularBankLogo(bankTypeKey = bank.typeKey)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(text = bank.name, fontWeight = FontWeight.Medium)
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
                }

                // 4. Card Number
                item {
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }.take(16)
                            cardNumber = clean
                        },
                        label = { Text("شماره ۱۶ رقمی کارت") },
                        placeholder = { Text("۶۱۰۴ ۳۳۷۸ ۹۰۱۲ ۳۴۵۶") },
                        leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color(0xFF164D98)) },
                        colors = navyFieldColors,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_card_number_input"),
                        singleLine = true
                    )
                }

                // 5. Account Number
                item {
                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = { accountNumber = it.filter { ch -> ch.isDigit() } },
                        label = { Text("شماره حساب") },
                        placeholder = { Text("۱۲۳۴۵۶۷۸۹") },
                        leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color(0xFF164D98)) },
                        colors = navyFieldColors,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // 6. IBAN
                item {
                    OutlinedTextField(
                        value = iban,
                        onValueChange = { input ->
                            var clean = input.uppercase().filter { it.isLetterOrDigit() }
                            if (!clean.startsWith("IR")) {
                                clean = "IR$clean"
                            }
                            iban = clean.take(26)
                        },
                        label = { Text("شماره شبا (IBAN)") },
                        placeholder = { Text("IR120170000000123456789001") },
                        leadingIcon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFF164D98)) },
                        colors = navyFieldColors,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // 7. Card Type Cards ("مشتری / مخاطب" vs "کارت شخصی")
                item {
                    Text(
                        text = "نوع کارت:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Customer Card Choice
                        val isCustomer = cardKind == "customer"
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isCustomer) Color(0xFF0F3875).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(
                                width = if (isCustomer) 2.dp else 1.dp,
                                color = if (isCustomer) Color(0xFF164D98) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    cardKind = "customer"
                                    cvv2 = ""
                                    expiryDate = ""
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = null,
                                    tint = if (isCustomer) Color(0xFF164D98) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "کارت مخاطب",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCustomer) Color(0xFF164D98) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "اشتراک سریع",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Personal Card Choice
                        val isPersonal = cardKind == "personal"
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isPersonal) Color(0xFF07815D).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(
                                width = if (isPersonal) 2.dp else 1.dp,
                                color = if (isPersonal) Color(0xFF07815D) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { cardKind = "personal" }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isPersonal) Color(0xFF07815D) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "کارت شخصی",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPersonal) Color(0xFF07815D) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "ذخیره CVV2",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // 8. CVV2 & Expiry Date (Locked if customer card!)
                item {
                    val isPersonal = cardKind == "personal"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = if (isPersonal) cvv2 else "",
                            onValueChange = { if (isPersonal) cvv2 = it.filter { c -> c.isDigit() }.take(4) },
                            enabled = isPersonal,
                            label = { Text("CVV2") },
                            placeholder = { Text(if (isPersonal) "۳ یا ۴ رقم" else "قفل شده 🔒") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isPersonal) Color(0xFF07815D) else Color.Gray
                                )
                            },
                            colors = navyFieldColors,
                            shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = if (isPersonal) expiryDate else "",
                            onValueChange = { if (isPersonal) expiryDate = it.take(5) },
                            enabled = isPersonal,
                            label = { Text("تاریخ انقضا") },
                            placeholder = { Text(if (isPersonal) "05/08" else "قفل شده 🔒") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isPersonal) Color(0xFF07815D) else Color.Gray
                                )
                            },
                            colors = navyFieldColors,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                // 9. Privacy Policy Notice
                item {
                    Surface(
                        color = Color(0xFF164D98).copy(alpha = 0.08f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFF164D98).copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFF164D98),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "اطلاعیه امنیت: CVV2 و تاریخ انقضا فقط برای کارت شخصی قابل ورود هستند و هرگز در ارسال پیامک یا تصویر قرار نمی‌گیرند.",
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // 10. Submit & Cancel Buttons (Equal Size)
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Save Button (Navy Background)
                        Button(
                            onClick = {
                                if (personName.isNotBlank() && cardNumber.length >= 16) {
                                    onSubmit(
                                        personName,
                                        personKind,
                                        selectedBank.name,
                                        selectedBank.typeKey,
                                        cardNumber,
                                        accountNumber,
                                        iban,
                                        cardKind,
                                        cvv2,
                                        expiryDate
                                    )
                                    onDismiss()
                                }
                            },
                            enabled = personName.isNotBlank() && cardNumber.length >= 16,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0A347A),
                                contentColor = Color.White,
                                disabledContainerColor = Color(0xFF0A347A).copy(alpha = 0.4f),
                                disabledContentColor = Color.White.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("save_person_card_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("ذخیره کارت", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        // Cancel Button (Red Background)
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDC2626),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("cancel_person_card_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("انصراف", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
