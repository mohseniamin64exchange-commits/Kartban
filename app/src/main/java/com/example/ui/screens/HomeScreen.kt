package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextButton
import com.example.ui.components.BackupRestoreDialog
import com.example.ui.components.QrScannerDialog
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CardGroupFilter
import com.example.data.PersonSortOption
import com.example.data.PersonWithCards
import com.example.ui.KartYarViewModel
import com.example.ui.components.AvatarView
import com.example.ui.components.RectangularBankLogo

@Composable
fun HomeScreen(
    viewModel: KartYarViewModel,
    onOpenPersonCards: (Int) -> Unit,
    onOpenAddDialog: () -> Unit
) {
    val personsWithCards by viewModel.personsWithCards.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val totalPersons by viewModel.totalPersonCount.collectAsState()
    val totalCards by viewModel.totalCardCount.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val groupFilter by viewModel.groupFilter.collectAsState()
    val selectedPersonIds by viewModel.selectedPersonIds.collectAsState()

    var showBackupDialog by remember { mutableStateOf(false) }
    var showQrScanner by remember { mutableStateOf(false) }
    var showSettingsMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showBackupDialog) {
        BackupRestoreDialog(
            viewModel = viewModel,
            onDismiss = { showBackupDialog = false }
        )
    }

    if (showQrScanner) {
        QrScannerDialog(
            onDismiss = { showQrScanner = false },
            onQrCodeScanned = { payload ->
                viewModel.importQrPayload(payload) {}
            },
            onShowToast = { msg -> viewModel.copyToClipboard("", "", msg) }
        )
    }

    if (showDeleteConfirm) {
        val count = selectedPersonIds.size
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("تأیید حذف مخاطبین", fontWeight = FontWeight.Bold) },
            text = { Text("آیا از حذف $count مخاطب انتخاب‌شده و تمام کارت‌های آن‌ها اطمینان دارید؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSelectedPersons()
                        showDeleteConfirm = false
                    }
                ) {
                    Text("حذف شود")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    Scaffold(
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.Start,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onOpenAddDialog,
                icon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White) },
                text = { Text("افزودن شخص یا کارت", color = Color.White, fontWeight = FontWeight.Bold) },
                containerColor = Color(0xFF0A347A),
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .testTag("add_person_card_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Hero Navy Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = if (isDarkMode) {
                                listOf(Color(0xFF071C45), Color(0xFF020C24))
                            } else {
                                listOf(Color(0xFF164D98), Color(0xFF0D2E69))
                            }
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Column {
                    // Top Bar: Settings / Contextual Multi-Select
                    if (selectedPersonIds.isNotEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            // Right Side: Close Selection
                            IconButton(
                                onClick = { viewModel.clearPersonSelection() },
                                modifier = Modifier.align(Alignment.CenterStart)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "لغو انتخاب", tint = Color.White)
                            }

                            // Center: Selected Count
                            Text(
                                text = "${selectedPersonIds.size} مخاطب انتخاب شد",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Left Side: Select All & Delete
                            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                                IconButton(
                                    onClick = {
                                        if (selectedPersonIds.size == personsWithCards.size) {
                                            viewModel.clearPersonSelection()
                                        } else {
                                            viewModel.selectAllPersons(personsWithCards.map { it.person.id })
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.SelectAll, contentDescription = "انتخاب همه", tint = Color.White)
                                }
                                IconButton(
                                    onClick = { showDeleteConfirm = true }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف انتخاب‌شده‌ها", tint = Color(0xFFEF4444))
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            // Right Side (CenterStart in RTL): Settings Button with Menu
                            Box(modifier = Modifier.align(Alignment.CenterStart)) {
                                IconButton(
                                    onClick = { showSettingsMenu = true },
                                    modifier = Modifier.testTag("settings_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "تنظیمات",
                                        tint = Color.White
                                    )
                                }

                                DropdownMenu(
                                    expanded = showSettingsMenu,
                                    onDismissRequest = { showSettingsMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.SettingsBackupRestore, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("پشتیبان‌گیری و بازیابی")
                                            }
                                        },
                                        onClick = {
                                            showSettingsMenu = false
                                            showBackupDialog = true
                                        },
                                        modifier = Modifier.testTag("menu_backup_restore")
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("اسکن کد QR (دریافت کارت)")
                                            }
                                        },
                                        onClick = {
                                            showSettingsMenu = false
                                            showQrScanner = true
                                        },
                                        modifier = Modifier.testTag("menu_qr_scan")
                                    )
                                }
                            }

                            // Center: Title & Brand Icon
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CreditCard,
                                    contentDescription = null,
                                    tint = Color(0xFF9DC6FF),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "کارت‌یار",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Left Side (CenterEnd in RTL): QR Scan Button + Theme Toggle Button
                            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                                IconButton(
                                    onClick = { showQrScanner = true },
                                    modifier = Modifier.testTag("qr_scan_header_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = "اسکن QR",
                                        tint = Color.White
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.toggleDarkMode() },
                                    modifier = Modifier.testTag("dark_mode_toggle")
                                ) {
                                    Icon(
                                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = "تغییر حالت شب و روز",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = { Text("جستجوی نام، شماره کارت یا شبا", color = Color(0xFFAAB8D0), fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF9EB4DA)) },
                        trailingIcon = if (searchQuery.isNotBlank()) {
                            {
                                IconButton(
                                    onClick = { viewModel.onSearchQueryChange("") },
                                    modifier = Modifier.testTag("clear_search_btn")
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "پاک کردن جستجو", tint = Color.White)
                                }
                            }
                        } else null,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.12f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                            focusedBorderColor = Color(0xFFA9C9FF),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_search_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Group Filters & Sort Dropdown Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Group Filter Chips
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CardGroupFilter.values().forEach { filter ->
                                val isSelected = groupFilter == filter
                                Surface(
                                    onClick = { viewModel.setGroupFilter(filter) },
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.15f),
                                    modifier = Modifier.testTag("filter_tab_${filter.name}")
                                ) {
                                    Text(
                                        text = filter.title,
                                        color = if (isSelected) Color(0xFF0A347A) else Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        // Sort Dropdown Button
                        var sortMenuExpanded by remember { mutableStateOf(false) }
                        Box {
                            Surface(
                                onClick = { sortMenuExpanded = true },
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.testTag("sort_menu_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sort,
                                        contentDescription = "مرتب‌سازی",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = sortOption.title,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = sortMenuExpanded,
                                onDismissRequest = { sortMenuExpanded = false }
                            ) {
                                PersonSortOption.values().forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = option.title, fontSize = 13.sp)
                                                if (sortOption == option) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            viewModel.setSortOption(option)
                                            sortMenuExpanded = false
                                        },
                                        modifier = Modifier.testTag("sort_option_${option.name}")
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats Banner (Total Contacts & Total Cards)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Total Contacts
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF78AEF7),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.People, contentDescription = null, tint = Color(0xFF0B367D))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "$totalPersons",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = "مخاطب", color = Color(0xFFC3CEE1), fontSize = 12.sp)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(36.dp)
                                    .background(Color.White.copy(alpha = 0.25f))
                            )

                            // Total Cards
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF78AEF7),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color(0xFF0B367D))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "$totalCards",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = "کارت بانکی", color = Color(0xFFC3CEE1), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // List of Persons with Cards
            if (personsWithCards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "مخاطب یا کارتی پیدا نشد.\nبرای افزودن کارت روی دکمه زیر بزنید.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(personsWithCards, key = { it.person.id }) { item ->
                        val isSelected = selectedPersonIds.contains(item.person.id)
                        PersonRowItem(
                            item = item,
                            isSelected = isSelected,
                            isSelectionMode = selectedPersonIds.isNotEmpty(),
                            onClick = {
                                if (selectedPersonIds.isNotEmpty()) {
                                    viewModel.togglePersonSelection(item.person.id)
                                } else {
                                    onOpenPersonCards(item.person.id)
                                }
                            },
                            onLongClick = {
                                viewModel.togglePersonSelection(item.person.id)
                            },
                            onTogglePin = { viewModel.togglePersonPinned(item.person) },
                            onToggleSelect = { viewModel.togglePersonSelection(item.person.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PersonRowItem(
    item: PersonWithCards,
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    onTogglePin: () -> Unit,
    onToggleSelect: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .testTag("person_row_${item.person.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Right Section (Child 0 in RTL): Selection Checkbox/Avatar, Name & Card Count
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                if (isSelectionMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleSelect() },
                        modifier = Modifier.testTag("person_checkbox_${item.person.id}")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }

                AvatarView(kind = item.person.kind, size = 48.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.person.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (item.person.isPinned) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "سنجاق شده",
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${item.cards.size} کارت بانکی",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        val defaultCard = item.cards.firstOrNull { it.isDefault }
                        if (defaultCard != null) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "★ ${defaultCard.bankName}",
                                fontSize = 11.sp,
                                color = Color(0xFFD97706),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    if (item.person.notes.isNotBlank()) {
                        Text(
                            text = item.person.notes,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1
                        )
                    }
                }
            }

            // Left Section (Child 1 in RTL): Pin button, Rectangular Bank Badges & Chevron
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onTogglePin,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("pin_person_btn_${item.person.id}")
                ) {
                    Icon(
                        imageVector = if (item.person.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                        contentDescription = if (item.person.isPinned) "برداشتن سنجاق" else "سنجاق کردن مخاطب",
                        tint = if (item.person.isPinned) Color(0xFFD97706) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Display up to 3 bank logos
                val distinctBanks = item.cards.map { it.bankType.ifEmpty { it.bankName } }.distinct().take(3)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    distinctBanks.forEach { bankType ->
                        RectangularBankLogo(bankTypeKey = bankType)
                    }
                    if (item.cards.size > 3) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "+${item.cards.size - 3}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
