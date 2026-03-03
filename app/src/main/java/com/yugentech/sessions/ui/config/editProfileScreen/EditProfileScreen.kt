package com.yugentech.sessions.ui.config.editProfileScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.components
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.config.editProfileScreen.components.AvatarSection
import com.yugentech.sessions.ui.config.editProfileScreen.components.DisplayNameSection
import com.yugentech.sessions.ui.dash.mainScreen.components.SectionHeader
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
    val user = uiState.user

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberScrollState()

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

        val canSave = validationError == null && displayName.isNotBlank()

        LaunchedEffect(displayName) {
            validationError = when {
                displayName.isBlank() -> context.getString(R.string.display_name_is_required)
                displayName.length < 2 -> context.getString(R.string.at_least_2_characters_please)
                displayName.length > 20 -> context.getString(R.string.keep_it_under_20_characters)
                !displayName.matches(Regex(context.getString(R.string.a_za_z0_9_s))) -> context.getString(
                    R.string.letters_numbers_and_basic_symbols_only
                )

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
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentWindowInsets = WindowInsets.statusBars,
            topBar = {
                LargeTopAppBar(
                    title = {
                        Column {
                            Text("Edit Profile")
                            Text(
                                "Change your avatar and name",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                    SectionHeader(
                        icon = Icons.Default.Face,
                        title = "Choose Your Avatar"
                    )
                    AvatarSection(
                        selectedAvatarId = selectedAvatarId,
                        onAvatarSelected = { selectedAvatarId = it }
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                ) {
                    SectionHeader(
                        icon = Icons.Default.Person,
                        title = "Display Name"
                    )
                    DisplayNameSection(
                        displayName = displayName,
                        onDisplayNameChange = {
                            if (it.length <= 20) {
                                displayName = it
                            }
                        },
                        validationError = validationError,
                        isSaving = uiState.isSaving
                    )
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.m))

                Button(
                    onClick = { saveProfile() },
                    enabled = canSave && !uiState.isSaving,
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(MaterialTheme.components.buttonMedium),
                    shape = RoundedCornerShape(MaterialTheme.corners.extraLarge),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = "Save Changes",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}