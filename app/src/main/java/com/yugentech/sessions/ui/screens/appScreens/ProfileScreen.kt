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
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.CircleCropTransformation
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
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
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

                    // ✅ SMALLER, CLEARER PROFILE IMAGE (80dp - 33% smaller)
                    user?.profilePictureUrl?.let { url ->
                        val density = LocalDensity.current
                        val sizePx = with(density) { 80.dp.toPx().toInt() } // ✅ Reduced from 120dp to 80dp

                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(url)
                                .size(Size(sizePx, sizePx))
                                .allowHardware(false)
                                .crossfade(300)
                                .transformations(CircleCropTransformation())
                                .build(),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(80.dp), // ✅ Smaller image size
                            contentScale = ContentScale.Crop,
                            filterQuality = FilterQuality.High,
                            placeholder = painterResource(R.drawable.new_beginnings),
                            error = painterResource(R.drawable.new_beginnings)
                        )
                    } ?: run {
                        // ✅ SMALLER FALLBACK
                        Surface(
                            modifier = Modifier
                                .size(80.dp) // ✅ Match the smaller size
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.new_beginnings),
                                    contentDescription = "Default Profile Picture",
                                    modifier = Modifier.size(40.dp) // ✅ Proportionally smaller
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp)) // ✅ Slightly reduced spacing

                    Text(
                        text = user?.username ?: "Anonymous",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp)) // ✅ Slightly reduced spacing

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
                shape = RoundedCornerShape(16.dp)
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
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier
                .size(100.dp) // ✅ Slightly smaller to match the new proportions
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.new_beginnings),
                    contentDescription = "No sessions illustration",
                    modifier = Modifier.size(60.dp) // ✅ Proportionally smaller
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp)) // ✅ Slightly reduced spacing

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