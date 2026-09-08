package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BankCardEntity
import com.example.data.IranianBankHelper
import com.example.data.KartYarRepository
import com.example.data.CardGroupFilter
import com.example.data.KartFilterAndSortHelper
import com.example.data.PersonEntity
import com.example.data.PersonSortOption
import com.example.data.PersonWithCards
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class UiToastEvent {
    data class Show(val message: String) : UiToastEvent()
}

class KartYarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KartYarRepository
    val searchQuery = MutableStateFlow("")
    val isDarkMode = MutableStateFlow(false)
    val selectedPersonId = MutableStateFlow<Int?>(null)
    val sortOption = MutableStateFlow(PersonSortOption.PINNED_FIRST)
    val groupFilter = MutableStateFlow(CardGroupFilter.ALL)

    private val _toastEvent = MutableStateFlow<UiToastEvent?>(null)
    val toastEvent: StateFlow<UiToastEvent?> = _toastEvent

    val totalPersonCount: StateFlow<Int>
    val totalCardCount: StateFlow<Int>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = KartYarRepository(database.kartYarDao())

        totalPersonCount = repository.totalPersonCount.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

        totalCardCount = repository.totalCardCount.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

        // Seed sample data if database is empty
        viewModelScope.launch {
            val count = repository.totalPersonCount.first()
            if (count == 0) {
                seedSampleData()
            }
        }
    }

    val personsWithCards: StateFlow<List<PersonWithCards>> = combine(
        repository.allPersonsWithCards,
        searchQuery,
        sortOption,
        groupFilter
    ) { list, query, sort, group ->
        KartFilterAndSortHelper.filterAndSort(list, query, sort, group)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSortOption(option: PersonSortOption) {
        sortOption.value = option
    }

    fun setGroupFilter(filter: CardGroupFilter) {
        groupFilter.value = filter
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun toggleDarkMode() {
        isDarkMode.value = !isDarkMode.value
        showToast(if (isDarkMode.value) "حالت تاریک فعال شد" else "حالت روشن فعال شد")
    }

    fun selectPerson(personId: Int?) {
        selectedPersonId.value = personId
    }

    fun addPersonAndCard(
        personName: String,
        personKind: String,
        personNotes: String = "",
        bankName: String,
        bankType: String,
        cardNumber: String,
        accountNumber: String,
        iban: String,
        cardKind: String,
        cvv2: String,
        expiryDate: String,
        cardNotes: String = "",
        isDefault: Boolean = false
    ) {
        viewModelScope.launch {
            val cardValidation = IranianBankHelper.validateCardNumber(cardNumber)
            if (!cardValidation.isValid) {
                showToast(cardValidation.errorMessage ?: "شماره کارت معتبر نیست")
                return@launch
            }

            val ibanValidation = IranianBankHelper.validateIban(iban, isOptional = true)
            if (!ibanValidation.isValid) {
                showToast(ibanValidation.errorMessage ?: "شماره شبا معتبر نیست")
                return@launch
            }

            val normalizedCard = IranianBankHelper.normalizeCardNumber(cardNumber)
            val existingCard = repository.findCardByNumber(normalizedCard)
            if (existingCard != null) {
                val owner = repository.getPersonById(existingCard.personId)
                val ownerName = owner?.name ?: "مخاطب دیگری"
                showToast("این شماره کارت قبلاً برای «$ownerName» ثبت شده است و نمی‌تواند تکراری باشد.")
                return@launch
            }

            // Find existing person with same name or create new
            val existing = personsWithCards.value.firstOrNull {
                it.person.name.trim().equals(personName.trim(), ignoreCase = true)
            }

            val personId = if (existing != null) {
                if (personNotes.isNotBlank() && existing.person.notes != personNotes) {
                    repository.updatePerson(existing.person.copy(notes = personNotes))
                }
                existing.person.id
            } else {
                val newPerson = PersonEntity(
                    name = personName.trim(),
                    kind = personKind,
                    notes = personNotes.trim()
                )
                repository.insertPerson(newPerson).toInt()
            }

            if (isDefault) {
                repository.clearDefaultCardsForPerson(personId)
            }

            // Lock CVV2 and Expiry for customer cards
            val finalCvv2 = if (cardKind == "personal") cvv2 else ""
            val finalExpiry = if (cardKind == "personal") expiryDate else ""

            val card = BankCardEntity(
                personId = personId,
                bankName = bankName,
                bankType = bankType,
                cardNumber = normalizedCard,
                accountNumber = IranianBankHelper.normalizeDigits(accountNumber),
                iban = IranianBankHelper.normalizeIban(iban),
                cardKind = cardKind,
                cvv2 = finalCvv2,
                expiryDate = finalExpiry,
                isDefault = isDefault,
                notes = cardNotes.trim()
            )

            repository.insertCard(card)
            showToast("کارت با موفقیت برای $personName ذخیره شد")
        }
    }

    fun addCardToCurrentPerson(
        personId: Int,
        bankName: String,
        bankType: String,
        cardNumber: String,
        accountNumber: String,
        iban: String,
        cardKind: String,
        cvv2: String,
        expiryDate: String,
        cardNotes: String = "",
        isDefault: Boolean = false
    ) {
        viewModelScope.launch {
            val cardValidation = IranianBankHelper.validateCardNumber(cardNumber)
            if (!cardValidation.isValid) {
                showToast(cardValidation.errorMessage ?: "شماره کارت معتبر نیست")
                return@launch
            }

            val ibanValidation = IranianBankHelper.validateIban(iban, isOptional = true)
            if (!ibanValidation.isValid) {
                showToast(ibanValidation.errorMessage ?: "شماره شبا معتبر نیست")
                return@launch
            }

            val normalizedCard = IranianBankHelper.normalizeCardNumber(cardNumber)
            val existingCard = repository.findCardByNumber(normalizedCard)
            if (existingCard != null) {
                val owner = repository.getPersonById(existingCard.personId)
                val ownerName = owner?.name ?: "مخاطب دیگری"
                showToast("این شماره کارت قبلاً برای «$ownerName» ثبت شده است و نمی‌تواند تکراری باشد.")
                return@launch
            }

            if (isDefault) {
                repository.clearDefaultCardsForPerson(personId)
            }

            val finalCvv2 = if (cardKind == "personal") cvv2 else ""
            val finalExpiry = if (cardKind == "personal") expiryDate else ""

            val card = BankCardEntity(
                personId = personId,
                bankName = bankName,
                bankType = bankType,
                cardNumber = normalizedCard,
                accountNumber = IranianBankHelper.normalizeDigits(accountNumber),
                iban = IranianBankHelper.normalizeIban(iban),
                cardKind = cardKind,
                cvv2 = finalCvv2,
                expiryDate = finalExpiry,
                isDefault = isDefault,
                notes = cardNotes.trim()
            )

            repository.insertCard(card)
            showToast("کارت جدید اضافه شد")
        }
    }

    fun updatePerson(person: PersonEntity) {
        viewModelScope.launch {
            repository.updatePerson(person)
            showToast("مشخصات مخاطب به‌روزرسانی شد")
        }
    }

    fun updateCard(card: BankCardEntity) {
        viewModelScope.launch {
            val cardValidation = IranianBankHelper.validateCardNumber(card.cardNumber)
            if (!cardValidation.isValid) {
                showToast(cardValidation.errorMessage ?: "شماره کارت معتبر نیست")
                return@launch
            }

            val ibanValidation = IranianBankHelper.validateIban(card.iban, isOptional = true)
            if (!ibanValidation.isValid) {
                showToast(ibanValidation.errorMessage ?: "شماره شبا معتبر نیست")
                return@launch
            }

            val normalizedCard = IranianBankHelper.normalizeCardNumber(card.cardNumber)
            val existingCard = repository.findCardByNumber(normalizedCard)
            if (existingCard != null && existingCard.id != card.id) {
                val owner = repository.getPersonById(existingCard.personId)
                val ownerName = owner?.name ?: "مخاطب دیگری"
                showToast("این شماره کارت قبلاً برای «$ownerName» ثبت شده است.")
                return@launch
            }

            if (card.isDefault) {
                repository.clearDefaultCardsForPerson(card.personId)
            }

            val finalCvv2 = if (card.cardKind == "personal") card.cvv2 else ""
            val finalExpiry = if (card.cardKind == "personal") card.expiryDate else ""

            val updated = card.copy(
                cardNumber = normalizedCard,
                accountNumber = IranianBankHelper.normalizeDigits(card.accountNumber),
                iban = IranianBankHelper.normalizeIban(card.iban),
                cvv2 = finalCvv2,
                expiryDate = finalExpiry,
                notes = card.notes.trim()
            )

            repository.updateCard(updated)
            showToast("کارت با موفقیت ویرایش شد")
        }
    }

    fun setDefaultCard(personId: Int, cardId: Int) {
        viewModelScope.launch {
            repository.setDefaultCard(personId, cardId)
            showToast("کارت پیش‌فرض با موفقیت تغییر یافت")
        }
    }

    fun togglePersonPinned(person: PersonEntity) {
        viewModelScope.launch {
            val newPinned = !person.isPinned
            repository.setPersonPinned(person.id, newPinned)
            showToast(if (newPinned) "مخاطب سنجاق شد" else "سنجاق مخاطب برداشته شد")
        }
    }

    fun toggleCardPinned(card: BankCardEntity) {
        viewModelScope.launch {
            val newPinned = !card.isPinned
            repository.setCardPinned(card.id, newPinned)
            showToast(if (newPinned) "کارت سنجاق شد" else "سنجاق کارت برداشته شد")
        }
    }

    fun moveCardUp(personId: Int, cardId: Int) {
        viewModelScope.launch {
            val personWithCards = repository.getPersonWithCardsById(personId).first() ?: return@launch
            val sorted = KartFilterAndSortHelper.sortCards(personWithCards.cards).toMutableList()
            val index = sorted.indexOfFirst { it.id == cardId }
            if (index > 0) {
                val current = sorted[index]
                val prev = sorted[index - 1]
                sorted.forEachIndexed { i, c ->
                    if (c.orderIndex != i) {
                        repository.updateCardOrderIndex(c.id, i)
                    }
                }
                repository.swapCardOrders(current.id, index, prev.id, index - 1)
                showToast("ترتیب کارت تغییر یافت")
            }
        }
    }

    fun moveCardDown(personId: Int, cardId: Int) {
        viewModelScope.launch {
            val personWithCards = repository.getPersonWithCardsById(personId).first() ?: return@launch
            val sorted = KartFilterAndSortHelper.sortCards(personWithCards.cards).toMutableList()
            val index = sorted.indexOfFirst { it.id == cardId }
            if (index >= 0 && index < sorted.size - 1) {
                val current = sorted[index]
                val next = sorted[index + 1]
                sorted.forEachIndexed { i, c ->
                    if (c.orderIndex != i) {
                        repository.updateCardOrderIndex(c.id, i)
                    }
                }
                repository.swapCardOrders(current.id, index, next.id, index + 1)
                showToast("ترتیب کارت تغییر یافت")
            }
        }
    }

    fun deleteCard(card: BankCardEntity) {
        viewModelScope.launch {
            repository.deleteCard(card)
            showToast("کارت حذف شد")
        }
    }

    fun deletePerson(person: PersonEntity) {
        viewModelScope.launch {
            repository.deletePerson(person)
            showToast("مخاطب حذف شد")
        }
    }

    fun copyToClipboard(label: String, text: String, toastMessage: String) {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        showToast(toastMessage)
    }

    private fun showToast(message: String) {
        _toastEvent.value = UiToastEvent.Show(message)
    }

    fun clearToast() {
        _toastEvent.value = null
    }

    private suspend fun seedSampleData() {
        // Seed initial sample data matching index.html & person-cards.html
        val p1 = repository.insertPerson(PersonEntity(name = "علی رضایی", kind = "man", notes = "دوست صمیمی")).toInt()
        repository.insertCard(
            BankCardEntity(
                personId = p1,
                bankName = "بانک ملت",
                bankType = "mellat",
                cardNumber = "6104337890123456",
                accountNumber = "123456789",
                iban = "IR120170000000123456789001",
                cardKind = "customer",
                cardColorStart = "#80142A",
                cardColorEnd = "#FF5B71",
                isDefault = true,
                notes = "حساب حقوق و کارت اصلی"
            )
        )
        repository.insertCard(
            BankCardEntity(
                personId = p1,
                bankName = "بانک ملی ایران",
                bankType = "melli",
                cardNumber = "6037997524689876",
                accountNumber = "987654321",
                iban = "IR170170000000987654321001",
                cardKind = "customer",
                cardColorStart = "#071B58",
                cardColorEnd = "#1D70C8",
                isDefault = false
            )
        )
        repository.insertCard(
            BankCardEntity(
                personId = p1,
                bankName = "بانک صادرات",
                bankType = "saderat",
                cardNumber = "5892101122331234",
                accountNumber = "1122334455",
                iban = "IR190170000000112233445501",
                cardKind = "customer",
                cardColorStart = "#045E7E",
                cardColorEnd = "#19C3D9",
                isDefault = false
            )
        )

        val p2 = repository.insertPerson(PersonEntity(name = "مریم احمدی", kind = "woman")).toInt()
        repository.insertCard(
            BankCardEntity(
                personId = p2,
                bankName = "بانک ملت",
                bankType = "mellat",
                cardNumber = "6104331122334455",
                accountNumber = "5544332211",
                iban = "IR330170000000554433221101",
                cardKind = "customer",
                isDefault = true
            )
        )
        repository.insertCard(
            BankCardEntity(
                personId = p2,
                bankName = "بانک سامان",
                bankType = "saman",
                cardNumber = "6219861098765432",
                accountNumber = "7788990011",
                iban = "IR440170000000778899001101",
                cardKind = "customer",
                isDefault = false
            )
        )

        val p3 = repository.insertPerson(PersonEntity(name = "فروشگاه آفتاب", kind = "store", notes = "خرید لوازم تحریر")).toInt()
        repository.insertCard(
            BankCardEntity(
                personId = p3,
                bankName = "بانک تجارت",
                bankType = "tejarat",
                cardNumber = "5859831020304050",
                accountNumber = "302010",
                iban = "IR550170000000302010000001",
                cardKind = "customer",
                isDefault = true
            )
        )

        val p4 = repository.insertPerson(PersonEntity(name = "شرکت پارس‌گستر", kind = "company")).toInt()
        repository.insertCard(
            BankCardEntity(
                personId = p4,
                bankName = "بانک پاسارگاد",
                bankType = "pasargad",
                cardNumber = "5022291011121314",
                accountNumber = "800900",
                iban = "IR660170000000800900000001",
                cardKind = "customer",
                isDefault = true
            )
        )

        val p5 = repository.insertPerson(PersonEntity(name = "کارت‌های شخصی من", kind = "man", notes = "کارت‌های شخصی خودم", isPinned = true)).toInt()
        repository.insertCard(
            BankCardEntity(
                personId = p5,
                bankName = "بلوبانک",
                bankType = "blubank",
                cardNumber = "6219861234567890",
                accountNumber = "99887766",
                iban = "IR770170000000998877660001",
                cardKind = "personal",
                cvv2 = "345",
                expiryDate = "06/28",
                cardColorStart = "#1D4ED8",
                cardColorEnd = "#3B82F6",
                isDefault = true,
                isPinned = true,
                notes = "کارت اصلی خرید اینترنتی"
            )
        )
    }
}
