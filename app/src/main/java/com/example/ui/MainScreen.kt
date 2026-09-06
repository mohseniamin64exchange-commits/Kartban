package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import com.example.ui.components.AddPersonAndCardDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PersonCardsScreen
import com.example.ui.theme.MyApplicationTheme

@Composable
fun MainScreen(viewModel: KartYarViewModel) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val selectedPersonId by viewModel.selectedPersonId.collectAsState()
    val toastEvent by viewModel.toastEvent.collectAsState()

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
        MyApplicationTheme(darkTheme = isDarkMode) {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = selectedPersonId,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "ScreenTransition"
                    ) { personId ->
                        if (personId == null) {
                            HomeScreen(
                                viewModel = viewModel,
                                onOpenPersonCards = { pId ->
                                    viewModel.selectPerson(pId)
                                },
                                onOpenAddDialog = {
                                    addDialogInitialPersonName = ""
                                    showAddDialog = true
                                }
                            )
                        } else {
                            PersonCardsScreen(
                                personId = personId,
                                viewModel = viewModel,
                                onBack = { viewModel.selectPerson(null) },
                                onOpenAddCardDialog = {
                                    // Pre-fill person name if known
                                    val currentPerson = viewModel.personsWithCards.value.firstOrNull { it.person.id == personId }
                                    addDialogInitialPersonName = currentPerson?.person?.name ?: ""
                                    showAddDialog = true
                                }
                            )
                        }
                    }

                    if (showAddDialog) {
                        AddPersonAndCardDialog(
                            initialPersonName = addDialogInitialPersonName,
                            onDismiss = { showAddDialog = false },
                            onSubmit = { personName, personKind, bankName, bankType, cardNumber, accountNumber, iban, cardKind, cvv2, expiryDate ->
                                viewModel.addPersonAndCard(
                                    personName = personName,
                                    personKind = personKind,
                                    bankName = bankName,
                                    bankType = bankType,
                                    cardNumber = cardNumber,
                                    accountNumber = accountNumber,
                                    iban = iban,
                                    cardKind = cardKind,
                                    cvv2 = cvv2,
                                    expiryDate = expiryDate
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
