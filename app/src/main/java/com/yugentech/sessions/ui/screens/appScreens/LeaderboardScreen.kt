package com.yugentech.sessions.ui.screens.appScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.jigaku.ui.components.RankNumber
import com.yugentech.jigaku.ui.components.StudyingIndicator
import com.yugentech.sessions.ui.components.UserInfo
import com.yugentech.sessions.models.User
import com.yugentech.sessions.viewModels.LeaderboardViewModel

@Composable
fun LeaderboardScreen(
    modifier: Modifier = Modifier,
    leaderboardViewModel: LeaderboardViewModel
) {
    val users by leaderboardViewModel.leaderboardUsers.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        leaderboardViewModel.getLeaderboard()
    }

    LeaderboardContent(users = users, modifier = modifier)
}

@Composable
private fun LeaderboardContent(
    users: List<User>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 24.dp, horizontal = 24.dp)
    ) {
        itemsIndexed(
            items = users.sortedByDescending { it.totalTimeStudied },
            key = { _, user -> user.userId }
        ) { index, user ->
            UserLeaderboardCard(rank = index + 1, user = user)
        }
    }
}

@Composable
private fun UserLeaderboardCard(
    rank: Int,
    user: User
) {
    val isTopThree = rank <= 3

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = if (isTopThree)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (isTopThree) 2.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RankNumber(rank, isTopThree)
            UserInfo(
                user.name,
                "${user.totalTimeStudied / 60} min",
                isTopThree,
                modifier = Modifier.weight(1f)
            )
            if (user.isStudying) {
                StudyingIndicator(isTopThree)
            }
        }
    }
}