package com.yugentech.sessions.ui.dash.homeScreen.components.dialogs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yugentech.sessions.templates.model.Template
import com.yugentech.sessions.theme.tokens.corners
import com.yugentech.sessions.theme.tokens.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSelectionSheet(
    currentTask: String,
    templates: List<Template>,
    focusDuration: Int,
    shortBreakDuration: Int,
    longBreakDuration: Int,
    targetSets: Int,
    longBreakEnabled: Boolean,
    setsPerLongBreak: Int = 4,
    onSetTask: (String) -> Unit,
    onApplyTemplate: (Template) -> Unit,
    onSaveTemplate: (String) -> Unit,
    onDeleteTemplate: (Long) -> Unit,
    onDismiss: () -> Unit,
    onHaptic: () -> Unit
) {
    var taskName by remember { mutableStateOf(currentTask) }
    val focusManager = LocalFocusManager.current
    var templateToDelete by remember { mutableStateOf<Template?>(null) }
    val scope = rememberCoroutineScope()

    val matchedTemplate = remember(taskName, templates) {
        if (taskName.isBlank()) null
        else templates.find { it.name.equals(taskName.trim(), ignoreCase = true) }
    }
    val isExistingTemplate = matchedTemplate != null
    val hasTask = taskName.isNotBlank()

    val isConfigChanged = remember(
        matchedTemplate,
        focusDuration,
        shortBreakDuration,
        longBreakDuration,
        targetSets,
        longBreakEnabled,
        setsPerLongBreak
    ) {
        if (matchedTemplate == null) true
        else {
            matchedTemplate.focusDuration != focusDuration ||
                    matchedTemplate.shortBreakDuration != shortBreakDuration ||
                    matchedTemplate.longBreakDuration != longBreakDuration ||
                    matchedTemplate.targetSets != targetSets ||
                    matchedTemplate.longBreakEnabled != longBreakEnabled ||
                    matchedTemplate.setsPerLongBreak != setsPerLongBreak
        }
    }

    val setTaskContainerColor by animateColorAsState(
        targetValue = if (hasTask) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surfaceContainerHigh,
        label = "setTaskContainer"
    )
    val setTaskContentColor by animateColorAsState(
        targetValue = if (hasTask) MaterialTheme.colorScheme.onSecondaryContainer
        else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "setTaskContent"
    )
    val saveContainerColor by animateColorAsState(
        targetValue = when {
            !hasTask -> MaterialTheme.colorScheme.surfaceContainerHighest
            isExistingTemplate && isConfigChanged -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
        },
        label = "saveContainer"
    )
    val saveContentColor by animateColorAsState(
        targetValue = when {
            !hasTask -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            isExistingTemplate && isConfigChanged -> MaterialTheme.colorScheme.onTertiary
            else -> MaterialTheme.colorScheme.onPrimary
        },
        label = "saveContent"
    )

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    templateToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { templateToDelete = null },
            title = { Text("Delete Template?", style = MaterialTheme.typography.headlineSmall) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = RoundedCornerShape(MaterialTheme.corners.medium),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = target.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(
                                horizontal = MaterialTheme.spacing.m,
                                vertical = MaterialTheme.spacing.s
                            )
                        )
                    }

                    Text(
                        text = "This template will be permanently removed. This action cannot be undone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            shape = RoundedCornerShape(MaterialTheme.corners.large),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            confirmButton = {
                Button(
                    onClick = {
                        onHaptic()
                        onDeleteTemplate(target.id)
                        templateToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { templateToDelete = null }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        contentWindowInsets = { WindowInsets.systemBars.only(WindowInsetsSides.Top) },
        dragHandle = {
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
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Scrollable section — header, task input, template list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 0.dp)
            ) {
                item(key = "header") {
                    Column(
                        modifier = Modifier.padding(bottom = MaterialTheme.spacing.l),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                    ) {
                        Text(
                            text = "Session Task",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Name your session or pick a saved template.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item(key = "task_section") {
                    Column(
                        modifier = Modifier.padding(bottom = MaterialTheme.spacing.l),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(MaterialTheme.corners.medium)
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = MaterialTheme.spacing.m,
                                    vertical = MaterialTheme.spacing.m
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                            ) {
                                BasicTextField(
                                    value = taskName,
                                    onValueChange = { taskName = it },
                                    modifier = Modifier.weight(1f),
                                    textStyle = MaterialTheme.typography.titleMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                    decorationBox = { innerTextField ->
                                        if (taskName.isEmpty()) {
                                            Text(
                                                text = "e.g. Coding, Math, Reading...",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = FontWeight.Normal
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                                if (taskName.isNotEmpty()) {
                                    IconButton(
                                        onClick = { taskName = "" },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = "Clear",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        FilledTonalButton(
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = setTaskContainerColor,
                                contentColor = setTaskContentColor,
                                disabledContainerColor = setTaskContainerColor,
                                disabledContentColor = setTaskContentColor
                            ),
                            onClick = {
                                onHaptic()
                                scope.launch {
                                    sheetState.hide()
                                    onSetTask(taskName.trim())
                                }
                            }
                        ) {
                            Text("Set Task")
                        }
                    }
                }

                item(key = "templates_header") {
                    Column(modifier = Modifier.padding(bottom = MaterialTheme.spacing.s)) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.height(MaterialTheme.spacing.l))
                        Text(
                            text = "Saved Templates",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (templates.isEmpty()) {
                    item(key = "empty_state") {
                        Column(
                            modifier = Modifier
                                .animateItem()
                                .fillMaxWidth()
                                .padding(vertical = MaterialTheme.spacing.xl),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "No templates yet",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Set your timer config and save it as a template using the button below.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(templates, key = { it.id }) { template ->
                        TemplateCard(
                            template = template,
                            modifier = Modifier
                                .animateItem()
                                .padding(bottom = MaterialTheme.spacing.xs),
                            onClick = {
                                onHaptic()
                                scope.launch {
                                    sheetState.hide()
                                    onApplyTemplate(template)
                                }
                            },
                            onDeleteRequest = {
                                onHaptic()
                                templateToDelete = template
                            }
                        )
                    }
                }
            }

            // Pinned bottom section — never moves
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l)
            ) {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.l)
                ) {
                    CurrentConfigCard(
                        focusDuration = focusDuration,
                        shortBreakDuration = shortBreakDuration,
                        longBreakDuration = longBreakDuration,
                        targetSets = targetSets,
                        setsPerLongBreak = setsPerLongBreak,
                        longBreakEnabled = longBreakEnabled
                    )
                    Button(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = if (isExistingTemplate && !isConfigChanged) true else hasTask,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = saveContainerColor,
                            contentColor = saveContentColor,
                            disabledContainerColor = saveContainerColor,
                            disabledContentColor = saveContentColor
                        ),
                        onClick = {
                            onHaptic()
                            if (isExistingTemplate && !isConfigChanged) {
                                scope.launch {
                                    sheetState.hide()
                                    onDismiss()
                                }
                            } else {
                                onSaveTemplate(taskName.trim())
                            }
                        }
                    ) {
                        Text(
                            text = when {
                                isExistingTemplate && isConfigChanged -> "Update Template"
                                isExistingTemplate && !isConfigChanged -> "Close"
                                else -> "Save as Template"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentConfigCard(
    focusDuration: Int,
    shortBreakDuration: Int,
    longBreakDuration: Int,
    targetSets: Int,
    setsPerLongBreak: Int,
    longBreakEnabled: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s)
    ) {
        Text(
            text = "Current configuration",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ConfigStat(label = "Focus", value = "${focusDuration}m")
                ConfigStat(label = "Break", value = "${shortBreakDuration}m")
                ConfigStat(label = "Sets", value = "$targetSets")
            }
            if (longBreakEnabled) {
                ConfigStat(
                    label = "Long break",
                    value = if (setsPerLongBreak == 1) "${longBreakDuration}m every set" else "${longBreakDuration}m every $setsPerLongBreak sets"
                )
            }
        }
    }
}

@Composable
private fun ConfigStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TemplateCard(
    template: Template,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDeleteRequest: () -> Unit
) {
    val mainSummary = "${template.focusDuration}m focus · ${template.shortBreakDuration}m break · ${template.targetSets} ${if (template.targetSets == 1) "set" else "sets"}"
    val longBreakSummary = if (template.longBreakEnabled) {
        "${template.longBreakDuration}m long break · every ${template.setsPerLongBreak} ${if (template.setsPerLongBreak == 1) "set" else "sets"}"
    } else null

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 14.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Bookmark,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(20.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = mainSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (longBreakSummary != null) {
                    Text(
                        text = longBreakSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                    )
                }
            }
            IconButton(
                onClick = onDeleteRequest,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete template",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
