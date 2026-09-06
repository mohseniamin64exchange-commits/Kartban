package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AvatarView(
    kind: String,
    size: Dp = 50.dp,
    modifier: Modifier = Modifier
) {
    val bgColors = when (kind) {
        "store" -> listOf(Color(0xFFE2EFFF), Color(0xFFC7DEFE))
        "company" -> listOf(Color(0xFFEDF4FF), Color(0xFFD7E7FF))
        "woman" -> listOf(Color(0xFFFCE7F3), Color(0xFFFBCFE8))
        else -> listOf(Color(0xFFEDF4FF), Color(0xFFD7E7FF))
    }

    val iconColor = when (kind) {
        "store" -> Color(0xFF2563EB)
        "company" -> Color(0xFF1E40AF)
        "woman" -> Color(0xFFDB2777)
        else -> Color(0xFF3D69B6)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(brush = Brush.linearGradient(colors = bgColors)),
        contentAlignment = Alignment.Center
    ) {
        when (kind) {
            "store" -> Icon(
                imageVector = Icons.Default.Storefront,
                contentDescription = "Store",
                tint = iconColor,
                modifier = Modifier.size(size * 0.6f)
            )
            "company" -> Icon(
                imageVector = Icons.Default.Business,
                contentDescription = "Company",
                tint = iconColor,
                modifier = Modifier.size(size * 0.6f)
            )
            else -> Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Person",
                tint = iconColor,
                modifier = Modifier.size(size * 0.65f)
            )
        }
    }
}
