package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FormatColorReset
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.AppTheme
import com.example.data.CardGroupFilter
import com.example.data.PersonSortOption
import com.example.ui.KartYarViewModel
import com.example.ui.components.BackupRestoreDialog
import com.example.ui.components.QrScannerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: KartYarViewModel,
    onBack: () -> Unit
) {
    val userSettings by viewModel.userSettings.collectAsState()

    var showThemeDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showResetAppearancesConfirm by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showQrScanner by remember { mutableStateOf(false) }

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

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("حالت برنامه", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    AppTheme.entries.forEach { theme ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateAppTheme(theme)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (userSettings.appTheme == theme),
                                onClick = {
                                    viewModel.updateAppTheme(theme)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = theme.title, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    if (showSortDialog) {
        AlertDialog(
            onDismissRequest = { showSortDialog = false },
            title = { Text("مرتب‌سازی پیش‌فرض", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    PersonSortOption.entries.forEach { option ->
                        val label = when (option) {
                            PersonSortOption.PINNED_FIRST -> "سنجاق‌شده‌ها ابتدا"
                            PersonSortOption.NAME_ASC -> "نام (الفبا)"
                            PersonSortOption.NEWEST -> "جدیدترین"
                            PersonSortOption.OLDEST -> "قدیمی‌ترین"
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateDefaultSortOption(option)
                                    showSortDialog = false
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (userSettings.defaultSortOption == option),
                                onClick = {
                                    viewModel.updateDefaultSortOption(option)
                                    showSortDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = label, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSortDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("فیلتر پیش‌فرض کارت‌ها", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    CardGroupFilter.entries.forEach { filter ->
                        val label = when (filter) {
                            CardGroupFilter.ALL -> "همه کارت‌ها"
                            CardGroupFilter.MY_CARDS -> "کارت‌های من"
                            CardGroupFilter.OTHER_CARDS -> "کارت‌های دیگران"
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateDefaultGroupFilter(filter)
                                    showFilterDialog = false
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (userSettings.defaultGroupFilter == filter),
                                onClick = {
                                    viewModel.updateDefaultGroupFilter(filter)
                                    showFilterDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = label, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    if (showResetAppearancesConfirm) {
        AlertDialog(
            onDismissRequest = { showResetAppearancesConfirm = false },
            title = { Text("بازنشانی ظاهر کارت‌ها", fontWeight = FontWeight.Bold) },
            text = { Text("آیا از بازنشانی ظاهر تمامی کارت‌ها به حالت پیش‌فرض بانک اطمینان دارید؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllCardAppearances()
                        showResetAppearancesConfirm = false
                    }
                ) {
                    Text("بازنشانی شود")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetAppearancesConfirm = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تنظیمات", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section A: ظاهر
            SettingsSectionCard(
                title = "ظاهر",
                icon = Icons.Default.Palette
            ) {
                val themeLabel = when (userSettings.appTheme) {
                    AppTheme.LIGHT -> "روشن"
                    AppTheme.DARK -> "تاریک"
                    AppTheme.SYSTEM -> "سیستم"
                }
                SettingsClickableRow(
                    title = "حالت برنامه",
                    subtitle = themeLabel,
                    onClick = { showThemeDialog = true },
                    testTag = "settings_theme_row"
                )
            }

            // Section B: نمایش
            SettingsSectionCard(
                title = "نمایش",
                icon = Icons.Default.Visibility
            ) {
                val sortLabel = when (userSettings.defaultSortOption) {
                    PersonSortOption.PINNED_FIRST -> "سنجاق‌شده‌ها ابتدا"
                    PersonSortOption.NAME_ASC -> "نام (الفبا)"
                    PersonSortOption.NEWEST -> "جدیدترین"
                    PersonSortOption.OLDEST -> "قدیمی‌ترین"
                }
                SettingsClickableRow(
                    title = "مرتب‌سازی پیش‌فرض",
                    subtitle = sortLabel,
                    onClick = { showSortDialog = true },
                    testTag = "settings_sort_row"
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                val filterLabel = when (userSettings.defaultGroupFilter) {
                    CardGroupFilter.ALL -> "همه کارت‌ها"
                    CardGroupFilter.MY_CARDS -> "کارت‌های من"
                    CardGroupFilter.OTHER_CARDS -> "کارت‌های دیگران"
                }
                SettingsClickableRow(
                    title = "فیلتر پیش‌فرض کارت‌ها",
                    subtitle = filterLabel,
                    onClick = { showFilterDialog = true },
                    testTag = "settings_filter_row"
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                SettingsSwitchRow(
                    title = "نمایش یادداشت در لیست",
                    subtitle = if (userSettings.showNotesInList) "نمایش داده می‌شود" else "پنهان شده است",
                    checked = userSettings.showNotesInList,
                    onCheckedChange = { viewModel.updateShowNotesInList(it) },
                    testTag = "settings_show_notes_switch"
                )
            }

            // Section C: کارت‌ها
            SettingsSectionCard(
                title = "کارت‌ها",
                icon = Icons.Default.CreditCard
            ) {
                SettingsSwitchRow(
                    title = "کارت پیش‌فرض همیشه بالاتر نمایش داده شود",
                    subtitle = if (userSettings.forceDefaultCardTop) "فعال" else "غیرفعال",
                    checked = userSettings.forceDefaultCardTop,
                    onCheckedChange = { viewModel.updateForceDefaultCardTop(it) },
                    testTag = "settings_force_default_top_switch"
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                SettingsActionRow(
                    title = "بازنشانی ظاهر کارت‌ها",
                    subtitle = "بازگرداندن رنگ و ظاهر تمام کارت‌ها به حالت اولیه بانک",
                    actionIcon = Icons.Default.FormatColorReset,
                    onClick = { showResetAppearancesConfirm = true },
                    testTag = "settings_reset_appearance_row"
                )
            }

            // Section D: داده‌ها
            SettingsSectionCard(
                title = "داده‌ها",
                icon = Icons.Default.Storage
            ) {
                SettingsActionRow(
                    title = "پشتیبان‌گیری و بازیابی",
                    subtitle = "خروجی گرفتن از اطلاعات یا بازیابی فایل پشتیبان",
                    actionIcon = Icons.Default.SettingsBackupRestore,
                    onClick = { showBackupDialog = true },
                    testTag = "settings_backup_restore_row"
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                SettingsActionRow(
                    title = "دریافت اطلاعات با QR",
                    subtitle = "اسکن و وارد کردن اطلاعات کارت‌ها از طریق QR",
                    actionIcon = Icons.Default.QrCodeScanner,
                    onClick = { showQrScanner = true },
                    testTag = "settings_qr_row"
                )
            }

            // Section E: درباره
            SettingsSectionCard(
                title = "درباره",
                icon = Icons.Default.Info
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "کارت‌بان",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "نسخه ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "مدیریت آفلاین کارت‌های بانکی و اطلاعات بانکی",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            content()
        }
    }
}

@Composable
private fun SettingsClickableRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Medium, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Medium, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingsActionRow(
    title: String,
    subtitle: String,
    actionIcon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Medium, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Icon(
            imageVector = actionIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
