package com.example.data

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import org.json.JSONArray
import org.json.JSONObject

data class QrPersonData(
    val name: String,
    val kind: String = "man",
    val notes: String = ""
)

data class QrCardData(
    val bankName: String,
    val bankType: String,
    val cardNumber: String,
    val accountNumber: String = "",
    val iban: String = "",
    val cardKind: String = "customer",
    val cvv2: String = "",
    val expiryDate: String = "",
    val cardColorStart: String = "",
    val cardColorEnd: String = "",
    val useCustomAppearance: Boolean = false,
    val isDefault: Boolean = false,
    val isPinned: Boolean = false,
    val orderIndex: Int = 0,
    val notes: String = ""
)

data class QrPayload(
    val app: String = "Kartban",
    val qrVersion: Int = 1,
    val type: String = "person_cards",
    val person: QrPersonData,
    val cards: List<QrCardData>
)

sealed class QrGenerateResult {
    data class Success(val jsonString: String, val bitmap: Bitmap) : QrGenerateResult()
    data class Error(val message: String) : QrGenerateResult()
}

sealed class QrParseResult {
    data class Success(val payload: QrPayload) : QrParseResult()
    data class Error(val message: String) : QrParseResult()
}

data class QrImportResult(
    val addedCards: Int,
    val duplicateCards: Int,
    val rejectedCards: Int,
    val targetPersonName: String,
    val isNewPerson: Boolean
)

object QrTransferHelper {

    private const val MAX_QR_CHAR_LIMIT = 2000

    fun generateQrPayload(
        person: PersonEntity,
        cards: List<BankCardEntity>,
        sizePx: Int = 512
    ): QrGenerateResult {
        if (cards.isEmpty()) {
            return QrGenerateResult.Error("کارتی برای ایجاد کد QR انتخاب نشده است")
        }

        val jsonStr = buildJsonString(person, cards, trimNonEssential = false)

        val finalJson = if (jsonStr.length > MAX_QR_CHAR_LIMIT) {
            // Attempt auto-trimming non-essential fields (notes)
            val trimmedJson = buildJsonString(person, cards, trimNonEssential = true)
            if (trimmedJson.length > MAX_QR_CHAR_LIMIT) {
                return QrGenerateResult.Error("حجم اطلاعات برای یک QR بیش از حد مجاز است")
            }
            trimmedJson
        } else {
            jsonStr
        }

        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(finalJson, BarcodeFormat.QR_CODE, sizePx, sizePx)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) AndroidColor.BLACK else AndroidColor.WHITE)
                }
            }
            QrGenerateResult.Success(jsonString = finalJson, bitmap = bitmap)
        } catch (e: Exception) {
            QrGenerateResult.Error("خطا در ایجاد تصویر کد QR")
        }
    }

    private fun buildJsonString(
        person: PersonEntity,
        cards: List<BankCardEntity>,
        trimNonEssential: Boolean
    ): String {
        val root = JSONObject()
        root.put("app", "Kartban")
        root.put("qrVersion", 1)
        root.put("type", "person_cards")

        val pObj = JSONObject()
        pObj.put("name", person.name)
        pObj.put("kind", person.kind)
        pObj.put("notes", if (trimNonEssential) "" else person.notes)
        root.put("person", pObj)

        val cardsArray = JSONArray()
        for (card in cards) {
            val cObj = JSONObject()
            cObj.put("bankName", card.bankName)
            cObj.put("bankType", card.bankType)
            cObj.put("cardNumber", card.cardNumber)
            cObj.put("accountNumber", card.accountNumber)
            cObj.put("iban", card.iban)
            cObj.put("cardKind", card.cardKind)
            cObj.put("cvv2", card.cvv2)
            cObj.put("expiryDate", card.expiryDate)
            cObj.put("cardColorStart", card.cardColorStart)
            cObj.put("cardColorEnd", card.cardColorEnd)
            cObj.put("useCustomAppearance", card.useCustomAppearance)
            cObj.put("isDefault", card.isDefault)
            cObj.put("isPinned", card.isPinned)
            cObj.put("orderIndex", card.orderIndex)
            cObj.put("notes", if (trimNonEssential) "" else card.notes)
            cardsArray.put(cObj)
        }
        root.put("cards", cardsArray)

        return root.toString()
    }

    fun parseQrString(qrString: String): QrParseResult {
        if (qrString.isBlank()) {
            return QrParseResult.Error("کد QR اسکن‌شده خالی است")
        }

        try {
            val root = JSONObject(qrString)
            val app = root.optString("app", "")
            if (app != "Kartban") {
                return QrParseResult.Error("کد QR متعلق به برنامه کارت‌بان نیست")
            }

            val qrVersion = root.optInt("qrVersion", 0)
            if (qrVersion < 1) {
                return QrParseResult.Error("نسخه کد QR پشتیبانی نمی‌شود")
            }

            val pObj = root.optJSONObject("person")
                ?: return QrParseResult.Error("کد QR فاقد اطلاعات مخاطب است")

            val personName = pObj.optString("name", "").trim()
            if (personName.isBlank()) {
                return QrParseResult.Error("نام مخاطب در کد QR معتبر نیست")
            }

            val qrPerson = QrPersonData(
                name = personName,
                kind = pObj.optString("kind", "man"),
                notes = pObj.optString("notes", "")
            )

            val cardsArray = root.optJSONArray("cards")
                ?: return QrParseResult.Error("کد QR فاقد کارت‌های بانکی است")

            val cardList = mutableListOf<QrCardData>()
            for (i in 0 until cardsArray.length()) {
                val cObj = cardsArray.optJSONObject(i) ?: continue
                val rawCardNum = cObj.optString("cardNumber", "")
                val normCardNum = IranianBankHelper.normalizeCardNumber(rawCardNum)

                val bankType = cObj.optString("bankType", "")
                val bankMeta = IranianBankHelper.detectBankByCardNumber(normCardNum)
                    ?: IranianBankHelper.getBankMetaByType(bankType)

                val cardData = QrCardData(
                    bankName = cObj.optString("bankName", bankMeta.name),
                    bankType = bankMeta.typeKey,
                    cardNumber = normCardNum,
                    accountNumber = cObj.optString("accountNumber", ""),
                    iban = IranianBankHelper.normalizeIban(cObj.optString("iban", "")),
                    cardKind = cObj.optString("cardKind", "customer"),
                    cvv2 = cObj.optString("cvv2", ""),
                    expiryDate = cObj.optString("expiryDate", ""),
                    cardColorStart = cObj.optString("cardColorStart", ""),
                    cardColorEnd = cObj.optString("cardColorEnd", ""),
                    useCustomAppearance = cObj.optBoolean("useCustomAppearance", false),
                    isDefault = cObj.optBoolean("isDefault", false),
                    isPinned = cObj.optBoolean("isPinned", false),
                    orderIndex = cObj.optInt("orderIndex", i),
                    notes = cObj.optString("notes", "")
                )
                cardList.add(cardData)
            }

            if (cardList.isEmpty()) {
                return QrParseResult.Error("هیچ کارت معتبری در کد QR یافت نشد")
            }

            return QrParseResult.Success(
                QrPayload(
                    app = app,
                    qrVersion = qrVersion,
                    person = qrPerson,
                    cards = cardList
                )
            )
        } catch (e: Exception) {
            return QrParseResult.Error("فرمت کد QR معتبر نیست")
        }
    }

    fun decodeBitmap(bitmap: Bitmap): String? {
        return try {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

            val source = RGBLuminanceSource(width, height, pixels)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = MultiFormatReader()
            val result = reader.decode(binaryBitmap)
            result.text
        } catch (e: Exception) {
            null
        }
    }

    suspend fun importQrPayload(
        repository: KartYarRepository,
        payload: QrPayload
    ): QrImportResult {
        return repository.runInTransaction {
            var addedCards = 0
            var duplicateCards = 0
            var rejectedCards = 0

            val existingPerson = repository.findPersonByName(payload.person.name)
            val targetPersonId: Int
            val isNewPerson: Boolean

            if (existingPerson != null) {
                targetPersonId = existingPerson.id
                isNewPerson = false
            } else {
                val newPerson = PersonEntity(
                    name = payload.person.name,
                    kind = payload.person.kind,
                    notes = payload.person.notes
                )
                targetPersonId = repository.insertPerson(newPerson).toInt()
                isNewPerson = true
            }

            for (card in payload.cards) {
                val normCardNum = IranianBankHelper.normalizeCardNumber(card.cardNumber)
                val cardValidation = IranianBankHelper.validateCardNumber(normCardNum)
                val ibanValidation = IranianBankHelper.validateIban(card.iban, isOptional = true)

                // Validate card number checksum & IBAN format
                if (!cardValidation.isValid || !ibanValidation.isValid) {
                    rejectedCards++
                    continue
                }

                // Check for duplicate card
                if (repository.isCardNumberDuplicate(normCardNum)) {
                    duplicateCards++
                    continue
                }

                val newCard = BankCardEntity(
                    personId = targetPersonId,
                    bankName = card.bankName,
                    bankType = card.bankType,
                    cardNumber = normCardNum,
                    accountNumber = card.accountNumber,
                    iban = card.iban,
                    cardKind = card.cardKind,
                    cvv2 = if (card.cardKind == "personal") card.cvv2 else "",
                    expiryDate = if (card.cardKind == "personal") card.expiryDate else "",
                    cardColorStart = card.cardColorStart,
                    cardColorEnd = card.cardColorEnd,
                    useCustomAppearance = card.useCustomAppearance,
                    isDefault = card.isDefault,
                    isPinned = card.isPinned,
                    orderIndex = card.orderIndex,
                    notes = card.notes
                )

                repository.insertCard(newCard)
                addedCards++
            }

            QrImportResult(
                addedCards = addedCards,
                duplicateCards = duplicateCards,
                rejectedCards = rejectedCards,
                targetPersonName = payload.person.name,
                isNewPerson = isNewPerson
            )
        }
    }
}
