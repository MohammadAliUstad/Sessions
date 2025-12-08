package com.yugentech.sessions // Use your package name

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*

@Composable
fun GreetingScreen() {
    // A simple state: holds the text "Hello" initially
    var message by remember { mutableStateOf("Hello") }

    Column {
        // The text that shows the message
        Text(text = message)

        // The button that changes the message
        Button(onClick = { message = "World" }) {
            Text(text = "Click Me")
        }
    }
}