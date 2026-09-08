package com.example

import com.example.data.IranianBankHelper
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testCardNormalization() {
        val rawCard = "۶۰۳۷-۹۹۷۵ ۴۳۲۱-۰۰۰۱"
        val normalized = IranianBankHelper.normalizeCardNumber(rawCard)
        assertEquals("6037997543210001", normalized)
    }

    @Test
    fun testBankDetectionByPrefix() {
        val mellatCard = "6104331234567890"
        val bank = IranianBankHelper.getBankMetaByCardNumber(mellatCard)
        assertEquals("mellat", bank.typeKey)

        val melliCard = "6037991234567890"
        val melliBank = IranianBankHelper.getBankMetaByCardNumber(melliCard)
        assertEquals("melli", melliBank.typeKey)
    }

    @Test
    fun testCardFormatting() {
        val card = "6037997512345678"
        val formatted = IranianBankHelper.formatCardNumber(card)
        assertEquals("6037 - 9975 - 1234 - 5678", formatted)
    }
}
