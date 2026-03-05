package com.yugentech.sessions.ui.dash.mainScreen.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import com.yugentech.sessions.theme.tokens.corners

@Composable
fun itemShape(index: Int, count: Int): Shape {
    val largeCorner = MaterialTheme.corners.large
    val smallCorner = MaterialTheme.corners.small

    return when {
        count == 1 -> RoundedCornerShape(largeCorner)
        index == 0 -> RoundedCornerShape(
            topStart = largeCorner,
            topEnd = largeCorner,
            bottomStart = smallCorner,
            bottomEnd = smallCorner
        )

        index == count - 1 -> RoundedCornerShape(
            topStart = smallCorner,
            topEnd = smallCorner,
            bottomStart = largeCorner,
            bottomEnd = largeCorner
        )

        else -> RoundedCornerShape(smallCorner)
    }
}