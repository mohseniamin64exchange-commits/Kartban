package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.AppTheme
import com.example.ui.components.AddPersonAndCardDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PersonCardsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme

@Composable
fun MainScreen(viewModel: KartYarViewModel) {
    val userSettings by viewModel.userSettings.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val toastEvent by viewModel.toastEvent.collectAsState()

    val darkTheme = when (userSettings.appTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var addDialogInitialPersonName by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastEvent) {
        when (val event = toastEvent) {
            is UiToastEvent.Show -> {
                snackbarHostState.showSnackbar(event.message)
                viewModel.clearToast()
            }
            null -> {}
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MyApplicationTheme(darkTheme = darkTheme) {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "ScreenTransition"
                    ) { screen ->
                        when (screen) {
                            is AppScreen.Home -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onOpenPersonCards = { pId ->
                                        viewModel.navigateToPersonCards(pId)
                                    },
                                    onOpenAddDialog = {
                                        addDialogInitialPersonName = ""
                                        showAddDialog = true
                                    }
                                )
                            }
                            is AppScreen.PersonCards -> {
                                PersonCardsScreen(
                                    personId = screen.personId,
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateToHome() },
                                    onOpenAddCardDialog = {
                                        val currentPerson = viewModel.personsWithCards.value.firstOrNull { it.person.id == screen.personId }
                                        addDialogInitialPersonName = currentPerson?.person?.name ?: ""
                                        showAddDialog = true
                                    }
                                )
                            }
                            is AppScreen.Settings -> {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateToHome() }
                                )
                            }
                        }
                    }

                    if (showAddDialog) {
                        AddPersonAndCardDialog(
                            initialPersonName = addDialogInitialPersonName,
                            onDismiss = { showAddDialog = false },
                            onSubmit = { personName, personKind, personNotes, bankName, bankType, cardNumber, accountNumber, iban, cardKind, cvv2, expiryDate, cardNotes, isDefault ->
                                viewModel.addPersonAndCard(
                                    personName = personName,
                                    personKind = personKind,
                                    personNotes = personNotes,
                                    bankName = bankName,
                                    bankType = bankType,
                                    cardNumber = cardNumber,
                                    accountNumber = accountNumber,
                                    iban = iban,
                                    cardKind = cardKind,
                                    cvv2 = cvv2,
                                    expiryDate = expiryDate,
                                    cardNotes = cardNotes,
                                    isDefault = isDefault
                                )
                                showAddDialog = false
                            }
                        )
                    }
                }
            }
        }
    }
}
