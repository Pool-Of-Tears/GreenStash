/**
 * MIT License
 *
 * Copyright (c) [2022 - Present] Stɑrry Shivɑm
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.starry.greenstash.ui.navigation

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

// Duration of the navigation animation
private const val NAVIGATION_ANIM_DURATION = 360

/**
 * Enter transition for the navigation animation
 */
fun enterTransition() = slideInHorizontally(
    initialOffsetX = { NAVIGATION_ANIM_DURATION },
    animationSpec = tween(
        durationMillis = (NAVIGATION_ANIM_DURATION * 1.5).toInt(),
        easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1f)
    )
) + fadeIn(
    animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION,
        delayMillis = NAVIGATION_ANIM_DURATION / 4,
        easing = LinearOutSlowInEasing
    )
)

/**
 * Exit transition for the navigation animation
 */
fun exitTransition() = slideOutHorizontally(
    targetOffsetX = { -NAVIGATION_ANIM_DURATION },
    animationSpec = tween(
        durationMillis = (NAVIGATION_ANIM_DURATION * 1.5).toInt(),
        easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1f)
    )
) + fadeOut(
    animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION,
        delayMillis = NAVIGATION_ANIM_DURATION / 4,
        easing = LinearOutSlowInEasing
    )
)

/**
 * Enter transition for the pop navigation animation
 */
fun popEnterTransition() = slideInHorizontally(
    initialOffsetX = { -NAVIGATION_ANIM_DURATION },
    animationSpec = tween(
        durationMillis = (NAVIGATION_ANIM_DURATION * 1.2).toInt(),
        easing = CubicBezierEasing(0.6f, 0.05f, 0.19f, 0.95f)
    )
) + fadeIn(
    animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION / 2,
        delayMillis = NAVIGATION_ANIM_DURATION / 4,
        easing = LinearEasing
    )
)

/**
 * Exit transition for the pop navigation animation
 */
fun popExitTransition() = slideOutHorizontally(
    targetOffsetX = { NAVIGATION_ANIM_DURATION },
    animationSpec = tween(
        durationMillis = (NAVIGATION_ANIM_DURATION * 1.2).toInt(),
        easing = CubicBezierEasing(0.6f, 0.05f, 0.19f, 0.95f)
    )
) + fadeOut(
    animationSpec = tween(
        durationMillis = NAVIGATION_ANIM_DURATION / 2,
        delayMillis = NAVIGATION_ANIM_DURATION / 4,
        easing = LinearEasing
    )
)