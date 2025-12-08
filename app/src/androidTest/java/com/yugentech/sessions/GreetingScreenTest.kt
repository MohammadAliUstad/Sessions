package com.yugentech.sessions // Use your package name

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class GreetingScreenTest {

    // 1. The Rule: This fires up the Compose environment
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun clickingButton_changesTextToWorld() {
        // 2. Set the Content: Load the screen we just made
        composeTestRule.setContent {
            GreetingScreen()
        }

        // 3. Check Initial State: "Hello" should be visible
        composeTestRule
            .onNodeWithText("Hello")
            .assertIsDisplayed()

        // 4. Perform Action: Click the button
        composeTestRule
            .onNodeWithText("Click Me")
            .performClick()

        // 5. Check Final State: "World" should now be visible
        composeTestRule
            .onNodeWithText("World")
            .assertIsDisplayed()
            
        // Optional: specific verification that "Hello" is gone
        composeTestRule
            .onNodeWithText("Hello")
            .assertDoesNotExist()
    }
}