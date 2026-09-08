package com.example

import androidx.compose.ui.graphics.Color
import com.example.data.BankCardEntity
import com.example.data.CardAppearanceHelper
import com.example.data.IranianBankHelper
import org.junit.Assert.*
import org.junit.Test

class CardAppearanceTest {

    private fun createCard(
        bankType: String = "mellat",
        bankName: String = "بانک ملت",
        useCustomAppearance: Boolean = false,
        cardColorStart: String = "",
        cardColorEnd: String = ""
    ) = BankCardEntity(
        id = 1,
        personId = 1,
        bankName = bankName,
        bankType = bankType,
        cardNumber = "6104337890123456",
        cardKind = "customer",
        useCustomAppearance = useCustomAppearance,
        cardColorStart = cardColorStart,
        cardColorEnd = cardColorEnd
    )

    @Test
    fun testDefaultAppearanceUsesBankColorsWhenCustomAppearanceIsDisabled() {
        val mellatBank = IranianBankHelper.getBankMetaByType("mellat")
        val card = createCard(
            bankType = "mellat",
            useCustomAppearance = false,
            cardColorStart = "#112233", // Should be ignored because useCustomAppearance is false
            cardColorEnd = "#445566"
        )

        val (startColor, endColor) = CardAppearanceHelper.getEffectiveColors(card, mellatBank)
        assertEquals(mellatBank.colorStart, startColor)
        assertEquals(mellatBank.colorEnd, endColor)
    }

    @Test
    fun testCustomAppearanceOverridesBankColorsWhenEnabledWithValidHex() {
        val mellatBank = IranianBankHelper.getBankMetaByType("mellat")
        val customStartHex = "#1E3A8A"
        val customEndHex = "#3B82F6"

        val card = createCard(
            bankType = "mellat",
            useCustomAppearance = true,
            cardColorStart = customStartHex,
            cardColorEnd = customEndHex
        )

        val (startColor, endColor) = CardAppearanceHelper.getEffectiveColors(card, mellatBank)
        val expectedStart = CardAppearanceHelper.parseHexColor(customStartHex, Color.Black)
        val expectedEnd = CardAppearanceHelper.parseHexColor(customEndHex, Color.Black)

        assertEquals(expectedStart, startColor)
        assertEquals(expectedEnd, endColor)
        assertNotEquals(mellatBank.colorStart, startColor)
    }

    @Test
    fun testInvalidHexFallbackGracefullyToDefaultColors() {
        val mellatBank = IranianBankHelper.getBankMetaByType("mellat")
        val card = createCard(
            bankType = "mellat",
            useCustomAppearance = true,
            cardColorStart = "not-a-color",
            cardColorEnd = "#ZZZZZZ"
        )

        val (startColor, endColor) = CardAppearanceHelper.getEffectiveColors(card, mellatBank)
        // Since custom hex values are invalid, fallback to bank colors
        assertEquals(mellatBank.colorStart, startColor)
        assertEquals(mellatBank.colorEnd, endColor)
    }

    @Test
    fun testHexColorValidation() {
        assertTrue(CardAppearanceHelper.isValidHexColor("#1E3A8A"))
        assertTrue(CardAppearanceHelper.isValidHexColor("1E3A8A"))
        assertTrue(CardAppearanceHelper.isValidHexColor("#FFF"))
        assertTrue(CardAppearanceHelper.isValidHexColor("#FF1E3A8A"))
        assertTrue(CardAppearanceHelper.isValidHexColor("045E7E"))

        assertFalse(CardAppearanceHelper.isValidHexColor(""))
        assertFalse(CardAppearanceHelper.isValidHexColor("#"))
        assertFalse(CardAppearanceHelper.isValidHexColor("12"))
        assertFalse(CardAppearanceHelper.isValidHexColor("#12345"))
        assertFalse(CardAppearanceHelper.isValidHexColor("XYZ123"))
        assertFalse(CardAppearanceHelper.isValidHexColor(null))
    }

    @Test
    fun testResetToBankDefault() {
        val saderatBank = IranianBankHelper.getBankMetaByType("saderat")
        val (useCustom, startHex, endHex) = CardAppearanceHelper.resetToBankDefault(saderatBank)

        assertFalse(useCustom)
        assertEquals(CardAppearanceHelper.colorToHex(saderatBank.colorStart), startHex)
        assertEquals(CardAppearanceHelper.colorToHex(saderatBank.colorEnd), endHex)
    }

    @Test
    fun testOnBankChangedMaintainsCustomAppearanceIfAlreadyCustomized() {
        val melliBank = IranianBankHelper.getBankMetaByType("melli")
        val customStart = "#831843"
        val customEnd = "#BE185D"

        val (useCustom, startHex, endHex) = CardAppearanceHelper.onBankChanged(
            newBank = melliBank,
            useCustomAppearance = true,
            currentColorStart = customStart,
            currentColorEnd = customEnd
        )

        assertTrue(useCustom)
        assertEquals(customStart, startHex)
        assertEquals(customEnd, endHex)
    }

    @Test
    fun testOnBankChangedUpdatesToNewBankColorsWhenNotCustomized() {
        val keshavarziBank = IranianBankHelper.getBankMetaByType("keshavarzi")

        val (useCustom, startHex, endHex) = CardAppearanceHelper.onBankChanged(
            newBank = keshavarziBank,
            useCustomAppearance = false,
            currentColorStart = "#000000",
            currentColorEnd = "#000000"
        )

        assertFalse(useCustom)
        assertEquals(CardAppearanceHelper.colorToHex(keshavarziBank.colorStart), startHex)
        assertEquals(CardAppearanceHelper.colorToHex(keshavarziBank.colorEnd), endHex)
    }

    @Test
    fun testCuratedPresetsIntegrity() {
        val presets = CardAppearanceHelper.curatedPresets
        assertTrue("Presets list should not be empty", presets.isNotEmpty())

        presets.forEach { preset ->
            assertTrue("Preset id should not be blank", preset.id.isNotBlank())
            assertTrue("Preset name should not be blank", preset.name.isNotBlank())
            assertTrue(
                "Preset startHex '${preset.startHex}' should be a valid hex color",
                CardAppearanceHelper.isValidHexColor(preset.startHex)
            )
            assertTrue(
                "Preset endHex '${preset.endHex}' should be a valid hex color",
                CardAppearanceHelper.isValidHexColor(preset.endHex)
            )
        }
    }
}
