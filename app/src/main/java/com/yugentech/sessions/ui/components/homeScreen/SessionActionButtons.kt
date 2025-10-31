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
import com.yugentech.sessions.ui.components
import com.yugentech.sessions.ui.corners
import com.yugentech.sessions.ui.elevation
import com.yugentech.sessions.ui.icons
import com.yugentech.sessions.ui.spacing

@Composable
fun SessionActionButtons(
    isStudying: Boolean,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onPlayPause,
        modifier = modifier.size(MaterialTheme.components.fabSize),
        shape = CircleShape,
        containerColor = if (isStudying)
            MaterialTheme.colorScheme.tertiaryContainer
        else
            MaterialTheme.colorScheme.primary,
        contentColor = if (isStudying)
            MaterialTheme.colorScheme.onTertiaryContainer
        else
            MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = MaterialTheme.elevation.level0
        )
    ) {
        Icon(
            imageVector = if (isStudying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isStudying) "Pause" else "Start",
            modifier = Modifier.size(MaterialTheme.icons.mediumLarge)
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
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
    ) {
        ExtendedFloatingActionButton(
            onClick = onStop,
            modifier = Modifier.weight(1f),
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            shape = RoundedCornerShape(MaterialTheme.corners.extraLarge),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = MaterialTheme.elevation.level0
            )
        ) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "Stop",
                modifier = Modifier.size(MaterialTheme.icons.smallMedium)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.xsSmall))
            Text("Stop", style = MaterialTheme.typography.labelMedium)
        }

        ExtendedFloatingActionButton(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = RoundedCornerShape(MaterialTheme.corners.extraLarge),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = MaterialTheme.elevation.level0
            )
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "Save",
                modifier = Modifier.size(MaterialTheme.icons.smallMedium)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.xsSmall))
            Text("Save", style = MaterialTheme.typography.labelMedium)
        }
    }
}