package com.example

import com.example.data.BackupHelper
import com.example.data.BackupParseResult
import com.example.data.BankCardEntity
import com.example.data.PersonEntity
import com.example.data.PersonWithCards
import com.example.data.QrParseResult
import com.example.data.QrTransferHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Phase5BackupAndQrTest {

    @Test
    fun testBackupHelperExportAndParseSuccess() {
        val person = PersonEntity(id = 1, name = "تست پشتیبان", kind = "man", notes = "یادداشت تست")
        val card1 = BankCardEntity(
            id = 10,
            personId = 1,
            bankName = "بانک سامان",
            bankType = "saman",
            cardNumber = "6219861098765432",
            accountNumber = "12345",
            iban = "IR120170000000000000001234",
            cardKind = "customer",
            notes = "کارت اول"
        )
        val card2 = BankCardEntity(
            id = 11,
            personId = 1,
            bankName = "بانک ملت",
            bankType = "mellat",
            cardNumber = "6104337890123456",
            accountNumber = "67890",
            iban = "IR120170000000000000006789",
            cardKind = "personal",
            notes = "کارت دوم"
        )

        val list = listOf(PersonWithCards(person = person, cards = listOf(card1, card2)))

        // 1. Export
        val json = BackupHelper.exportBackupJson(list)
        assertTrue(json.contains("\"backupVersion\": 1"))
        assertTrue(json.contains("تست پشتیبان"))
        assertTrue(json.contains("6219861098765432"))

        // 2. Parse
        val parseResult = BackupHelper.parseBackupJson(json)
        val errMsg = if (parseResult is BackupParseResult.Error) parseResult.message else ""
        assertTrue("Expected BackupParseResult.Success but was $parseResult. Error: $errMsg. JSON was:\n$json", parseResult is BackupParseResult.Success)
        val success = parseResult as BackupParseResult.Success
        assertEquals(1, success.personCount)
        assertEquals(2, success.cardCount)
        assertEquals("تست پشتیبان", success.payload.personGroups.first().person.name)
        assertEquals(2, success.payload.personGroups.first().cards.size)
    }

    @Test
    fun testBackupHelperParseInvalidJson() {
        val result = BackupHelper.parseBackupJson("Invalid JSON text")
        assertTrue(result is BackupParseResult.Error)
    }

    @Test
    fun testQrTransferHelperGenerateAndParseSuccess() {
        val person = PersonEntity(id = 1, name = "سارا علوی", kind = "woman")
        val card = BankCardEntity(
            id = 20,
            personId = 1,
            bankName = "بلوبانک",
            bankType = "blubank",
            cardNumber = "6219861234567890",
            accountNumber = "9988",
            iban = "IR770170000000998877660001",
            cardKind = "customer"
        )

        // Generate Payload
        val genResult = QrTransferHelper.generateQrPayload(person, listOf(card))
        assertTrue(genResult is com.example.data.QrGenerateResult.Success)
        val genSuccess = genResult as com.example.data.QrGenerateResult.Success
        val qrString = genSuccess.jsonString
        assertTrue(qrString.contains("Kartban") && qrString.contains("blubank"))

        // Parse String
        val parseResult = QrTransferHelper.parseQrString(qrString)
        val qrErrMsg = if (parseResult is QrParseResult.Error) parseResult.message else ""
        assertTrue("Expected QrParseResult.Success but was $parseResult. Error: $qrErrMsg. QRStr was:\n$qrString", parseResult is QrParseResult.Success)
        val success = parseResult as QrParseResult.Success
        assertEquals("سارا علوی", success.payload.person.name)
        assertEquals(1, success.payload.cards.size)
        assertEquals("6219861234567890", success.payload.cards.first().cardNumber)
    }

    @Test
    fun testQrTransferHelperParseInvalidHeader() {
        val result = QrTransferHelper.parseQrString("INVALID_HEADER_DATA")
        assertTrue(result is QrParseResult.Error)
    }
}
