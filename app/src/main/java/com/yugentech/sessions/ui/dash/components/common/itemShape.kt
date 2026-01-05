package com.yugentech.sessions.ui.dash.components.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun itemShape(index: Int, count: Int): Shape {
    val largeCorner = 24.dp
    val smallCorner = 4.dp

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