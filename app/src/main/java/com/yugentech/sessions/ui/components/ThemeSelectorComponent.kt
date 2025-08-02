package com.yugentech.sessions.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.ui.theme.ColorTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ThemeSelector(
    modifier: Modifier = Modifier,
    viewModel: ThemeViewModel = koinViewModel()
) {
    val themeConfig by viewModel.themeConfig.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    // Show error if any
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // You can show a snackbar here or handle error as needed
            viewModel.clearError()
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Color Theme",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ColorTheme.entries.toTypedArray()) { colorTheme ->
                        ThemeOptionItem(
                            colorTheme = colorTheme,
                            isSelected = themeConfig.colorTheme == colorTheme,
                            onSelected = {
                                viewModel.updateColorTheme(colorTheme)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeOptionItem(
    colorTheme: ColorTheme,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val displayName = when (colorTheme) {
        ColorTheme.DYNAMIC -> "Dynamic (Material You)"
        ColorTheme.MONOCHROME -> "Monochrome"
        ColorTheme.BLUE -> "Blue"
        ColorTheme.GREEN -> "Green"
        ColorTheme.ORANGE -> "Orange"
        ColorTheme.PURPLE -> "Purple"
        ColorTheme.TEAL -> "Teal"
    }

    val previewColor = when (colorTheme) {
        ColorTheme.DYNAMIC -> MaterialTheme.colorScheme.primary
        ColorTheme.MONOCHROME -> Color(0xFF808080)
        ColorTheme.BLUE -> Color(0xFF1976D2)
        ColorTheme.GREEN -> Color(0xFF388E3C)
        ColorTheme.ORANGE -> Color(0xFFF57C00)
        ColorTheme.PURPLE -> Color(0xFF7B1FA2)
        ColorTheme.TEAL -> Color(0xFF00796B)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() },
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        else
            Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Preview Circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(previewColor)
                    .border(
                        BorderStroke(
                            2.dp,
                            if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Theme Name
            Text(
                text = displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            // Selection indicator
            if (isSelected) {
                Surface(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}