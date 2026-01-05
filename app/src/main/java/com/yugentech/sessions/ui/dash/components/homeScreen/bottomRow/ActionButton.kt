package com.yugentech.sessions.ui.dash.components.homeScreen.bottomRow

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ActionButton(
    isStudying: Boolean,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledIconToggleButton(
        checked = isStudying,
        onCheckedChange = { onPlayPause() },
        modifier = modifier.size(80.dp),
        shapes = IconToggleButtonShapes(
            // Idle (Play) -> Circle
            shape = CircleShape,
            pressedShape = CircleShape,

            // Running (Pause) -> Squircle (Opposite of Side Buttons in this state)
            checkedShape = RoundedCornerShape(30)
        ),
        colors = IconButtonDefaults.filledIconToggleButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            checkedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            checkedContentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Icon(
            imageVector = if (isStudying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isStudying) "Pause" else "Start",
            modifier = Modifier.size(32.dp)
        )
    }
}