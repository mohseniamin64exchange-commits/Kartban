package com.example.data

enum class PersonSortOption(val title: String) {
    PINNED_FIRST("سنجاق شده‌ها اول"),
    NAME_ASC("نام (الفبا)"),
    NEWEST("جدیدترین"),
    OLDEST("قدیمی‌ترین")
}

enum class CardGroupFilter(val title: String) {
    ALL("همه"),
    MY_CARDS("کارت‌های من"),
    OTHER_CARDS("کارت‌های دیگران")
}

object KartFilterAndSortHelper {

    /**
     * Card ordering priority:
     * 1. default card (isDefault DESC)
     * 2. pinned card (isPinned DESC)
     * 3. manual order (orderIndex ASC)
     * 4. createdAt (createdAt DESC)
     */
    val BankCardComparator: Comparator<BankCardEntity> =
        compareByDescending<BankCardEntity> { it.isDefault }
            .thenByDescending { it.isPinned }
            .thenBy { it.orderIndex }
            .thenByDescending { it.createdAt }

    val BankCardNoDefaultComparator: Comparator<BankCardEntity> =
        compareByDescending<BankCardEntity> { it.isPinned }
            .thenBy { it.orderIndex }
            .thenByDescending { it.createdAt }

    fun sortCards(cards: List<BankCardEntity>, forceDefaultCardTop: Boolean = true): List<BankCardEntity> {
        val comparator = if (forceDefaultCardTop) BankCardComparator else BankCardNoDefaultComparator
        return cards.sortedWith(comparator)
    }

    /**
     * Person sorting:
     * Pinned persons always appear before non-pinned persons,
     * then secondary ordering according to [sortOption].
     */
    fun sortPersons(
        persons: List<PersonWithCards>,
        sortOption: PersonSortOption = PersonSortOption.PINNED_FIRST
    ): List<PersonWithCards> {
        val comparator: Comparator<PersonWithCards> = when (sortOption) {
            PersonSortOption.PINNED_FIRST -> {
                compareByDescending<PersonWithCards> { it.person.isPinned }
                    .thenByDescending { it.person.createdAt }
            }
            PersonSortOption.NAME_ASC -> {
                compareByDescending<PersonWithCards> { it.person.isPinned }
                    .thenBy { it.person.name.trim() }
            }
            PersonSortOption.NEWEST -> {
                compareByDescending<PersonWithCards> { it.person.isPinned }
                    .thenByDescending { it.person.createdAt }
            }
            PersonSortOption.OLDEST -> {
                compareByDescending<PersonWithCards> { it.person.isPinned }
                    .thenBy { it.person.createdAt }
            }
        }
        return persons.sortedWith(comparator)
    }

    /**
     * Filters persons and their cards by category:
     * - ALL: retains all cards and persons
     * - MY_CARDS: retains persons having personal cards, displaying only personal cards
     * - OTHER_CARDS: retains persons having non-personal (customer) cards, displaying only those
     */
    fun filterByGroup(
        persons: List<PersonWithCards>,
        filter: CardGroupFilter
    ): List<PersonWithCards> {
        return when (filter) {
            CardGroupFilter.ALL -> persons
            CardGroupFilter.MY_CARDS -> {
                persons.mapNotNull { pwc ->
                    val personalCards = pwc.cards.filter { it.cardKind == "personal" }
                    if (personalCards.isNotEmpty()) {
                        pwc.copy(cards = personalCards)
                    } else null
                }
            }
            CardGroupFilter.OTHER_CARDS -> {
                persons.mapNotNull { pwc ->
                    val otherCards = pwc.cards.filter { it.cardKind != "personal" }
                    if (otherCards.isNotEmpty()) {
                        pwc.copy(cards = otherCards)
                    } else null
                }
            }
        }
    }

    /**
     * Checks if a PersonWithCards matches the search query.
     * Matches partial text across:
     * - person name
     * - store name
     * - company name
     * - bank name
     * - partial card number
     * - last 4 digits of card number
     * - partial account number
     * - partial IBAN
     * - person note
     * - card note
     *
     * Normalizes Persian, Arabic, and English digits and ignores whitespace/hyphens.
     */
    fun matchesQuery(
        personWithCards: PersonWithCards,
        rawQuery: String
    ): Boolean {
        if (rawQuery.isBlank()) return true
        val q = rawQuery.trim()
        val normalizedQueryDigits = IranianBankHelper.normalizeDigits(q).replace(" ", "").replace("-", "")
        val normalizedQueryIban = IranianBankHelper.normalizeIban(q).replace(" ", "")

        val person = personWithCards.person
        // 1. Person name
        if (person.name.contains(q, ignoreCase = true)) return true

        // 2. Store name / Company name (by kind keyword or name/notes)
        if (person.kind == "store" && (q.contains("فروشگاه") || "فروشگاه".contains(q))) return true
        if (person.kind == "company" && (q.contains("شرکت") || "شرکت".contains(q))) return true

        // 3. Person notes
        if (person.notes.contains(q, ignoreCase = true)) return true

        // 4. Cards
        for (card in personWithCards.cards) {
            // Bank name
            if (card.bankName.contains(q, ignoreCase = true)) return true

            // Card notes
            if (card.notes.contains(q, ignoreCase = true)) return true

            // Card Number
            val cardDigits = IranianBankHelper.normalizeCardNumber(card.cardNumber)
            if (normalizedQueryDigits.isNotEmpty()) {
                if (cardDigits.contains(normalizedQueryDigits)) return true
                // Explicit check for last 4 digits match
                if (cardDigits.endsWith(normalizedQueryDigits)) return true
            }
            if (card.cardNumber.replace(" ", "").contains(q.replace(" ", ""), ignoreCase = true)) return true

            // Account number
            val accDigits = IranianBankHelper.normalizeDigits(card.accountNumber)
            if (normalizedQueryDigits.isNotEmpty() && accDigits.isNotEmpty()) {
                if (accDigits.contains(normalizedQueryDigits)) return true
            }
            if (card.accountNumber.isNotBlank() && card.accountNumber.contains(q, ignoreCase = true)) return true

            // IBAN (spaces ignored, digits normalized)
            val cardIban = IranianBankHelper.normalizeIban(card.iban).replace(" ", "")
            if (normalizedQueryIban.isNotEmpty() && normalizedQueryIban != "IR") {
                if (cardIban.contains(normalizedQueryIban, ignoreCase = true)) return true
            }
            if (normalizedQueryDigits.isNotEmpty() && cardIban.contains(normalizedQueryDigits)) return true
            if (card.iban.replace(" ", "").contains(q.replace(" ", ""), ignoreCase = true)) return true
        }

        return false
    }

    /**
     * Performs full pipeline: sort cards inside persons, filter by group, filter by search query, sort persons.
     */
    fun filterAndSort(
        list: List<PersonWithCards>,
        query: String,
        sortOption: PersonSortOption,
        groupFilter: CardGroupFilter,
        forceDefaultCardTop: Boolean = true
    ): List<PersonWithCards> {
        // 1. Sort cards inside each person by priority
        val personsWithSortedCards = list.map { pwc ->
            pwc.copy(cards = sortCards(pwc.cards, forceDefaultCardTop))
        }

        // 2. Filter by group ("All", "My Cards", "Other People's Cards")
        val groupedList = filterByGroup(personsWithSortedCards, groupFilter)

        // 3. Filter by search query
        val searchFiltered = if (query.isBlank()) {
            groupedList
        } else {
            groupedList.filter { matchesQuery(it, query) }
        }

        // 4. Sort persons (pinned persons first, then by sortOption)
        return sortPersons(searchFiltered, sortOption)
    }
}
