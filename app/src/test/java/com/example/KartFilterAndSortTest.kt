package com.example

import com.example.data.BankCardEntity
import com.example.data.CardGroupFilter
import com.example.data.KartFilterAndSortHelper
import com.example.data.PersonEntity
import com.example.data.PersonSortOption
import com.example.data.PersonWithCards
import org.junit.Assert.*
import org.junit.Test

class KartFilterAndSortTest {

    private fun createPerson(
        id: Int,
        name: String,
        isPinned: Boolean = false,
        notes: String = "",
        createdAt: Long = id * 1000L
    ) = PersonEntity(
        id = id,
        name = name,
        kind = "personal",
        notes = notes,
        isPinned = isPinned,
        createdAt = createdAt
    )

    private fun createCard(
        id: Int,
        personId: Int,
        bankName: String,
        cardNumber: String,
        iban: String = "",
        accountNumber: String = "",
        isDefault: Boolean = false,
        isPinned: Boolean = false,
        orderIndex: Int = 0,
        cardKind: String = "general",
        createdAt: Long = id * 1000L
    ) = BankCardEntity(
        id = id,
        personId = personId,
        bankName = bankName,
        bankType = "mellat",
        cardNumber = cardNumber,
        iban = iban,
        accountNumber = accountNumber,
        cardKind = cardKind,
        isDefault = isDefault,
        isPinned = isPinned,
        orderIndex = orderIndex,
        createdAt = createdAt
    )

    // 1. Pinned persons appear before unpinned persons
    @Test
    fun testPinnedPersonsAppearFirst() {
        val p1 = createPerson(id = 1, name = "علی", isPinned = false)
        val p2 = createPerson(id = 2, name = "رضا", isPinned = true)
        val p3 = createPerson(id = 3, name = "سارا", isPinned = false)

        val list = listOf(
            PersonWithCards(p1, emptyList()),
            PersonWithCards(p2, emptyList()),
            PersonWithCards(p3, emptyList())
        )

        val sorted = KartFilterAndSortHelper.filterAndSort(
            list = list,
            query = "",
            sortOption = PersonSortOption.PINNED_FIRST,
            groupFilter = CardGroupFilter.ALL
        )

        assertEquals("رضا", sorted[0].person.name)
        assertTrue(sorted[0].person.isPinned)
        assertFalse(sorted[1].person.isPinned)
        assertFalse(sorted[2].person.isPinned)
    }

    // 2. Sorting options: NAME_ASC, NEWEST, OLDEST
    @Test
    fun testSortingOptions() {
        val pA = createPerson(id = 1, name = "بهرام", createdAt = 100L)
        val pB = createPerson(id = 2, name = "آرش", createdAt = 300L)
        val pC = createPerson(id = 3, name = "حمید", createdAt = 200L)

        val list = listOf(
            PersonWithCards(pA, emptyList()),
            PersonWithCards(pB, emptyList()),
            PersonWithCards(pC, emptyList())
        )

        // Name Ascending: آرش -> بهرام -> حمید
        val sortedByName = KartFilterAndSortHelper.filterAndSort(list, "", PersonSortOption.NAME_ASC, CardGroupFilter.ALL)
        assertEquals("آرش", sortedByName[0].person.name)
        assertEquals("بهرام", sortedByName[1].person.name)
        assertEquals("حمید", sortedByName[2].person.name)

        // Newest First: آرش (300L) -> حمید (200L) -> بهرام (100L)
        val sortedByNewest = KartFilterAndSortHelper.filterAndSort(list, "", PersonSortOption.NEWEST, CardGroupFilter.ALL)
        assertEquals("آرش", sortedByNewest[0].person.name)
        assertEquals("حمید", sortedByNewest[1].person.name)
        assertEquals("بهرام", sortedByNewest[2].person.name)

        // Oldest First: بهرام (100L) -> حمید (200L) -> آرش (300L)
        val sortedByOldest = KartFilterAndSortHelper.filterAndSort(list, "", PersonSortOption.OLDEST, CardGroupFilter.ALL)
        assertEquals("بهرام", sortedByOldest[0].person.name)
        assertEquals("حمید", sortedByOldest[1].person.name)
        assertEquals("آرش", sortedByOldest[2].person.name)
    }

    // 3. Card sorting priority: isDefault > isPinned > orderIndex > createdAt
    @Test
    fun testCardSortingPriority() {
        val c1 = createCard(id = 1, personId = 1, bankName = "ملت", cardNumber = "6104337890123456", isDefault = false, isPinned = false, orderIndex = 0)
        val c2 = createCard(id = 2, personId = 1, bankName = "ملی", cardNumber = "6037997512345678", isDefault = true, isPinned = false, orderIndex = 5)
        val c3 = createCard(id = 3, personId = 1, bankName = "صادرات", cardNumber = "6037691122334455", isDefault = false, isPinned = true, orderIndex = 1)
        val c4 = createCard(id = 4, personId = 1, bankName = "تجارت", cardNumber = "5859831122334455", isDefault = false, isPinned = true, orderIndex = 2)

        val sorted = KartFilterAndSortHelper.sortCards(listOf(c1, c2, c3, c4))

        // Default card must come first
        assertEquals(2, sorted[0].id)
        assertTrue(sorted[0].isDefault)

        // Pinned cards next, ordered by orderIndex
        assertEquals(3, sorted[1].id)
        assertTrue(sorted[1].isPinned)
        assertEquals(4, sorted[2].id)
        assertTrue(sorted[2].isPinned)

        // Non-pinned, non-default last
        assertEquals(1, sorted[3].id)
    }

    // 4. Group Filter: ALL, MY_CARDS, OTHER_CARDS
    @Test
    fun testGroupFiltering() {
        val me = createPerson(id = 1, name = "من (کارت‌های من)")
        val myCard = createCard(id = 1, personId = 1, bankName = "ملت", cardNumber = "6104337890123456", cardKind = "personal")

        val other = createPerson(id = 2, name = "علی محمدی")
        val otherCard = createCard(id = 2, personId = 2, bankName = "صادرات", cardNumber = "6037691122334455", cardKind = "general")

        val list = listOf(
            PersonWithCards(me, listOf(myCard)),
            PersonWithCards(other, listOf(otherCard))
        )

        // ALL
        val allResult = KartFilterAndSortHelper.filterAndSort(list, "", PersonSortOption.PINNED_FIRST, CardGroupFilter.ALL)
        assertEquals(2, allResult.size)

        // MY_CARDS
        val myCardsResult = KartFilterAndSortHelper.filterAndSort(list, "", PersonSortOption.PINNED_FIRST, CardGroupFilter.MY_CARDS)
        assertEquals(1, myCardsResult.size)
        assertEquals("من (کارت‌های من)", myCardsResult[0].person.name)

        // OTHER_CARDS
        val otherCardsResult = KartFilterAndSortHelper.filterAndSort(list, "", PersonSortOption.PINNED_FIRST, CardGroupFilter.OTHER_CARDS)
        assertEquals(1, otherCardsResult.size)
        assertEquals("علی محمدی", otherCardsResult[0].person.name)
    }

    // 5. Search matching with Persian digits and normalization
    @Test
    fun testSearchMatching() {
        val p = createPerson(id = 1, name = "علیرضا احمدی", notes = "همکار پروژه")
        val c = createCard(
            id = 1,
            personId = 1,
            bankName = "بانک ملت",
            cardNumber = "6104337890123456",
            iban = "IR120120000000001003242000",
            accountNumber = "9876543210"
        )
        val item = PersonWithCards(p, listOf(c))

        // Match by name
        assertTrue(KartFilterAndSortHelper.matchesQuery(item, "علیرضا"))
        assertTrue(KartFilterAndSortHelper.matchesQuery(item, "احمدی"))

        // Match by notes
        assertTrue(KartFilterAndSortHelper.matchesQuery(item, "پروژه"))

        // Match by bank name
        assertTrue(KartFilterAndSortHelper.matchesQuery(item, "ملت"))

        // Match by card number in English digits
        assertTrue(KartFilterAndSortHelper.matchesQuery(item, "6104"))
        assertTrue(KartFilterAndSortHelper.matchesQuery(item, "3456"))

        // Match by card number in Persian digits
        assertTrue(KartFilterAndSortHelper.matchesQuery(item, "۶۱۰۴"))
        assertTrue(KartFilterAndSortHelper.matchesQuery(item, "۳۴۵۶"))

        // Match by account number
        assertTrue(KartFilterAndSortHelper.matchesQuery(item, "98765"))
        assertTrue(KartFilterAndSortHelper.matchesQuery(item, "۹۸۷۶۵"))

        // Match by IBAN
        assertTrue(KartFilterAndSortHelper.matchesQuery(item, "1003242000"))
        assertTrue(KartFilterAndSortHelper.matchesQuery(item, "۱۰۰۳۲۴۲۰۰۰"))

        // No match
        assertFalse(KartFilterAndSortHelper.matchesQuery(item, "سامان"))
        assertFalse(KartFilterAndSortHelper.matchesQuery(item, "999999"))
    }
}
