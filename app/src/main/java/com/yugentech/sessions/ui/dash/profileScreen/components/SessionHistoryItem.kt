package com.yugentech.sessions.ui.dash.profileScreen.components

import com.yugentech.sessions.ui.dash.util.displayTaskName
import androidx.compose.ui.platform.LocalContext
import com.yugentech.sessions.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.yugentech.sessions.sessions.model.Session
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.mainScreen.components.itemShape
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SessionHistoryItem(
    session: Session,
    index: Int,
    totalCount: Int,
    onDelete: () -> Unit
) {
    val shape = itemShape(index, totalCount)

    // The phone's own 12 or 24 hour clock, in the app's language
    val formattedTime = android.text.format.DateFormat.getTimeFormat(LocalContext.current).format(Date(session.timestamp))

    val hours = session.duration / 3600
    val minutes = (session.duration % 3600) / 60

    val durationText = when {
        hours > 0 && minutes == 0 -> stringResource(R.string.duration_hours_short, hours.toInt())
        hours > 0 -> stringResource(R.string.duration_hours_minutes_short, hours.toInt(), minutes.toInt())
        else -> stringResource(R.string.duration_minutes_short, minutes.toInt())
    }

    ListItem(
        headlineContent = {
            Text(
                text = displayTaskName(session.sessionTask),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(MaterialTheme.corners.small),
                    modifier = Modifier.padding(end = MaterialTheme.spacing.s)
                ) {
                    Text(
                        text = durationText,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(
                            horizontal = MaterialTheme.spacing.s,
                            vertical = MaterialTheme.spacing.xs
                        )
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(MaterialTheme.components.buttonMedium)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(MaterialTheme.icons.mediumSmall)
                    )
                }
            }
        },
        modifier = Modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainer),
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}