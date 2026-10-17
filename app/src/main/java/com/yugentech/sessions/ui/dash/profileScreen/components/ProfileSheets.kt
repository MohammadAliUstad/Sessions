package com.yugentech.sessions.ui.dash.profileScreen.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.R
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing
import com.yugentech.sessions.ui.config.editProfileScreen.components.AvatarSection
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// The two profile edits, each in its own sheet (they replaced the Edit Profile screen).
// Same sheet style as the task sheet: roomy drag handle, bold title, content below.

private const val MAX_NAME_LENGTH = 20

// Tapping an avatar applies it straight away and closes the sheet, so there's no Save button.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarPickerSheet(
    selectedAvatarId: Int,
    onAvatarSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        dragHandle = { SheetHandle() }
    ) {
        // No card around the picker: the title and grid sit on the sheet, the tabs run edge to edge.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
        ) {
            Text(
                text = "Choose Your Avatar",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            AvatarSection(
                selectedAvatarId = selectedAvatarId,
                onAvatarSelected = { avatarId ->
                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                    onAvatarSelected(avatarId)
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                }
            )
        }
    }
}

// Same sheet Quill and Ryori use to rename: a filled text field that gets focus once the sheet
// is open, a character count (or the problem with the name), and Cancel / Save at the end.
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditNameSheet(
    currentName: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = currentName,
                selection = TextRange(currentName.length)
            )
        )
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusRequester = remember { FocusRequester() }

    val currentText = textFieldValue.text
    val namePattern = stringResource(R.string.a_za_z0_9_s)
    val nameAllowed = remember(namePattern) { Regex(namePattern) }
    val error = when {
        currentText.isBlank() -> stringResource(R.string.display_name_is_required)
        currentText.trim().length < 2 -> stringResource(R.string.at_least_2_characters_please)
        !currentText.matches(nameAllowed) -> stringResource(R.string.letters_numbers_and_basic_symbols_only)
        else -> null
    }
    val canSave = error == null && currentText.trim() != currentName.trim()

    // Save eases between its disabled and enabled colours instead of snapping, on the theme's
    // effects spring (no bounce). Both states get the same animated colour, so the button's own
    // enabled/disabled switch can't cut the animation short.
    val defaultSaveColors = ButtonDefaults.buttonColors()
    val colorSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Color>()
    val saveContainerColor by animateColorAsState(
        targetValue = if (canSave) defaultSaveColors.containerColor else defaultSaveColors.disabledContainerColor,
        animationSpec = colorSpec,
        label = "saveContainer"
    )
    val saveContentColor by animateColorAsState(
        targetValue = if (canSave) defaultSaveColors.contentColor else defaultSaveColors.disabledContentColor,
        animationSpec = colorSpec,
        label = "saveContent"
    )

    val save = {
        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
        onSave(currentText.trim())
    }

    LaunchedEffect(Unit) {
        snapshotFlow { sheetState.currentValue }
            .filter { it == SheetValue.Expanded }
            .first()
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        dragHandle = { SheetHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Change Your Name",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextField(
                value = textFieldValue,
                onValueChange = {
                    if (it.text.length <= MAX_NAME_LENGTH) textFieldValue = it
                },
                label = { Text("Display Name") },
                supportingText = {
                    // Only complain once something has changed, so the sheet doesn't open on an error.
                    val showError = error != null && currentText != currentName
                    Text(
                        text = if (showError) error.orEmpty() else "${currentText.length} / $MAX_NAME_LENGTH",
                        color = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                shape = RoundedCornerShape(MaterialTheme.corners.medium),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { if (canSave) save() })
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // No haptic on Cancel: only the action button gives feedback.
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = save,
                    enabled = canSave,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = saveContainerColor,
                        contentColor = saveContentColor,
                        disabledContainerColor = saveContainerColor,
                        disabledContentColor = saveContentColor
                    )
                ) { Text("Save") }
            }
        }
    }
}

// Sessions' sheet handle: a small pill with roomy padding, so it's easy to grab.
@Composable
private fun SheetHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
            .padding(vertical = 22.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 32.dp, height = 6.dp)
                .background(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    shape = CircleShape
                )
        )
    }
}
