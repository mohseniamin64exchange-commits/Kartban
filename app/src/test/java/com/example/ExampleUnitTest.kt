package com.example

import com.example.data.IranianBankHelper
import com.example.data.ValidationResult
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    // 1. Persian digit normalization
    @Test
    fun testPersianDigitNormalization() {
        val persianCard = "۶۰۳۷۹۹۷۵۴۳۲۱۰۰۰۱"
        val normalized = IranianBankHelper.normalizeCardNumber(persianCard)
        assertEquals("6037997543210001", normalized)

        val formattedWithPersianSeparators = "۶۱۰۴-۳۳۷۸ ۹۰۱۲/۳۴۵۶"
        val normalizedSeparators = IranianBankHelper.normalizeCardNumber(formattedWithPersianSeparators)
        assertEquals("6104337890123456", normalizedSeparators)
    }

    // 2. Arabic digit normalization
    @Test
    fun testArabicDigitNormalization() {
        val arabicCard = "٦٠٣٧٩٩٧٥٤٣٢١٠٠٠١"
        val normalized = IranianBankHelper.normalizeCardNumber(arabicCard)
        assertEquals("6037997543210001", normalized)

        val mixedArabic = "٥٨٩٢-١٠١١ ٢٢٣٣-١٢٣٤"
        val normalizedMixed = IranianBankHelper.normalizeCardNumber(mixedArabic)
        assertEquals("5892101122331234", normalizedMixed)
    }

    // 3. Valid card number checksum (Luhn algorithm)
    @Test
    fun testValidCardNumberChecksum() {
        // Valid Luhn cards
        val validCard1 = "6104337890123456"
        assertTrue(IranianBankHelper.isCardNumberChecksumValid(validCard1))
        assertTrue(IranianBankHelper.isCardNumberValid(validCard1))
        assertEquals(ValidationResult.Valid, IranianBankHelper.validateCardNumber(validCard1))

        val validCard2 = "6037997599988889"
        assertTrue(IranianBankHelper.isCardNumberChecksumValid(validCard2))
        assertTrue(IranianBankHelper.isCardNumberValid(validCard2))

        // Valid card entered in Persian digits
        val validPersianCard = "۶۱۰۴۳۳۷۸۹۰۱۲۳۴۵۶"
        assertTrue(IranianBankHelper.isCardNumberValid(validPersianCard))
    }

    // 4. Invalid card number checksum
    @Test
    fun testInvalidCardNumberChecksum() {
        // Same card with tampered last check digit
        val invalidCard = "6104337890123457"
        assertFalse(IranianBankHelper.isCardNumberChecksumValid(invalidCard))
        assertFalse(IranianBankHelper.isCardNumberValid(invalidCard))
        val result = IranianBankHelper.validateCardNumber(invalidCard)
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("شماره کارت معتبر نیست", (result as ValidationResult.Invalid).message)

        // All zeros
        val allZeros = "0000000000000000"
        assertFalse(IranianBankHelper.isCardNumberValid(allZeros))

        // Incomplete length
        val shortCard = "60379912345"
        val shortResult = IranianBankHelper.validateCardNumber(shortCard)
        assertEquals("شماره کارت باید ۱۶ رقم باشد", (shortResult as ValidationResult.Invalid).message)

        // Empty card
        val emptyResult = IranianBankHelper.validateCardNumber("")
        assertEquals("شماره کارت نمی‌تواند خالی باشد", (emptyResult as ValidationResult.Invalid).message)
    }

    // 5. Valid Iranian Sheba (MOD-97)
    @Test
    fun testValidIranianSheba() {
        val bban = "0120000000001003242000"
        val checkDigits = IranianBankHelper.calculateIbanCheckDigits(bban)
        val validSheba = "IR$checkDigits$bban"

        assertTrue(IranianBankHelper.isIbanValid(validSheba))
        assertEquals(ValidationResult.Valid, IranianBankHelper.validateIban(validSheba))

        // Lowercase 'ir' prefix
        val lowerSheba = "ir$checkDigits$bban"
        assertTrue(IranianBankHelper.isIbanValid(lowerSheba))

        // Sheba with spaces
        val spacedSheba = "IR $checkDigits 0120 0000 0000 1003 2420 00"
        assertTrue(IranianBankHelper.isIbanValid(spacedSheba))

        // Sheba with Persian digits
        val persianSheba = "IR" + checkDigits.map { ('۰'.code + (it - '0')).toChar() }.joinToString("") +
                bban.map { ('۰'.code + (it - '0')).toChar() }.joinToString("")
        assertTrue(IranianBankHelper.isIbanValid(persianSheba))

        // Empty or "IR" is valid when optional
        assertTrue(IranianBankHelper.isIbanValid("", isOptional = true))
        assertTrue(IranianBankHelper.isIbanValid("IR", isOptional = true))
    }

    // 6. Invalid Iranian Sheba
    @Test
    fun testInvalidIranianSheba() {
        val bban = "0120000000001003242000"
        val checkDigits = IranianBankHelper.calculateIbanCheckDigits(bban)
        // Corrupt check digits
        val wrongCheck = if (checkDigits == "00") "01" else "00"
        val invalidSheba = "IR$wrongCheck$bban"

        assertFalse(IranianBankHelper.isIbanValid(invalidSheba))
        val result = IranianBankHelper.validateIban(invalidSheba)
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("شماره شبا معتبر نیست", (result as ValidationResult.Invalid).message)

        // Invalid length
        assertFalse(IranianBankHelper.isIbanValid("IR123456789"))

        // Wrong country code
        assertFalse(IranianBankHelper.isIbanValid("DE$checkDigits$bban"))

        // All zeros
        assertFalse(IranianBankHelper.isIbanValid("IR000000000000000000000000"))

        // Non-optional empty
        val emptyMandatory = IranianBankHelper.validateIban("", isOptional = false)
        assertTrue(emptyMandatory is ValidationResult.Invalid)
    }

    // 7. Known BIN detection
    @Test
    fun testKnownBinDetection() {
        // Mellat
        assertEquals("mellat", IranianBankHelper.detectBankByCardNumber("6104331234567890")?.typeKey)
        // Melli
        assertEquals("melli", IranianBankHelper.detectBankByCardNumber("6037991234567890")?.typeKey)
        // Saderat
        assertEquals("saderat", IranianBankHelper.detectBankByCardNumber("6037691234567890")?.typeKey)
        // Tejarat
        assertEquals("tejarat", IranianBankHelper.detectBankByCardNumber("5859831234567890")?.typeKey)
        // Sepah
        assertEquals("sepah", IranianBankHelper.detectBankByCardNumber("5892101234567890")?.typeKey)
        // BluBank (7 digits: 6219861)
        assertEquals("blubank", IranianBankHelper.detectBankByCardNumber("6219861234567890")?.typeKey)
        // Saman (6 digits: 621986, not ending in 1)
        assertEquals("saman", IranianBankHelper.detectBankByCardNumber("6219860234567890")?.typeKey)
        // Pasargad
        assertEquals("pasargad", IranianBankHelper.detectBankByCardNumber("5022291234567890")?.typeKey)
        // Parsian
        assertEquals("parsian", IranianBankHelper.detectBankByCardNumber("6221061234567890")?.typeKey)
        // Resalat
        assertEquals("resalat", IranianBankHelper.detectBankByCardNumber("5041721234567890")?.typeKey)
        // Mehr Iran
        assertEquals("mehr", IranianBankHelper.detectBankByCardNumber("6063731234567890")?.typeKey)

        // Detection with Persian digits
        assertEquals("mellat", IranianBankHelper.detectBankByCardNumber("۶۱۰۴۳۳۱۲۳۴۵۶۷۸۹۰")?.typeKey)
    }

    // 8. Unknown BIN handling
    @Test
    fun testUnknownBinHandling() {
        // Unknown 6-digit prefix
        assertNull(IranianBankHelper.detectBankByCardNumber("9999991234567890"))
        val fallbackMeta = IranianBankHelper.getBankMetaByCardNumber("9999991234567890")
        assertEquals("other", fallbackMeta.typeKey)

        // Less than 6 digits
        assertNull(IranianBankHelper.detectBankByCardNumber("60379"))
        assertNull(IranianBankHelper.detectBankByCardNumber(""))
    }

    @Test
    fun testCardFormatting() {
        val card = "6037997512345678"
        val formatted = IranianBankHelper.formatCardNumber(card)
        assertEquals("6037  9975  1234  5678", formatted)
    }
}
