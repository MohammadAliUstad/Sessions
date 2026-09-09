package com.yugentech.sessions.ui.config.editProfileScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.icons
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.config.editProfileScreen.components.AvatarSection
import com.yugentech.sessions.ui.config.editProfileScreen.components.DisplayNameSection
import com.yugentech.sessions.ui.dash.mainScreen.components.SectionHeader
import com.yugentech.sessions.viewModels.ProfileViewModel
import kotlin.compareTo

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

        val hasUnsavedChanges = displayName.trim() != user.name.orEmpty().trim() ||
                selectedAvatarId != (user.avatarId ?: 0)

        val buttonContainerColor by animateColorAsState(
            targetValue = if (hasUnsavedChanges && canSave)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surfaceVariant,
            animationSpec = tween(300),
            label = "saveButtonContainer"
        )
        val buttonContentColor by animateColorAsState(
            targetValue = if (hasUnsavedChanges && canSave)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            animationSpec = tween(300),
            label = "saveButtonContent"
        )

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
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
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
                        onAvatarSelected = {
                            selectedAvatarId = it
                            profileViewModel.performHaptic(view)
                        }
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
                    enabled = hasUnsavedChanges && canSave && !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonContainerColor,
                        contentColor = buttonContentColor,
                        disabledContainerColor = buttonContainerColor,
                        disabledContentColor = buttonContentColor
                    )
                ) {
                    Text(
                        text = "Save Changes",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                    SectionHeader(
                        icon = Icons.Default.Email,
                        title = "Connected Account"
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        shape = RoundedCornerShape(MaterialTheme.corners.extraLarge)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(MaterialTheme.spacing.l),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(MaterialTheme.icons.medium)
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
                            ) {
                                Text(
                                    text = "Email address",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = user.email ?: "Not available",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                    SectionHeader(
                        icon = Icons.Default.EmojiEvents,
                        title = "Achievements"
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        shape = RoundedCornerShape(MaterialTheme.corners.extraLarge)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(MaterialTheme.spacing.l),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                modifier = Modifier.size(MaterialTheme.icons.extraLarge)
                            )
                            Text(
                                text = "Coming Soon",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Earn badges as you build your focus habits",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}