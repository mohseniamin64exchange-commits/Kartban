package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BankCardEntity
import com.example.data.PersonEntity
import com.example.ui.KartYarViewModel
import com.example.ui.components.AvatarView
import com.example.ui.components.EditCardDialog
import com.example.ui.components.EditPersonDialog
import com.example.ui.components.RealisticBankCard
import com.example.ui.components.RectangularBankLogo
import com.example.ui.components.ShareOptionsModal

@Composable
fun PersonCardsScreen(
    personId: Int,
    viewModel: KartYarViewModel,
    onBack: () -> Unit,
    onOpenAddCardDialog: () -> Unit
) {
    val personsWithCards by viewModel.personsWithCards.collectAsState()
    val personWithCards = personsWithCards.firstOrNull { it.person.id == personId }

    val context = LocalContext.current
    val selectedCardIds = remember { mutableStateListOf<Int>() }
    var showShareModal by remember { mutableStateOf(false) }

    var showEditPersonDialog by remember { mutableStateOf(false) }
    var cardToEdit by remember { mutableStateOf<BankCardEntity?>(null) }
    var showDeletePersonConfirm by remember { mutableStateOf(false) }
    var cardToDelete by remember { mutableStateOf<BankCardEntity?>(null) }

    if (personWithCards == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("مخاطب یافت نشد.")
        }
        return
    }

    val person = personWithCards.person
    val cards = personWithCards.cards

    Scaffold(
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.Start,
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Add Card FAB (Right side in RTL)
                ExtendedFloatingActionButton(
                    onClick = onOpenAddCardDialog,
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Card", tint = Color.White) },
                    text = {
                        Text(
                            "افزودن کارت",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    containerColor = Color(0xFF07815D),
                    modifier = Modifier.testTag("add_card_to_person_fab")
                )

                // 2. Share Selected Cards FAB (Left side in RTL, permanently visible with dynamic count)
                val count = selectedCardIds.size
                ExtendedFloatingActionButton(
                    onClick = {
                        if (count > 0) {
                            showShareModal = true
                        } else {
                            Toast.makeText(context, "لطفاً ابتدا حداقل یک کارت را انتخاب کنید", Toast.LENGTH_SHORT).show()
                        }
                    },
                    icon = { Icon(Icons.Default.Share, contentDescription = null, tint = Color.White) },
                    text = {
                        Text(
                            "ارسال ($count)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    containerColor = if (count > 0) Color(0xFF1475E8) else Color(0xFF1475E8).copy(alpha = 0.7f),
                    modifier = Modifier.testTag("share_selected_cards_fab")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header Profile Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF0D3F91), Color(0xFF041B50))
                        )
                    )
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("person_cards_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { showEditPersonDialog = true },
                                modifier = Modifier.testTag("edit_person_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "ویرایش مخاطب",
                                    tint = Color.White
                                )
                            }

                            IconButton(
                                onClick = { showDeletePersonConfirm = true },
                                modifier = Modifier.testTag("delete_person_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف مخاطب",
                                    tint = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AvatarView(kind = person.kind, size = 64.dp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = person.name,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${cards.size} کارت بانکی",
                                color = Color(0xFFD4E1F6),
                                fontSize = 13.sp
                            )

                            if (person.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = Color.White.copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Notes,
                                            contentDescription = null,
                                            tint = Color(0xFF9DC6FF),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = person.notes,
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            val distinctBankTypes = cards.map { it.bankType.ifEmpty { it.bankName } }.distinct()
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                distinctBankTypes.forEach { bankType ->
                                    RectangularBankLogo(bankTypeKey = bankType)
                                }
                            }
                        }
                    }
                }
            }

            // Cards List
            if (cards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "هیچ کارتی برای این شخص ثبت نشده است.\nبرای افزودن روی دکمه + بزنید.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(cards, key = { it.id }) { card ->
                        val isSelected = selectedCardIds.contains(card.id)

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            RealisticBankCard(
                                card = card,
                                personName = person.name,
                                isSelected = isSelected,
                                onToggleSelect = { checked ->
                                    if (checked) {
                                        if (!selectedCardIds.contains(card.id)) selectedCardIds.add(card.id)
                                    } else {
                                        selectedCardIds.remove(card.id)
                                    }
                                },
                                onCopyIban = { iban ->
                                    viewModel.copyToClipboard("IBAN", iban, "شماره شبا کپی شد")
                                },
                                onCopyCardNumber = { cardNumber ->
                                    viewModel.copyToClipboard("Card Number", cardNumber, "شماره کارت کپی شد")
                                },
                                onCopyAccountNumber = { accNumber ->
                                    viewModel.copyToClipboard("Account Number", accNumber, "شماره حساب کپی شد")
                                }
                            )

                            // Card Action Bar: Default Star / Edit / Delete
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Default Card Toggle / Indicator
                                    if (card.isDefault) {
                                        Surface(
                                            color = Color(0xFFD97706).copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = Color(0xFFD97706),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "کارت پیش‌فرض",
                                                    color = Color(0xFFD97706),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { viewModel.setDefaultCard(person.id, card.id) }
                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.StarBorder,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "تنظیم به عنوان پیش‌فرض",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    // Action Buttons: Edit and Delete
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = { cardToEdit = card },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .testTag("edit_card_btn_${card.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "ویرایش کارت",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = { cardToDelete = card },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .testTag("delete_card_btn_${card.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "حذف کارت",
                                                tint = Color(0xFFDC2626),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Share Options BottomSheet Modal
    if (showShareModal) {
        val selectedCards = cards.filter { selectedCardIds.contains(it.id) }
        ShareOptionsModal(
            personName = person.name,
            selectedCards = selectedCards,
            onDismiss = { showShareModal = false },
            onShowToast = { msg ->
                viewModel.copyToClipboard("Share", "", msg)
            }
        )
    }

    // Edit Person Dialog
    if (showEditPersonDialog) {
        EditPersonDialog(
            person = person,
            onDismiss = { showEditPersonDialog = false },
            onSubmit = { updatedPerson ->
                viewModel.updatePerson(updatedPerson)
                showEditPersonDialog = false
            }
        )
    }

    // Edit Card Dialog
    cardToEdit?.let { card ->
        EditCardDialog(
            card = card,
            personName = person.name,
            onDismiss = { cardToEdit = null },
            onSubmit = { updatedCard ->
                viewModel.updateCard(updatedCard)
                cardToEdit = null
            }
        )
    }

    // Delete Person Confirmation Dialog
    if (showDeletePersonConfirm) {
        AlertDialog(
            onDismissRequest = { showDeletePersonConfirm = false },
            title = { Text("حذف مخاطب", fontWeight = FontWeight.Bold) },
            text = { Text("آیا از حذف «${person.name}» و تمامی کارت‌های آن اطمینان دارید؟ این عملیات قابل بازگشت نیست.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePerson(person)
                        showDeletePersonConfirm = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("حذف", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePersonConfirm = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Delete Card Confirmation Dialog
    cardToDelete?.let { card ->
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
            title = { Text("حذف کارت بانکی", fontWeight = FontWeight.Bold) },
            text = { Text("آیا از حذف کارت ${card.bankName} به شماره ${card.cardNumber} اطمینان دارید؟") },
            confirmButton = {
                Button(
                    onClick = {
                        selectedCardIds.remove(card.id)
                        viewModel.deleteCard(card)
                        cardToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("حذف", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { cardToDelete = null }) {
                    Text("انصراف")
                }
            }
        )
    }
}
