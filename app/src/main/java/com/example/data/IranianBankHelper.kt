package com.example.data

import androidx.compose.ui.graphics.Color

data class BankMeta(
    val name: String,
    val enName: String,
    val typeKey: String,
    val logoText: String,
    val colorStart: Color,
    val colorEnd: Color,
    val binPrefixes: List<String>
)

object IranianBankHelper {
    val defaultBanks = listOf(
        BankMeta(
            name = "بانک ملت",
            enName = "BANK MELLAT",
            typeKey = "mellat",
            logoText = "ملت",
            colorStart = Color(0xFF80142A),
            colorEnd = Color(0xFFFF5B71),
            binPrefixes = listOf("610433")
        ),
        BankMeta(
            name = "بانک ملی ایران",
            enName = "BANK MELLI IRAN",
            typeKey = "melli",
            logoText = "ملی",
            colorStart = Color(0xFF071B58),
            colorEnd = Color(0xFF1D70C8),
            binPrefixes = listOf("603799")
        ),
        BankMeta(
            name = "بانک صادرات",
            enName = "BANK SADERAT IRAN",
            typeKey = "saderat",
            logoText = "صادرات",
            colorStart = Color(0xFF045E7E),
            colorEnd = Color(0xFF19C3D9),
            binPrefixes = listOf("603769")
        ),
        BankMeta(
            name = "بانک تجارت",
            enName = "TEJARAT BANK",
            typeKey = "tejarat",
            logoText = "تجارت",
            colorStart = Color(0xFF1E3A8A),
            colorEnd = Color(0xFF3B82F6),
            binPrefixes = listOf("585983", "627353")
        ),
        BankMeta(
            name = "بانک سامان",
            enName = "SAMAN BANK",
            typeKey = "saman",
            logoText = "سامان",
            colorStart = Color(0xFF0284C7),
            colorEnd = Color(0xFF38BDF8),
            binPrefixes = listOf("621986")
        ),
        BankMeta(
            name = "بانک پاسارگاد",
            enName = "PASARGAD BANK",
            typeKey = "pasargad",
            logoText = "پاسارگاد",
            colorStart = Color(0xFF1F2937),
            colorEnd = Color(0xFF4B5563),
            binPrefixes = listOf("502229")
        ),
        BankMeta(
            name = "بانک سپه",
            enName = "BANK SEPAH",
            typeKey = "sepah",
            logoText = "سپه",
            colorStart = Color(0xFF065F46),
            colorEnd = Color(0xFF10B981),
            binPrefixes = listOf("589210", "627381", "639599")
        ),
        BankMeta(
            name = "بانک کشاورزی",
            enName = "KESHAVARZI BANK",
            typeKey = "keshavarzi",
            logoText = "کشاورزی",
            colorStart = Color(0xFF15803D),
            colorEnd = Color(0xFF22C55E),
            binPrefixes = listOf("603770")
        ),
        BankMeta(
            name = "بلوبانک",
            enName = "BLUBANK",
            typeKey = "blubank",
            logoText = "بلو",
            colorStart = Color(0xFF1D4ED8),
            colorEnd = Color(0xFF60A5FA),
            binPrefixes = listOf("6219861")
        ),
        BankMeta(
            name = "بانک رسالت",
            enName = "RESALAT BANK",
            typeKey = "resalat",
            logoText = "رسالت",
            colorStart = Color(0xFF047857),
            colorEnd = Color(0xFF34D399),
            binPrefixes = listOf("504172")
        ),
        BankMeta(
            name = "بانک شهر",
            enName = "CITY BANK",
            typeKey = "shahr",
            logoText = "شهر",
            colorStart = Color(0xFFB91C1C),
            colorEnd = Color(0xFFEF4444),
            binPrefixes = listOf("502806")
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
