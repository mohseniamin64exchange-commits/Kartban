package com.example.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object CardImageGenerator {

    /**
     * Renders a high-resolution Bitmap representation of the bank card,
     * faithfully incorporating custom colors (if customized) or bank default colors.
     */
    fun createCardBitmap(
        card: BankCardEntity,
        personName: String
    ): Bitmap {
        val width = 1000
        val height = 620
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val meta = IranianBankHelper.getBankMetaByType(card.bankType.ifEmpty { card.bankName })
        val (colorStartCompose, colorEndCompose) = CardAppearanceHelper.getEffectiveColors(card, meta)

        // Convert Compose Color to Android ARGB Int
        val startColorInt = android.graphics.Color.argb(
            (colorStartCompose.alpha * 255).toInt(),
            (colorStartCompose.red * 255).toInt(),
            (colorStartCompose.green * 255).toInt(),
            (colorStartCompose.blue * 255).toInt()
        )
        val endColorInt = android.graphics.Color.argb(
            (colorEndCompose.alpha * 255).toInt(),
            (colorEndCompose.red * 255).toInt(),
            (colorEndCompose.green * 255).toInt(),
            (colorEndCompose.blue * 255).toInt()
        )

        // 1. Draw Card Background Rounded Rect with Gradient
        val cardRect = RectF(20f, 20f, (width - 20).toFloat(), (height - 20).toFloat())
        val cardCornerRadius = 36f
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                cardRect.left, cardRect.top,
                cardRect.right, cardRect.bottom,
                startColorInt, endColorInt,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(cardRect, cardCornerRadius, cardCornerRadius, bgPaint)

        // 2. Draw Decorative Wave Arcs (PSD aesthetic)
        val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.argb(30, 255, 255, 255)
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        val wavePath = Path().apply {
            moveTo(cardRect.left, cardRect.height() * 0.35f)
            cubicTo(
                cardRect.width() * 0.4f, cardRect.height() * 0.1f,
                cardRect.width() * 0.6f, cardRect.height() * 0.8f,
                cardRect.right, cardRect.height() * 0.45f
            )
        }
        canvas.drawPath(wavePath, wavePaint)

        // Wave ribbon filled
        val ribbonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.argb(18, 255, 255, 255)
            style = Paint.Style.FILL
        }
        val ribbonPath = Path().apply {
            moveTo(cardRect.left, cardRect.height() * 0.5f)
            cubicTo(
                cardRect.width() * 0.3f, cardRect.height() * 0.8f,
                cardRect.width() * 0.7f, cardRect.height() * 0.3f,
                cardRect.right, cardRect.height() * 0.85f
            )
            lineTo(cardRect.right, cardRect.bottom)
            lineTo(cardRect.left, cardRect.bottom)
            close()
        }
        canvas.save()
        canvas.clipRect(cardRect)
        canvas.drawPath(ribbonPath, ribbonPaint)
        canvas.restore()

        // 3. Top Row: Bank Emblem & Bank Name
        val emblemBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.argb(45, 255, 255, 255)
            style = Paint.Style.FILL
        }
        val emblemRect = RectF(cardRect.right - 180f, cardRect.top + 40f, cardRect.right - 50f, cardRect.top + 100f)
        canvas.drawRoundRect(emblemRect, 16f, 16f, emblemBgPaint)

        val bankTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(meta.logoText.ifEmpty { "بانک" }, emblemRect.centerX(), emblemRect.centerY() + 10f, bankTextPaint)

        // Bank Full Name
        val bankNamePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textSize = 34f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText(meta.name, cardRect.right - 200f, cardRect.top + 80f, bankNamePaint)

        // Card Chip Simulation (EMV Chip)
        val chipPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.argb(230, 245, 197, 66)
            style = Paint.Style.FILL
        }
        val chipRect = RectF(cardRect.left + 60f, cardRect.top + 130f, cardRect.left + 150f, cardRect.top + 200f)
        canvas.drawRoundRect(chipRect, 12f, 12f, chipPaint)

        // 4. Center: 16-Digit Card Number (Formatted)
        val formattedCard = IranianBankHelper.formatCardNumber(card.cardNumber)
        val cardNumberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textSize = 48f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            letterSpacing = 0.08f
        }
        canvas.drawText(formattedCard, cardRect.centerX(), cardRect.centerY() + 30f, cardNumberPaint)

        // 5. Bottom Row: Person Name & IBAN / Account
        val nameLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.argb(200, 255, 255, 255)
            textSize = 22f
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("نام صاحب کارت", cardRect.right - 60f, cardRect.bottom - 110f, nameLabelPaint)

        val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textSize = 32f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText(personName, cardRect.right - 60f, cardRect.bottom - 60f, namePaint)

        // IBAN (left aligned at bottom)
        if (card.iban.isNotBlank()) {
            val ibanFormatted = IranianBankHelper.formatIban(card.iban)
            val ibanLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.argb(200, 255, 255, 255)
                textSize = 20f
                textAlign = Paint.Align.LEFT
            }
            canvas.drawText("شماره شبا (IBAN)", cardRect.left + 60f, cardRect.bottom - 110f, ibanLabelPaint)

            val ibanPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
                textSize = 26f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                textAlign = Paint.Align.LEFT
            }
            canvas.drawText(ibanFormatted, cardRect.left + 60f, cardRect.bottom - 60f, ibanPaint)
        } else if (card.accountNumber.isNotBlank()) {
            val accLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.argb(200, 255, 255, 255)
                textSize = 20f
                textAlign = Paint.Align.LEFT
            }
            canvas.drawText("شماره حساب", cardRect.left + 60f, cardRect.bottom - 110f, accLabelPaint)

            val accPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
                textSize = 26f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                textAlign = Paint.Align.LEFT
            }
            canvas.drawText(card.accountNumber, cardRect.left + 60f, cardRect.bottom - 60f, accPaint)
        }

        return bitmap
    }

    /**
     * Saves the card bitmap to cache and triggers an Android ACTION_SEND share intent.
     */
    fun shareCardImage(
        context: Context,
        card: BankCardEntity,
        personName: String
    ): Boolean {
        return try {
            val bitmap = createCardBitmap(card, personName)
            val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
            val file = File(imagesDir, "card_${card.id}_${System.currentTimeMillis()}.png")

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(
                    Intent.EXTRA_TEXT,
                    "${card.bankName} - $personName\n${IranianBankHelper.formatCardNumber(card.cardNumber)}"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "اشتراک‌گذاری تصویر کارت")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
