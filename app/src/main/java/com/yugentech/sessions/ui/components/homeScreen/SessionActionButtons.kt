package com.yugentech.sessions.ui.components.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SessionActionButtons(
    isStudying: Boolean,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onPlayPause,
        modifier = modifier.size(64.dp),
        shape = CircleShape,
        containerColor = if (isStudying)
            MaterialTheme.colorScheme.tertiaryContainer
        else
            MaterialTheme.colorScheme.primary,
        contentColor = if (isStudying)
            MaterialTheme.colorScheme.onTertiaryContainer
        else
            MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)  // Remove shadow
    ) {
        Icon(
            imageVector = if (isStudying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isStudying) "Pause" else "Start",
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun StudyingControlButtons(
    onStop: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ExtendedFloatingActionButton(
            onClick = onStop,
            modifier = Modifier.weight(1f),
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            shape = RoundedCornerShape(30.dp),
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)  // Remove shadow
        ) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "Stop",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Stop", style = MaterialTheme.typography.labelMedium)
        }

        ExtendedFloatingActionButton(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = RoundedCornerShape(30.dp),
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)  // Remove shadow
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "Save",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Save", style = MaterialTheme.typography.labelMedium)
        }
    }
}
