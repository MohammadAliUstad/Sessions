package com.yugentech.sessions.ui.components.profileScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.models.Session

@Composable
fun SessionList(
    sessions: List<Session>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp) // Slightly more space for clarity
    ) {
        sessions.forEach { session ->
            SessionCard(session = session)
        }
    }
}