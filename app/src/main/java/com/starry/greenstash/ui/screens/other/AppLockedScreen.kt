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


package com.starry.greenstash.ui.screens.other

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.starry.greenstash.R
import com.starry.greenstash.ui.theme.greenstashFont
import kotlinx.coroutines.delay


@Composable
fun AppLockedScreen(onAuthRequest: () -> Unit) {
    LaunchedEffect(key1 = true) {
        // Auto trigger the auth request for the first time.
        // We are waiting for 650ms to let the screen load properly.
        delay(650); onAuthRequest()
    }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LockIconCard() // Animated lock icon

        Text(
            text = stringResource(id = R.string.app_lock_screen_title),
            style = MaterialTheme.typography.headlineSmall,
            fontFamily = greenstashFont,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
        )

        Text(
            text = stringResource(id = R.string.app_lock_screen_subtitle),
            style = MaterialTheme.typography.bodySmall,
            fontFamily = greenstashFont,
            modifier = Modifier.padding(horizontal = 42.dp)
        )

        FilledTonalButton(
            onClick = onAuthRequest,
            modifier = Modifier.padding(top = 18.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Fingerprint,
                contentDescription = stringResource(id = R.string.app_lock_button_icon_desc),
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Text(
                text = stringResource(id = R.string.app_lock_button_text),
                fontFamily = greenstashFont,
            )
        }
    }
}

@Composable
private fun LockIconCard() {
    val isAnimated = remember { mutableStateOf(false) }
    val animationSize by animateFloatAsState(
        targetValue = if (isAnimated.value) 1f else 0.8f,
        animationSpec = tween(durationMillis = 500), label = "animationSize"
    )

    LaunchedEffect(key1 = true) {
        delay(300)
        isAnimated.value = true
    }

    Card(
        modifier = Modifier
            .size(350.dp * animationSize)
            .clip(CircleShape)
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = R.drawable.app_lock_icon,
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )
        }
    }

}


@Preview(showBackground = true)
@Composable
private fun AppLockedScreenPV() {
    AppLockedScreen(onAuthRequest = {
        // do nothing
    })
}