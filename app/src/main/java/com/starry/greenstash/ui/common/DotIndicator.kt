package com.starry.greenstash.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DotIndicator(modifier: Modifier = Modifier, color: Color) {
    val glowColor by animateColorAsState(
        targetValue = color.copy(alpha = 0.5f),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = modifier) {
        val radius = size.width / 2

        drawCircle(
            color = color,
            radius = radius,
            center = Offset(size.width / 2, size.height / 2)
        )

        drawCircle(
            color = glowColor,
            radius = radius * 1.5f,
            style = Stroke(width = 4.dp.toPx()),
            center = Offset(size.width / 2, size.height / 2)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DotIndicatorPV() {
    DotIndicator(modifier = Modifier.size(18.dp), color = Color.Red)
}