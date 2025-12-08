package com.yugentech.sessions.ui.dash.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.dimensions.AppConstants
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.dash.components.avatar.AvatarSection
import com.yugentech.sessions.ui.dash.components.avatar.DisplayNameSection
import com.yugentech.sessions.viewModels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    profileViewModel: ProfileViewModel,
    userId: String,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val view = LocalView.current
    val scrollState = rememberScrollState()
    val user = uiState.user

    LaunchedEffect(userId) {
        profileViewModel.loadUser(userId)
    }

    if (user != null) {

        var displayName by remember(user.userId) {
            mutableStateOf(user.name.orEmpty())
        }

        var selectedAvatarId by remember(user.userId) {
            mutableIntStateOf(user.avatarId ?: 0)
        }

        var validationError by remember { mutableStateOf<String?>(null) }

        val canSave = validationError == null

        LaunchedEffect(displayName) {
            validationError = when {
                displayName.isBlank() -> context.getString(R.string.display_name_is_required)
                displayName.length < AppConstants.TWO -> context.getString(R.string.at_least_2_characters_please)
                displayName.length > AppConstants.TWENTY -> context.getString(R.string.keep_it_under_20_characters)
                !displayName.matches(Regex(context.getString(R.string.a_za_z0_9_s))) -> context.getString(R.string.letters_numbers_and_basic_symbols_only)
                else -> null
            }
        }

        fun saveProfile() {
            if (canSave) {
                val updatedUser = user.copy(
                    name = displayName.trim(),
                    avatarId = selectedAvatarId
                )

                profileViewModel.upsertUser(updatedUser)
                profileViewModel.performHaptic(view)
                onNavigateBack()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.edit_profile),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                    actions = {
                        Button(
                            modifier = Modifier.padding(end = MaterialTheme.spacing.s),
                            onClick = { saveProfile() },
                            enabled = canSave,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.save),
                                style = MaterialTheme.typography.labelLarge
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
                    .padding(horizontal = MaterialTheme.spacing.m)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l)
            ) {
                AnimatedVisibility(visible = uiState.errorMessage != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(MaterialTheme.spacing.xl),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = uiState.errorMessage ?: stringResource(R.string.something_went_wrong),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                AvatarSection(
                    selectedAvatarId = selectedAvatarId,
                    onAvatarSelected = { selectedAvatarId = it }
                )

                DisplayNameSection(
                    displayName = displayName,
                    onDisplayNameChange = {
                        if (it.length <= AppConstants.TWENTY) {
                            displayName = it
                        }
                    },
                    validationError = validationError,
                    isSaving = uiState.isSaving
                )
            }
        }
    }
}