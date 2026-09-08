package com.example.data

import androidx.compose.ui.graphics.Color

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()

    val isValid: Boolean get() = this is Valid
    val errorMessage: String? get() = (this as? Invalid)?.message
}

data class BankMeta(
    val name: String,
    val enName: String,
    val typeKey: String,
    val logoText: String,
    val colorStart: Color,
    val colorEnd: Color,
    val accentColor: Color = Color(0xFFFFD700),
    val binPrefixes: List<String>
)

object IranianBankHelper {
    val defaultBanks = listOf(
        BankMeta(
            name = "بانک ملت",
            enName = "BANK MELLAT",
            typeKey = "mellat",
            logoText = "ملت",
            colorStart = Color(0xFF7A0016),
            colorEnd = Color(0xFFC71C38),
            accentColor = Color(0xFFFFCC00),
            binPrefixes = listOf("610433", "991975")
        ),
        BankMeta(
            name = "بانک ملی ایران",
            enName = "BANK MELLI IRAN",
            typeKey = "melli",
            logoText = "ملی",
            colorStart = Color(0xFF002244),
            colorEnd = Color(0xFF0F5298),
            accentColor = Color(0xFFE5A93B),
            binPrefixes = listOf("603799", "170019")
        ),
        BankMeta(
            name = "بانک صادرات ایران",
            enName = "BANK SADERAT IRAN",
            typeKey = "saderat",
            logoText = "صادرات",
            colorStart = Color(0xFF013A52),
            colorEnd = Color(0xFF0288D1),
            accentColor = Color(0xFF4FC3F7),
            binPrefixes = listOf("603769", "903769")
        ),
        BankMeta(
            name = "بانک تجارت",
            enName = "TEJARAT BANK",
            typeKey = "tejarat",
            logoText = "تجارت",
            colorStart = Color(0xFF102A43),
            colorEnd = Color(0xFF2465E0),
            accentColor = Color(0xFF6200EE),
            binPrefixes = listOf("585983", "627353")
        ),
        BankMeta(
            name = "بانک سپه",
            enName = "BANK SEPAH",
            typeKey = "sepah",
            logoText = "سپه",
            colorStart = Color(0xFF064E3B),
            colorEnd = Color(0xFF10B981),
            accentColor = Color(0xFF34D399),
            binPrefixes = listOf("589210", "627381", "639599", "636949", "639370", "505801")
        ),
        BankMeta(
            name = "بانک کشاورزی",
            enName = "KESHAVARZI BANK",
            typeKey = "keshavarzi",
            logoText = "کشاورزی",
            colorStart = Color(0xFF14532D),
            colorEnd = Color(0xFF16A34A),
            accentColor = Color(0xFF4ADE80),
            binPrefixes = listOf("603770", "639217")
        ),
        BankMeta(
            name = "بانک مسکن",
            enName = "MASKAN BANK",
            typeKey = "maskan",
            logoText = "مسکن",
            colorStart = Color(0xFF78350F),
            colorEnd = Color(0xFFD97706),
            accentColor = Color(0xFFFBBF24),
            binPrefixes = listOf("628023")
        ),
        BankMeta(
            name = "بانک رفاه کارگران",
            enName = "REFAH BANK",
            typeKey = "refah",
            logoText = "رفاه",
            colorStart = Color(0xFF1E1B4B),
            colorEnd = Color(0xFF4338CA),
            accentColor = Color(0xFF818CF8),
            binPrefixes = listOf("589463")
        ),
        BankMeta(
            name = "بلوبانک",
            enName = "BLUBANK",
            typeKey = "blubank",
            logoText = "بلو",
            colorStart = Color(0xFF1D4ED8),
            colorEnd = Color(0xFF3B82F6),
            accentColor = Color(0xFF93C5FD),
            binPrefixes = listOf("6219861")
        ),
        BankMeta(
            name = "بانک سامان",
            enName = "SAMAN BANK",
            typeKey = "saman",
            logoText = "سامان",
            colorStart = Color(0xFF0061A8),
            colorEnd = Color(0xFF29B6F6),
            accentColor = Color(0xFF81D4FA),
            binPrefixes = listOf("621986", "861986")
        ),
        BankMeta(
            name = "بانک پاسارگاد",
            enName = "PASARGAD BANK",
            typeKey = "pasargad",
            logoText = "پاسارگاد",
            colorStart = Color(0xFF1C1917),
            colorEnd = Color(0xFF44403C),
            accentColor = Color(0xFFEAB308),
            binPrefixes = listOf("502229", "639347")
        ),
        BankMeta(
            name = "بانک پارسیان",
            enName = "PARSIAN BANK",
            typeKey = "parsian",
            logoText = "پارسیان",
            colorStart = Color(0xFF4A0E17),
            colorEnd = Color(0xFF8B1E2D),
            accentColor = Color(0xFFF59E0B),
            binPrefixes = listOf("622106", "639194", "627884")
        ),
        BankMeta(
            name = "بانک اقتصاد نوین",
            enName = "EGHTESAD NOVIN BANK",
            typeKey = "eghtesad_novin",
            logoText = "اقتصاد",
            colorStart = Color(0xFF581C87),
            colorEnd = Color(0xFF7E22CE),
            accentColor = Color(0xFFD8B4FE),
            binPrefixes = listOf("627412")
        ),
        BankMeta(
            name = "بانک شهر",
            enName = "CITY BANK",
            typeKey = "shahr",
            logoText = "شهر",
            colorStart = Color(0xFF991B1B),
            colorEnd = Color(0xFFEF4444),
            accentColor = Color(0xFFFCA5A5),
            binPrefixes = listOf("502806", "504706")
        ),
        BankMeta(
            name = "بانک آینده",
            enName = "AYANDEH BANK",
            typeKey = "ayandeh",
            logoText = "آینده",
            colorStart = Color(0xFF312E81),
            colorEnd = Color(0xFF6366F1),
            accentColor = Color(0xFFA5B4FC),
            binPrefixes = listOf("636214")
        ),
        BankMeta(
            name = "بانک دی",
            enName = "DAY BANK",
            typeKey = "dey",
            logoText = "دی",
            colorStart = Color(0xFF065F46),
            colorEnd = Color(0xFF059669),
            accentColor = Color(0xFF6EE7B7),
            binPrefixes = listOf("502938")
        ),
        BankMeta(
            name = "بانک سینا",
            enName = "SINA BANK",
            typeKey = "sina",
            logoText = "سینا",
            colorStart = Color(0xFF1E293B),
            colorEnd = Color(0xFF0284C7),
            accentColor = Color(0xFF38BDF8),
            binPrefixes = listOf("639346")
        ),
        BankMeta(
            name = "بانک قرض‌الحسنه مهر ایران",
            enName = "MEHR IRAN BANK",
            typeKey = "mehr",
            logoText = "مهر",
            colorStart = Color(0xFF064E3B),
            colorEnd = Color(0xFF047857),
            accentColor = Color(0xFF10B981),
            binPrefixes = listOf("606373")
        ),
        BankMeta(
            name = "بانک قرض‌الحسنه رسالت",
            enName = "RESALAT BANK",
            typeKey = "resalat",
            logoText = "رسالت",
            colorStart = Color(0xFF047857),
            colorEnd = Color(0xFF34D399),
            accentColor = Color(0xFFA7F3D0),
            binPrefixes = listOf("504172")
        ),
        BankMeta(
            name = "بانک کارآفرین",
            enName = "KARAFARIN BANK",
            typeKey = "karafarin",
            logoText = "کارآفرین",
            colorStart = Color(0xFF0F766E),
            colorEnd = Color(0xFF14B8A6),
            accentColor = Color(0xFF5EEAD4),
            binPrefixes = listOf("627488", "502910")
        ),
        BankMeta(
            name = "بانک گردشگری",
            enName = "TOURISM BANK",
            typeKey = "gardeshgari",
            logoText = "گردشگری",
            colorStart = Color(0xFF9A3412),
            colorEnd = Color(0xFFEA580C),
            accentColor = Color(0xFFFDBA74),
            binPrefixes = listOf("505416")
        ),
        BankMeta(
            name = "بانک سرمایه",
            enName = "SARMAYEH BANK",
            typeKey = "sarmayeh",
            logoText = "سرمایه",
            colorStart = Color(0xFF1E3A8A),
            colorEnd = Color(0xFF3B82F6),
            accentColor = Color(0xFF93C5FD),
            binPrefixes = listOf("639607")
        ),
        BankMeta(
            name = "پست بانک ایران",
            enName = "POST BANK IRAN",
            typeKey = "postbank",
            logoText = "پست‌بانک",
            colorStart = Color(0xFF14532D),
            colorEnd = Color(0xFF15803D),
            accentColor = Color(0xFF86EFAC),
            binPrefixes = listOf("627760")
        ),
        BankMeta(
            name = "بانک ایران زمین",
            enName = "IRAN ZAMIN BANK",
            typeKey = "iranzamin",
            logoText = "ایران‌زمین",
            colorStart = Color(0xFF701A75),
            colorEnd = Color(0xFFA21CAF),
            accentColor = Color(0xFFF0ABFC),
            binPrefixes = listOf("505785")
        ),
        BankMeta(
            name = "بانک خاورمیانه",
            enName = "MIDDLE EAST BANK",
            typeKey = "khavarmianeh",
            logoText = "خاورمیانه",
            colorStart = Color(0xFF334155),
            colorEnd = Color(0xFF64748B),
            accentColor = Color(0xFFCBD5E1),
            binPrefixes = listOf("585947")
        ),
        BankMeta(
            name = "بانک صنعت و معدن",
            enName = "SANAT VA MADAN BANK",
            typeKey = "sanat_madan",
            logoText = "صنعت‌و‌معدن",
            colorStart = Color(0xFF431407),
            colorEnd = Color(0xFF78350F),
            accentColor = Color(0xFFF59E0B),
            binPrefixes = listOf("627961")
        ),
        BankMeta(
            name = "بانک توسعه تعاون",
            enName = "TOSE'E TA'AVON BANK",
            typeKey = "tosee_taavon",
            logoText = "توسعه‌تعاون",
            colorStart = Color(0xFF164E63),
            colorEnd = Color(0xFF0891B2),
            accentColor = Color(0xFF67E8F9),
            binPrefixes = listOf("502908")
        ),
        BankMeta(
            name = "بانک توسعه صادرات ایران",
            enName = "EXPORT DEVELOPMENT BANK",
            typeKey = "tosee_saderat",
            logoText = "توسعه‌صادرات",
            colorStart = Color(0xFF1E293B),
            colorEnd = Color(0xFF0F766E),
            accentColor = Color(0xFF2DD4BF),
            binPrefixes = listOf("627648", "207177")
        ),
        BankMeta(
            name = "بانک مرکزی جمهوری اسلامی ایران",
            enName = "CENTRAL BANK",
            typeKey = "cbi",
            logoText = "مرکزی",
            colorStart = Color(0xFF1E293B),
            colorEnd = Color(0xFF475569),
            accentColor = Color(0xFFFBBF24),
            binPrefixes = listOf("636795")
        )
    )

    fun getBankMetaByType(typeKey: String): BankMeta {
        return defaultBanks.firstOrNull { it.typeKey.equals(typeKey, ignoreCase = true) }
            ?: defaultBanks.firstOrNull { it.name.contains(typeKey, ignoreCase = true) }
            ?: defaultBanks.firstOrNull { it.enName.contains(typeKey, ignoreCase = true) }
            ?: BankMeta(
                name = if (typeKey.isNotBlank()) typeKey else "بانک",
                enName = "BANK",
                typeKey = "other",
                logoText = typeKey.take(2).ifEmpty { "بانک" },
                colorStart = Color(0xFF1E293B),
                colorEnd = Color(0xFF334155),
                accentColor = Color(0xFF94A3B8),
                binPrefixes = emptyList()
            )
    }

    fun normalizeDigits(raw: String): String {
        val builder = StringBuilder()
        for (ch in raw) {
            when (ch) {
                in '0'..'9' -> builder.append(ch)
                in '۰'..'۹' -> builder.append(('0'.code + (ch.code - '۰'.code)).toChar())
                in '٠'..'٩' -> builder.append(('0'.code + (ch.code - '٠'.code)).toChar())
            }
        }
        return builder.toString()
    }

    fun normalizeCardNumber(raw: String): String {
        return normalizeDigits(raw)
    }

    fun normalizeIban(raw: String): String {
        val builder = StringBuilder()
        for (ch in raw.trim()) {
            when (ch) {
                in '0'..'9' -> builder.append(ch)
                in '۰'..'۹' -> builder.append(('0'.code + (ch.code - '۰'.code)).toChar())
                in '٠'..'٩' -> builder.append(('0'.code + (ch.code - '٠'.code)).toChar())
                in 'a'..'z' -> builder.append(ch.uppercaseChar())
                in 'A'..'Z' -> builder.append(ch)
            }
        }
        val res = builder.toString()
        if (res.startsWith("IR")) {
            return res
        } else if (res.all { it.isDigit() } && res.length == 24) {
            return "IR$res"
        }
        return res
    }

    fun detectBankByCardNumber(cardNumber: String): BankMeta? {
        val digits = normalizeCardNumber(cardNumber)
        if (digits.length < 6) return null

        // Sort mappings by prefix length descending so longer specific prefixes (e.g. 7-digit BluBank) match first
        val sortedPairs = defaultBanks.flatMap { bank ->
            bank.binPrefixes.map { prefix -> prefix to bank }
        }.sortedByDescending { it.first.length }

        return sortedPairs.firstOrNull { (prefix, _) ->
            digits.startsWith(prefix)
        }?.second
    }

    fun getBankMetaByCardNumber(cardNumber: String): BankMeta {
        return detectBankByCardNumber(cardNumber) ?: getBankMetaByType("other")
    }

    /**
     * Standard ISO/IEC 7812 Luhn algorithm for 16-digit Iranian bank cards (Shetab)
     */
    fun isCardNumberChecksumValid(cardNumber: String): Boolean {
        val digits = normalizeCardNumber(cardNumber)
        if (digits.length != 16) return false
        // Exclude dummy numbers like all zeros
        if (digits.all { it == '0' }) return false

        var sum = 0
        for (i in 0 until 16) {
            val d = digits[i].digitToIntOrNull() ?: return false
            val v = if (i % 2 == 0) {
                val doubled = d * 2
                if (doubled > 9) doubled - 9 else doubled
            } else {
                d
            }
            sum += v
        }
        return sum % 10 == 0
    }

    fun validateCardNumber(cardNumber: String): ValidationResult {
        val digits = normalizeCardNumber(cardNumber)
        if (digits.isEmpty()) {
            return ValidationResult.Invalid("شماره کارت نمی‌تواند خالی باشد")
        }
        if (digits.length != 16) {
            return ValidationResult.Invalid("شماره کارت باید ۱۶ رقم باشد")
        }
        if (!isCardNumberChecksumValid(digits)) {
            return ValidationResult.Invalid("شماره کارت معتبر نیست")
        }
        return ValidationResult.Valid
    }

    fun isCardNumberValid(cardNumber: String): Boolean {
        return validateCardNumber(cardNumber).isValid
    }

    /**
     * Standard ISO 7064 MOD 97-10 IBAN validation for Iranian Sheba numbers
     */
    fun validateIban(rawIban: String, isOptional: Boolean = true): ValidationResult {
        val clean = normalizeIban(rawIban)
        if (clean.isEmpty() || clean == "IR") {
            return if (isOptional) ValidationResult.Valid else ValidationResult.Invalid("شماره شبا نمی‌تواند خالی باشد")
        }
        if (!clean.startsWith("IR")) {
            return ValidationResult.Invalid("شماره شبا معتبر نیست")
        }
        if (clean.length != 26) {
            return ValidationResult.Invalid("شماره شبا معتبر نیست")
        }
        for (i in 2 until 26) {
            if (!clean[i].isDigit()) {
                return ValidationResult.Invalid("شماره شبا معتبر نیست")
            }
        }
        // Exclude all zeros in the numeric body
        if (clean.substring(2).all { it == '0' }) {
            return ValidationResult.Invalid("شماره شبا معتبر نیست")
        }

        // Rearrange: substring(4) + "1827" (numeric code for 'I'=18, 'R'=27) + checkDigits (chars 2..3)
        val rearranged = clean.substring(4) + "1827" + clean.substring(2, 4)
        var remainder = 0
        for (ch in rearranged) {
            if (!ch.isDigit()) return ValidationResult.Invalid("شماره شبا معتبر نیست")
            remainder = (remainder * 10 + (ch - '0')) % 97
        }

        return if (remainder == 1) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("شماره شبا معتبر نیست")
        }
    }

    fun isIbanValid(rawIban: String, isOptional: Boolean = true): Boolean {
        return validateIban(rawIban, isOptional).isValid
    }

    /**
     * Calculates the 2 check digits for a 22-digit Iranian BBAN according to ISO 7064 MOD 97-10
     */
    fun calculateIbanCheckDigits(bban22Digits: String): String {
        val bban = normalizeDigits(bban22Digits).padStart(22, '0').take(22)
        val testStr = bban + "182700"
        var remainder = 0
        for (ch in testStr) {
            remainder = (remainder * 10 + (ch - '0')) % 97
        }
        val checkNum = 98 - remainder
        return if (checkNum < 10) "0$checkNum" else checkNum.toString()
    }

    fun formatCardNumber(raw: String): String {
        val digits = normalizeCardNumber(raw).take(16)
        return digits.chunked(4).joinToString("  ")
    }

    fun formatIban(rawIban: String): String {
        val clean = normalizeIban(rawIban).take(26)
        return clean.chunked(4).joinToString(" ")
    }
}


