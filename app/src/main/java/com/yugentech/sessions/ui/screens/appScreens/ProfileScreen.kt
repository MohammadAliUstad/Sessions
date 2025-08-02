package com.yugentech.sessions.ui.screens.appScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.R
import com.yugentech.sessions.ui.components.profileScreen.StudyTimeSection
import com.yugentech.sessions.authentication.AuthViewModel
import com.yugentech.sessions.session.SessionViewModel
import com.yugentech.sessions.ui.components.profileScreen.SessionList
import com.yugentech.sessions.utils.formatTime

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    sessionViewModel: SessionViewModel,
    onEditProfile: () -> Unit = {}
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val sessions by sessionViewModel.sessions.collectAsStateWithLifecycle()
    val totalTime by sessionViewModel.totalTime.collectAsStateWithLifecycle()
    val user = authState.userData

    LaunchedEffect(user?.userId) {
        user?.userId?.let { id ->
            sessionViewModel.getSessions(id)
            sessionViewModel.getTotalTime(id)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp), // ✅ Reduced from 24dp to 20dp
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onEditProfile
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // ✅ NAME ABOVE ILLUSTRATION - CLOSER TO TOP
                    Text(
                        text = user?.username ?: "Anonymous",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp)) // ✅ Reduced from 16dp to 12dp

                    // ✅ EVEN LARGER ILLUSTRATION
                    Surface(
                        modifier = Modifier
                            .size(180.dp) // ✅ Increased from 140dp to 180dp
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            val avatarRes = getAvatarResource(user?.profileAvatarId ?: "1")
                            Image(
                                painter = painterResource(id = avatarRes),
                                contentDescription = "Profile Avatar",
                                modifier = Modifier.size(130.dp) // ✅ Increased from 100dp to 130dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp)) // ✅ Reduced from 12dp to 8dp

                    // ✅ ILLUSTRATION NAME BELOW AVATAR
                    Text(
                        text = getAvatarName(user?.profileAvatarId ?: "1"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp)) // ✅ Reduced from 24dp to 16dp

                    StudyTimeSection(
                        formattedTime = formatTime(totalTime)
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Recent Sessions",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (sessions.isEmpty()) {
                        EmptySessionsIllustration(
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        SessionList(
                            sessions = sessions,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptySessionsIllustration(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp), // ✅ Reduced from 32dp to 24dp
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ✅ EVEN LARGER ILLUSTRATION FOR EMPTY STATE
        Surface(
            modifier = Modifier
                .size(200.dp) // ✅ Increased from 160dp to 200dp
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.new_beginnings),
                    contentDescription = "No sessions illustration",
                    modifier = Modifier.size(150.dp) // ✅ Increased from 120dp to 150dp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp)) // ✅ Reduced from 24dp to 20dp

        Text(
            text = "No sessions yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Start your first session to track your progress and build productive habits!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun getAvatarResource(avatarId: String): Int {
    return when (avatarId) {
        "1" -> R.drawable.peep_100     // Wise Elder
        "2" -> R.drawable.peep_101     // Doc Life
        "3" -> R.drawable.peep_17      // Dapper Gent
        "4" -> R.drawable.peep_2       // Lost Soul
        "5" -> R.drawable.peep_27      // Beard Boss
        "6" -> R.drawable.peep_47      // Teeth Grind
        "7" -> R.drawable.peep_6       // Smart Hijabi
        "8" -> R.drawable.peep_85      // Cool Singh
        "9" -> R.drawable.peep_93      // Bored Bun
        else -> R.drawable.peep_27     // Default to Beard Boss
    }
}

@Composable
fun getAvatarName(avatarId: String): String {
    return when (avatarId) {
        "1" -> "Wise Elder"
        "2" -> "Doc Life"
        "3" -> "Dapper Gent"
        "4" -> "Lost Soul"
        "5" -> "Beard Boss"
        "6" -> "Teeth Grind"
        "7" -> "Smart Hijabi"
        "8" -> "Cool Singh"
        "9" -> "Bored Bun"
        else -> "Beard Boss"
    }
}