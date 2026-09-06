package com.example.data

import androidx.compose.ui.graphics.Color

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
            binPrefixes = listOf("610433")
        ),
        BankMeta(
            name = "بانک ملی ایران",
            enName = "BANK MELLI IRAN",
            typeKey = "melli",
            logoText = "ملی",
            colorStart = Color(0xFF002244),
            colorEnd = Color(0xFF0F5298),
            accentColor = Color(0xFFE5A93B),
            binPrefixes = listOf("603799")
        ),
        BankMeta(
            name = "بانک صادرات ایران",
            enName = "BANK SADERAT IRAN",
            typeKey = "saderat",
            logoText = "صادرات",
            colorStart = Color(0xFF013A52),
            colorEnd = Color(0xFF0288D1),
            accentColor = Color(0xFF4FC3F7),
            binPrefixes = listOf("603769")
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
            name = "بانک سامان",
            enName = "SAMAN BANK",
            typeKey = "saman",
            logoText = "سامان",
            colorStart = Color(0xFF0061A8),
            colorEnd = Color(0xFF29B6F6),
            accentColor = Color(0xFF81D4FA),
            binPrefixes = listOf("621986")
        ),
        BankMeta(
            name = "بانک پاسارگاد",
            enName = "PASARGAD BANK",
            typeKey = "pasargad",
            logoText = "پاسارگاد",
            colorStart = Color(0xFF1C1917),
            colorEnd = Color(0xFF44403C),
            accentColor = Color(0xFFEAB308),
            binPrefixes = listOf("502229")
        ),
        BankMeta(
            name = "بانک پارسیان",
            enName = "PARSIAN BANK",
            typeKey = "parsian",
            logoText = "پارسیان",
            colorStart = Color(0xFF4A0E17),
            colorEnd = Color(0xFF8B1E2D),
            accentColor = Color(0xFFF59E0B),
            binPrefixes = listOf("622106")
        ),
        BankMeta(
            name = "بانک سپه",
            enName = "BANK SEPAH",
            typeKey = "sepah",
            logoText = "سپه",
            colorStart = Color(0xFF064E3B),
            colorEnd = Color(0xFF10B981),
            accentColor = Color(0xFF34D399),
            binPrefixes = listOf("589210", "627381", "639599")
        ),
        BankMeta(
            name = "بانک کشاورزی",
            enName = "KESHAVARZI BANK",
            typeKey = "keshavarzi",
            logoText = "کشاورزی",
            colorStart = Color(0xFF14532D),
            colorEnd = Color(0xFF16A34A),
            accentColor = Color(0xFF4ADE80),
            binPrefixes = listOf("603770")
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
            name = "بانک شهر",
            enName = "CITY BANK",
            typeKey = "shahr",
            logoText = "شهر",
            colorStart = Color(0xFF991B1B),
            colorEnd = Color(0xFFEF4444),
            accentColor = Color(0xFFFCA5A5),
            binPrefixes = listOf("502806")
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
            name = "بانک رسالت",
            enName = "RESALAT BANK",
            typeKey = "resalat",
            logoText = "رسالت",
            colorStart = Color(0xFF047857),
            colorEnd = Color(0xFF34D399),
            accentColor = Color(0xFFA7F3D0),
            binPrefixes = listOf("504172")
        )
    )

    fun getBankMetaByType(typeKey: String): BankMeta {
        return defaultBanks.firstOrNull { it.typeKey == typeKey }
            ?: defaultBanks.firstOrNull { it.name.contains(typeKey, ignoreCase = true) }
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

    fun detectBankByCardNumber(cardNumber: String): BankMeta? {
        val digits = cardNumber.filter { it.isDigit() }
        if (digits.length < 6) return null
        val prefix6 = digits.take(6)
        return defaultBanks.firstOrNull { bank ->
            bank.binPrefixes.any { prefix -> prefix6.startsWith(prefix) }
        }
    }

    fun formatCardNumber(raw: String): String {
        val digits = raw.filter { it.isDigit() }.take(16)
        return digits.chunked(4).joinToString("  ")
    }

    fun formatIban(rawIban: String): String {
        var clean = rawIban.uppercase().filter { it.isLetterOrDigit() }
        if (!clean.startsWith("IR")) {
            clean = "IR$clean"
        }
        clean = clean.take(26)
        return clean.chunked(4).joinToString(" ")
    }
}

