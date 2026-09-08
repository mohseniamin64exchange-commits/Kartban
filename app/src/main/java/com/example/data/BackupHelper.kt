package com.example.data

import org.json.JSONArray
import org.json.JSONObject

data class BackupPersonData(
    val name: String,
    val kind: String = "man",
    val notes: String = "",
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class BackupCardData(
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
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class BackupPersonGroup(
    val person: BackupPersonData,
    val cards: List<BackupCardData>
)

data class BackupPayload(
    val app: String = "Kartban",
    val backupVersion: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val personGroups: List<BackupPersonGroup>
)

sealed class BackupParseResult {
    data class Success(
        val personCount: Int,
        val cardCount: Int,
        val payload: BackupPayload
    ) : BackupParseResult()

    data class Error(val message: String) : BackupParseResult()
}

data class RestoreResult(
    val addedPersons: Int,
    val mergedPersons: Int,
    val addedCards: Int,
    val duplicateCards: Int
)

object BackupHelper {

    fun exportBackupJson(personsWithCards: List<PersonWithCards>): String {
        val root = JSONObject()
        root.put("app", "Kartban")
        root.put("backupVersion", 1)
        root.put("createdAt", System.currentTimeMillis())

        val personsArray = JSONArray()
        for (pwCards in personsWithCards) {
            val groupObj = JSONObject()

            val pObj = JSONObject()
            pObj.put("name", pwCards.person.name)
            pObj.put("kind", pwCards.person.kind)
            pObj.put("notes", pwCards.person.notes)
            pObj.put("isPinned", pwCards.person.isPinned)
            pObj.put("createdAt", pwCards.person.createdAt)
            groupObj.put("person", pObj)

            val cardsArray = JSONArray()
            for (card in pwCards.cards) {
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
                cObj.put("notes", card.notes)
                cObj.put("createdAt", card.createdAt)
                cardsArray.put(cObj)
            }
            groupObj.put("cards", cardsArray)

            personsArray.put(groupObj)
        }
        root.put("persons", personsArray)

        return root.toString(2)
    }

    fun parseBackupJson(jsonString: String): BackupParseResult {
        if (jsonString.isBlank()) {
            return BackupParseResult.Error("فایل پشتیبان خالی است")
        }

        try {
            val root = JSONObject(jsonString)
            val app = root.optString("app", "")
            if (app != "Kartban") {
                return BackupParseResult.Error("فایل پشتیبان معتبر نیست")
            }

            val version = root.optInt("backupVersion", 0)
            if (version < 1) {
                return BackupParseResult.Error("نسخه فایل پشتیبان پشتیبانی نمی‌شود")
            }

            val createdAt = root.optLong("createdAt", System.currentTimeMillis())
            val personsArray = root.optJSONArray("persons")
                ?: return BackupParseResult.Error("فایل پشتیبان فاقد اطلاعات مخاطبین است")

            val groupList = mutableListOf<BackupPersonGroup>()
            var totalCards = 0

            for (i in 0 until personsArray.length()) {
                val groupObj = personsArray.optJSONObject(i) ?: continue
                val pObj = groupObj.optJSONObject("person") ?: continue

                val personName = pObj.optString("name", "").trim()
                if (personName.isBlank()) continue

                val personData = BackupPersonData(
                    name = personName,
                    kind = pObj.optString("kind", "man"),
                    notes = pObj.optString("notes", ""),
                    isPinned = pObj.optBoolean("isPinned", false),
                    createdAt = pObj.optLong("createdAt", System.currentTimeMillis())
                )

                val cardsArray = groupObj.optJSONArray("cards") ?: JSONArray()
                val cardList = mutableListOf<BackupCardData>()

                for (j in 0 until cardsArray.length()) {
                    val cObj = cardsArray.optJSONObject(j) ?: continue
                    val rawCardNum = cObj.optString("cardNumber", "")
                    val normCardNum = IranianBankHelper.normalizeCardNumber(rawCardNum)
                    if (normCardNum.length < 16) continue

                    val bankType = cObj.optString("bankType", "")
                    val bankMeta = IranianBankHelper.detectBankByCardNumber(normCardNum)
                        ?: IranianBankHelper.getBankMetaByType(bankType)

                    val cardData = BackupCardData(
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
                        orderIndex = cObj.optInt("orderIndex", j),
                        notes = cObj.optString("notes", ""),
                        createdAt = cObj.optLong("createdAt", System.currentTimeMillis())
                    )
                    cardList.add(cardData)
                }

                totalCards += cardList.size
                groupList.add(BackupPersonGroup(person = personData, cards = cardList))
            }

            if (groupList.isEmpty()) {
                return BackupParseResult.Error("اطلاعات معتبری در فایل پشتیبان یافت نشد")
            }

            return BackupParseResult.Success(
                personCount = groupList.size,
                cardCount = totalCards,
                payload = BackupPayload(
                    app = app,
                    backupVersion = version,
                    createdAt = createdAt,
                    personGroups = groupList
                )
            )
        } catch (e: Exception) {
            return BackupParseResult.Error("فایل پشتیبان معتبر نیست")
        }
    }

    suspend fun restoreBackup(repository: KartYarRepository, payload: BackupPayload): RestoreResult {
        var addedPersons = 0
        var mergedPersons = 0
        var addedCards = 0
        var duplicateCards = 0

        for (group in payload.personGroups) {
            val existingPerson = repository.findPersonByName(group.person.name)
            val targetPersonId: Int
            if (existingPerson != null) {
                targetPersonId = existingPerson.id
                mergedPersons++
            } else {
                val newPerson = PersonEntity(
                    name = group.person.name,
                    kind = group.person.kind,
                    notes = group.person.notes,
                    isPinned = group.person.isPinned,
                    createdAt = group.person.createdAt
                )
                targetPersonId = repository.insertPerson(newPerson).toInt()
                addedPersons++
            }

            for (card in group.cards) {
                val normNum = IranianBankHelper.normalizeCardNumber(card.cardNumber)
                if (repository.isCardNumberDuplicate(normNum)) {
                    duplicateCards++
                    continue
                }

                val newCard = BankCardEntity(
                    personId = targetPersonId,
                    bankName = card.bankName,
                    bankType = card.bankType,
                    cardNumber = normNum,
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
                    notes = card.notes,
                    createdAt = card.createdAt
                )

                repository.insertCard(newCard)
                addedCards++
            }
        }

        return RestoreResult(
            addedPersons = addedPersons,
            mergedPersons = mergedPersons,
            addedCards = addedCards,
            duplicateCards = duplicateCards
        )
    }
}
