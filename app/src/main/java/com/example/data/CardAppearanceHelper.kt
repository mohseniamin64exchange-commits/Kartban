package com.example.data

import androidx.compose.ui.graphics.Color
import java.util.Locale

data class CardColorPreset(
    val id: String,
    val name: String,
    val startHex: String,
    val endHex: String
)

object CardAppearanceHelper {

    val curatedPresets = listOf(
        CardColorPreset("gold", "طلایی لوکس", "#78350F", "#D97706"),
        CardColorPreset("carbon_blue", "آبی کربنی", "#0F172A", "#1E40AF"),
        CardColorPreset("emerald", "سبز زمردی", "#064E3B", "#059669"),
        CardColorPreset("royal_purple", "بنفش سلطنتی", "#3B0764", "#7E22CE"),
        CardColorPreset("obsidian", "مشکی متالیک", "#18181B", "#3F3F46"),
        CardColorPreset("ruby", "یاقوتی سرخ", "#7F1D1D", "#DC2626"),
        CardColorPreset("ocean_teal", "فیروزه‌ای اقیانوسی", "#0F766E", "#06B6D4"),
        CardColorPreset("rose_gold", "رزگلد", "#831843", "#DB2777"),
        CardColorPreset("night_indigo", "نیلی شب", "#1E1B4B", "#4338CA"),
        CardColorPreset("warm_amber", "کهربایی گرم", "#9A3412", "#EA580C")
    )

    val singleColorSwatches = listOf(
        "#0F172A", // Dark Navy
        "#1E293B", // Slate
        "#18181B", // Dark Zinc
        "#1D4ED8", // Blue
        "#0284C7", // Sky
        "#0F766E", // Teal
        "#064E3B", // Forest
        "#16A34A", // Green
        "#78350F", // Brown Bronze
        "#D97706", // Amber Gold
        "#7F1D1D", // Dark Crimson
        "#DC2626", // Red
        "#3B0764", // Deep Violet
        "#7E22CE", // Purple
        "#831843", // Rose
        "#4B5563"  // Cool Gray
    )

    /**
     * Checks whether a string represents a valid 6 or 8-digit hexadecimal color (with or without '#').
     */
    fun isValidHexColor(hex: String?): Boolean {
        if (hex.isNullOrBlank()) return false
        val clean = hex.trim().removePrefix("#")
        if (clean.length != 6 && clean.length != 8) return false
        return clean.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
    }

    /**
     * Safely parses a hex color string into a Compose Color.
     * If the input is null, empty, or invalid, returns the fallback color without throwing.
     */
    fun parseHexColor(hex: String?, fallback: Color): Color {
        if (!isValidHexColor(hex)) return fallback
        return try {
            val clean = hex!!.trim().removePrefix("#")
            when (clean.length) {
                6 -> {
                    val colorInt = clean.toLong(16).toInt() or (0xFF shl 24)
                    Color(colorInt)
                }
                8 -> {
                    val colorInt = clean.toLong(16).toInt()
                    Color(colorInt)
                }
                else -> fallback
            }
        } catch (e: Exception) {
            fallback
        }
    }

    /**
     * Converts a Compose Color to uppercase "#RRGGBB" string.
     */
    fun colorToHex(color: Color): String {
        val r = (color.red * 255f).toInt().coerceIn(0, 255)
        val g = (color.green * 255f).toInt().coerceIn(0, 255)
        val b = (color.blue * 255f).toInt().coerceIn(0, 255)
        return String.format(Locale.US, "#%02X%02X%02X", r, g, b)
    }

    /**
     * Resolves the effective start and end colors for a given card and bank metadata.
     * If [BankCardEntity.useCustomAppearance] is true and colors are valid, returns custom colors.
     * If colors are invalid or corrupted, safely falls back to the bank default.
     * If [BankCardEntity.useCustomAppearance] is false, returns bank default colors.
     */
    fun getEffectiveColors(card: BankCardEntity, bankMeta: BankMeta): Pair<Color, Color> {
        return if (card.useCustomAppearance) {
            val start = parseHexColor(card.cardColorStart, bankMeta.colorStart)
            val end = parseHexColor(card.cardColorEnd, bankMeta.colorEnd)
            start to end
        } else {
            bankMeta.colorStart to bankMeta.colorEnd
        }
    }

    /**
     * Returns the bank's default hex colors as a pair of "#RRGGBB" strings.
     */
    fun getBankDefaultHexColors(bankMeta: BankMeta): Pair<String, String> {
        return colorToHex(bankMeta.colorStart) to colorToHex(bankMeta.colorEnd)
    }

    /**
     * Helper to compute appearance state when the selected bank changes during edit.
     * If user has already customized appearance, keep existing custom appearance and colors.
     * Otherwise, update colors to match the new bank's default appearance.
     */
    fun onBankChanged(
        newBank: BankMeta,
        useCustomAppearance: Boolean,
        currentColorStart: String,
        currentColorEnd: String
    ): Triple<Boolean, String, String> {
        return if (useCustomAppearance) {
            Triple(true, currentColorStart, currentColorEnd)
        } else {
            val (defaultStart, defaultEnd) = getBankDefaultHexColors(newBank)
            Triple(false, defaultStart, defaultEnd)
        }
    }

    /**
     * Resets appearance to the bank default.
     * Returns Triple(useCustomAppearance = false, defaultStartHex, defaultEndHex).
     */
    fun resetToBankDefault(bankMeta: BankMeta): Triple<Boolean, String, String> {
        val (defaultStart, defaultEnd) = getBankDefaultHexColors(bankMeta)
        return Triple(false, defaultStart, defaultEnd)
    }
}
