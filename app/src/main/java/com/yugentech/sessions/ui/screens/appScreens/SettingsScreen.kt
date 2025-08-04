package com.yugentech.sessions.ui.screens.appScreens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugentech.sessions.theme.ThemeViewModel
import com.yugentech.sessions.theme.utils.ColorTheme
import com.yugentech.sessions.theme.utils.ThemeMode
import org.koin.androidx.compose.koinViewModel

@Composable
fun ThemeSelector(
    modifier: Modifier = Modifier,
    viewModel: ThemeViewModel = koinViewModel()
) {
    val themeConfig by viewModel.themeConfiguration.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    // Get current primary color for dynamic theme
    val currentPrimary = MaterialTheme.colorScheme.primary
    val currentTertiary = MaterialTheme.colorScheme.tertiary

    // Create theme options with current dynamic colors
    val themeOptions = remember(currentPrimary, currentTertiary) {
        createThemeOptions(currentPrimary, currentTertiary)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Color Theme",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                // Grid of theme options
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.height(240.dp)
                ) {
                    items(themeOptions) { themeOption ->
                        ThemeCard(
                            themeOption = themeOption,
                            isSelected = themeConfig.colorTheme == themeOption.colorTheme,
                            onClick = {
                                viewModel.updateColorTheme(themeOption.colorTheme)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeCard(
    themeOption: ThemeOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(200),
        label = "scale"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
        animationSpec = tween(200),
        label = "border"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        else
            MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Color preview with gradient
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(themeOption.gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Surface(
                        modifier = Modifier.size(24.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.9f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = themeOption.primaryColor,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Theme name
            Text(
                text = themeOption.displayName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

// Data class for theme options
data class ThemeOption(
    val colorTheme: ColorTheme,
    val displayName: String,
    val primaryColor: Color,
    val gradientColors: List<Color>
)

// ✅ Fixed: Regular function (not @Composable)
fun createThemeOptions(currentPrimary: Color, currentTertiary: Color): List<ThemeOption> = listOf(
    ThemeOption(
        colorTheme = ColorTheme.DYNAMIC,
        displayName = "Dynamic",
        primaryColor = currentPrimary,
        gradientColors = listOf(
            currentPrimary,
            currentTertiary,
            currentPrimary.copy(alpha = 0.7f)
        )
    ),
    ThemeOption(
        colorTheme = ColorTheme.MONOCHROME,
        displayName = "Monochrome",
        primaryColor = Color(0xFF1C1B1F),
        gradientColors = listOf(
            Color(0xFF1C1B1F),
            Color(0xFF49454F),
            Color(0xFF79747E)
        )
    ),
    ThemeOption(
        colorTheme = ColorTheme.BLUE,
        displayName = "Ocean",
        primaryColor = Color(0xFF0061A4),
        gradientColors = listOf(
            Color(0xFF0061A4),
            Color(0xFF6B5778),
            Color(0xFF9ECAFF)
        )
    ),
    ThemeOption(
        colorTheme = ColorTheme.GREEN,
        displayName = "Forest",
        primaryColor = Color(0xFF006E1C),
        gradientColors = listOf(
            Color(0xFF006E1C),
            Color(0xFF38656A),
            Color(0xFF97F593)
        )
    ),
    ThemeOption(
        colorTheme = ColorTheme.ORANGE,
        displayName = "Sunset",
        primaryColor = Color(0xFF8C4A00),
        gradientColors = listOf(
            Color(0xFF8C4A00),
            Color(0xFF5D5E2F),
            Color(0xFFFFB86E)
        )
    ),
    ThemeOption(
        colorTheme = ColorTheme.PURPLE,
        displayName = "Royal",
        primaryColor = Color(0xFF6750A4),
        gradientColors = listOf(
            Color(0xFF6750A4),
            Color(0xFF7D5260),
            Color(0xFFD0BCFF)
        )
    ),
    ThemeOption(
        colorTheme = ColorTheme.TEAL,
        displayName = "Aqua",
        primaryColor = Color(0xFF006A6B),
        gradientColors = listOf(
            Color(0xFF006A6B),
            Color(0xFF4D6042),
            Color(0xFF4FD8D9)
        )
    )
)

@Composable
fun SettingsCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            content = content
        )
    }
}

@Composable
fun SettingsSectionHeader(
    icon: ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SettingsToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingsDropdownItem(
    title: String,
    subtitle: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedOption,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsClickableItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ThemeModeSelector(
    modifier: Modifier = Modifier,
    viewModel: ThemeViewModel = koinViewModel()
) {
    val themeConfig by viewModel.themeConfiguration.collectAsStateWithLifecycle()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Brightness6,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Theme Mode",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Theme mode options
            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeMode.entries.forEach { mode ->
                    val (icon, title, subtitle) = when (mode) {
                        ThemeMode.LIGHT -> Triple(
                            Icons.Default.LightMode,
                            "Light",
                            "Always use light appearance"
                        )

                        ThemeMode.DARK -> Triple(
                            Icons.Default.DarkMode,
                            "Dark",
                            "Always use dark appearance"
                        )

                        ThemeMode.SYSTEM -> Triple(
                            Icons.Default.AutoMode,
                            "System",
                            "Match system appearance"
                        )
                    }

                    ThemeModeOption(
                        icon = icon,
                        title = title,
                        subtitle = subtitle,
                        isSelected = themeConfig.themeMode == mode,
                        onClick = { viewModel.updateThemeMode(mode) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeModeOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        else
            MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onAbout: () -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var studyRemindersEnabled by remember { mutableStateOf(false) }
    var breakRemindersEnabled by remember { mutableStateOf(true) }
    var selectedAppIcon by remember { mutableStateOf("Default") }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }

    val appIconOptions = listOf("Default", "Minimal", "Classic", "Modern")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Notifications Section
            item {
                SettingsCard {
                    SettingsSectionHeader(
                        icon = Icons.Default.Notifications,
                        title = "Notifications"
                    )

                    SettingsToggleItem(
                        title = "Enable Notifications",
                        subtitle = "Allow Sessions to send you notifications",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )

                    if (notificationsEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))

                        SettingsToggleItem(
                            title = "Study Reminders",
                            subtitle = "Get notified to start your study sessions",
                            checked = studyRemindersEnabled,
                            onCheckedChange = { studyRemindersEnabled = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        SettingsToggleItem(
                            title = "Break Reminders",
                            subtitle = "Get reminded to take breaks during long sessions",
                            checked = breakRemindersEnabled,
                            onCheckedChange = { breakRemindersEnabled = it }
                        )
                    }
                }
            }

            // Audio & Haptics Section
            item {
                SettingsCard {
                    SettingsSectionHeader(
                        icon = Icons.Default.VolumeUp,
                        title = "Audio & Haptics"
                    )

                    SettingsToggleItem(
                        title = "Sound Effects",
                        subtitle = "Play sounds for timer events and interactions",
                        checked = soundEnabled,
                        onCheckedChange = { soundEnabled = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsToggleItem(
                        title = "Haptic Feedback",
                        subtitle = "Feel vibrations for timer events",
                        checked = vibrationEnabled,
                        onCheckedChange = { vibrationEnabled = it }
                    )
                }
            }

            // Appearance Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Theme Mode Selector
                    ThemeModeSelector(viewModel = themeViewModel)

                    // Color Theme Selector
                    ThemeSelector(viewModel = themeViewModel)
                }
            }

            // App Customization Section
            item {
                SettingsCard {
                    SettingsSectionHeader(
                        icon = Icons.Default.Apps,
                        title = "Customization"
                    )

                    SettingsDropdownItem(
                        title = "App Icon",
                        subtitle = "Customize your home screen icon",
                        options = appIconOptions,
                        selectedOption = selectedAppIcon,
                        onOptionSelected = { selectedAppIcon = it }
                    )
                }
            }

            // Data & Privacy Section
            item {
                SettingsCard {
                    SettingsSectionHeader(
                        icon = Icons.Default.Security,
                        title = "Data & Privacy"
                    )

                    SettingsClickableItem(
                        title = "Export Data",
                        subtitle = "Export your study data and statistics",
                        onClick = { /* Handle export */ }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsClickableItem(
                        title = "Privacy Policy",
                        subtitle = "Learn how we protect your data",
                        onClick = { /* Handle privacy policy */ }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsClickableItem(
                        title = "Clear Data",
                        subtitle = "Reset all app data and preferences",
                        onClick = { /* Handle clear data */ }
                    )
                }
            }

            // Support Section
            item {
                SettingsCard {
                    SettingsSectionHeader(
                        icon = Icons.Default.Help,
                        title = "Support"
                    )

                    SettingsClickableItem(
                        title = "Help Center",
                        subtitle = "Get help and learn how to use Sessions",
                        onClick = { /* Handle help */ }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsClickableItem(
                        title = "Contact Support",
                        subtitle = "Get in touch with our support team",
                        onClick = { /* Handle contact */ }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsClickableItem(
                        title = "Rate App",
                        subtitle = "Leave a review on the Play Store",
                        onClick = { /* Handle rating */ }
                    )
                }
            }

            // About Section
            item {
                SettingsCard {
                    SettingsSectionHeader(
                        icon = Icons.Default.Info,
                        title = "About"
                    )

                    SettingsClickableItem(
                        title = "About Sessions",
                        subtitle = "Learn more about the app and developer",
                        onClick = onAbout
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsClickableItem(
                        title = "Version",
                        subtitle = "1.0.0 (Build 1)",
                        onClick = { /* Handle version info */ }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsClickableItem(
                        title = "Terms of Service",
                        subtitle = "Read our terms and conditions",
                        onClick = { /* Handle terms */ }
                    )
                }
            }

            // Add some bottom padding
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}