package com.yugentech.sessions.ui.dash.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.ui.dash.components.avatar.AvatarSection
import com.yugentech.sessions.ui.dash.components.avatar.DisplayNameSection
import com.yugentech.sessions.user.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    val userState by userViewModel.userState.collectAsStateWithLifecycle()
    val view = LocalView.current
    val user = userState.user

    if (user != null) {
        var displayName by remember(user.userId) { mutableStateOf(user.name.orEmpty()) }
        var selectedAvatarId by remember(user.userId) { mutableIntStateOf(user.avatarId ?: 0) }
        var validationError by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(displayName) {
            validationError = when {
                displayName.isBlank() -> "Display name is required"
                displayName.length < 2 -> "At least 2 characters please"
                displayName.length > 20 -> "Keep it under 20 characters"
                !displayName.matches(Regex("^[a-zA-Z0-9\\s._-]+$")) -> "Letters, numbers, and basic symbols only"
                else -> null
            }
        }

        val isSaving = userState.isLoading
        val canSave = validationError == null && !isSaving

        fun saveProfile() {
            if (canSave) {
                val updatedUser = user.copy(
                    name = displayName.trim(),
                    avatarId = selectedAvatarId
                )
                userViewModel.upsertUser(updatedUser)
            }
            onNavigateBack()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Profile", style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack, enabled = !isSaving) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = {
                                saveProfile()
                                userViewModel.performHaptic(view)
                            },
                            enabled = canSave
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = if (isSaving) "Saving..." else "Save",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AnimatedVisibility(visible = userState.errorMessage != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = userState.errorMessage ?: "Something went wrong",
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                AvatarSection(
                    selectedAvatarId = selectedAvatarId,
                    onAvatarSelected = { if (!isSaving) selectedAvatarId = it }
                )

                DisplayNameSection(
                    displayName = displayName,
                    onDisplayNameChange = { if (it.length <= 20 && !isSaving) displayName = it },
                    validationError = validationError,
                    isSaving = isSaving
                )
            }
        }
    }
}