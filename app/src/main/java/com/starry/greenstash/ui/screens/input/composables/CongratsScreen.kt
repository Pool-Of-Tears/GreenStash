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


package com.starry.greenstash.ui.screens.input.composables

import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.starry.greenstash.R
import com.starry.greenstash.ui.common.SlideInAnimatedContainer
import com.starry.greenstash.ui.navigation.DrawerScreens
import com.starry.greenstash.ui.theme.greenstashFont

@Composable
fun CongratsScreen(navController: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        val context = LocalContext.current
        LaunchedEffect(key1 = true, block = {
            val mediaPlayer = MediaPlayer.create(context, R.raw.congrats_sound)
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener {
                mediaPlayer.release() // release the media player on completion.
            }
        })

        BackHandler {
            navController.popBackStack(DrawerScreens.Home.route, true)
            navController.navigate(DrawerScreens.Home.route)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val compositionResult: LottieCompositionResult = rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(R.raw.congrats_lottie)
            )
            val progressAnimation by animateLottieCompositionAsState(
                compositionResult.value,
                isPlaying = true,
                iterations = LottieConstants.IterateForever,
                speed = 1f
            )

            LottieAnimation(
                composition = compositionResult.value,
                progress = progressAnimation,
                modifier = Modifier.size(360.dp),
                enableMergePaths = true
            )

            SlideInAnimatedContainer(initialDelay = 1200) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            4.dp
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text(
                            text = stringResource(id = R.string.goal_achieved_heading),
                            style = MaterialTheme.typography.headlineSmall,
                            fontFamily = greenstashFont,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(id = R.string.goal_achieved_subtext),
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = greenstashFont,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }

            SlideInAnimatedContainer(initialDelay = 3600) {
                FilledTonalButton(
                    onClick = {
                        navController.popBackStack(DrawerScreens.Home.route, true)
                        navController.navigate(DrawerScreens.Home.route)
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.goal_achieved_button))
                }
            }
        }
    }
}

@Preview
@Composable
private fun PV() {
    CongratsScreen(rememberNavController())
}