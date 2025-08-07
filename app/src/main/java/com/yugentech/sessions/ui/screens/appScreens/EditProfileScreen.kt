package com.yugentech.sessions.ui.screens.appScreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.LoginViewModel
import com.yugentech.sessions.ui.components.avatar.AvatarSection
import com.yugentech.sessions.ui.components.avatar.DisplayNameSection
import com.yugentech.sessions.user.UserResult
import com.yugentech.sessions.user.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val authState by loginViewModel.authState.collectAsStateWithLifecycle()
    val userData by userViewModel.userState.collectAsStateWithLifecycle()
    val updateStatus by userViewModel.updateStatus.collectAsStateWithLifecycle()
    val errorMessage by userViewModel.errorMessage.collectAsStateWithLifecycle()
    var displayName by remember { mutableStateOf("") }
    var selectedAvatarId by remember { mutableIntStateOf(-1) }
    var validationError by remember { mutableStateOf<String?>(null) }
    var isDataLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(authState.userId) {
        authState.userId?.let { userViewModel.loadUser(it) }
    }

    LaunchedEffect(userData) {
        userData?.let { user ->
            if (!isDataLoaded) {
                displayName = user.name ?: ""
                selectedAvatarId = user.avatarId
                isDataLoaded = true
            }
        }
    }

    LaunchedEffect(updateStatus) {
        when (updateStatus) {
            is UserResult.Success -> {
                userViewModel.clearUpdateStatus()
                onNavigateBack()
            }

            is UserResult.Error -> userViewModel.clearUpdateStatus()
            else -> {}
        }
    }

    LaunchedEffect(displayName, isDataLoaded) {
        if (isDataLoaded) {
            validationError = when {
                displayName.isBlank() -> "Display name is required"
                displayName.length < 2 -> "At least 2 characters please"
                displayName.length > 20 -> "Keep it under 20 characters"
                !displayName.matches(Regex("^[a-zA-Z0-9\\s._-]+$")) -> "Letters, numbers and basic symbols only"
                else -> null
            }
        }
    }

    val isSaving = updateStatus is UserResult.Loading
    val canSave =
        validationError == null && authState.userId != null && !isSaving && isDataLoaded && userData != null

    fun saveProfile() {
        if (canSave && userData != null) {
            val updatedUserData = userData!!.copy(
                name = displayName.trim(),
                avatarId = selectedAvatarId,
                pendingSync = true,
                lastSyncTimestamp = System.currentTimeMillis()
            )
            userViewModel.updateUser(updatedUserData)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        enabled = !isSaving
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { saveProfile() },
                        enabled = canSave
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = if (isSaving) "Saving..." else "Save",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            AnimatedVisibility(visible = errorMessage != null) {
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
                            text = errorMessage ?: "Something went wrong",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            if (!isDataLoaded || userData == null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "Loading your profile...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
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