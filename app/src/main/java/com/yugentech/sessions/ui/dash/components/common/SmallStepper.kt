package com.yugentech.sessions.ui.dash.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SmallStepper(value: Int, onValueChange: (Int) -> Unit, range: IntRange, step: Int = 1) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = { if (value - step >= range.first) onValueChange(value - step) },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .size(32.dp)
        ) { Icon(Icons.Default.Remove, "Decrease", modifier = Modifier.size(16.dp)) }

        Text(
            text = "$value",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        IconButton(
            onClick = { if (value + step <= range.last) onValueChange(value + step) },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                .size(32.dp)
        ) { Icon(Icons.Default.Add, "Increase", modifier = Modifier.size(16.dp)) }
    }
}