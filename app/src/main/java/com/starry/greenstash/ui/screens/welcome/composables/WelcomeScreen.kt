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


package com.starry.greenstash.ui.screens.welcome.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.starry.greenstash.R
import com.starry.greenstash.ui.common.CurrencyPicker
import com.starry.greenstash.ui.navigation.DrawerScreens
import com.starry.greenstash.ui.screens.welcome.viewmodels.WelcomeViewModel
import com.starry.greenstash.ui.theme.greenstashFont

@ExperimentalMaterial3Api
@Composable
fun WelcomeScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: WelcomeViewModel = hiltViewModel()

    val currencyDialog = remember { mutableStateOf(false) }
    val currencyNames = context.resources.getStringArray(R.array.currency_names)
    val currencyValues = context.resources.getStringArray(R.array.currency_values)


    val selectedCurrencyName = remember {
        mutableStateOf(currencyNames[currencyValues.indexOf(viewModel.getDefaultCurrencyValue())])
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        val compositionResult: LottieCompositionResult =
            rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(R.raw.welcome_lottie)
            )
        val progressAnimation by animateLottieCompositionAsState(
            compositionResult.value,
            isPlaying = true,
            iterations = 1,
            speed = 1f
        )

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            LottieAnimation(
                composition = compositionResult.value,
                progress = progressAnimation,
                modifier = Modifier.size(320.dp),
                enableMergePaths = true
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    4.dp
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.welcome_screen_text),
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    fontFamily = greenstashFont,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                )

                Row {

                }

                OutlinedButton(
                    onClick = { currencyDialog.value = true },
                    modifier = Modifier.animateContentSize(),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = selectedCurrencyName.value,
                        fontFamily = greenstashFont,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

        }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            FilledTonalButton(
                onClick = {
                    viewModel.saveOnBoardingState(completed = true)
                    navController.popBackStack()
                    navController.navigate(DrawerScreens.Home.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.welcome_screen_button),
                    fontFamily = greenstashFont,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        CurrencyPicker(
            defaultCurrencyValue = viewModel.getDefaultCurrencyValue()
                ?: currencyValues.first(),
            currencyNames = currencyNames,
            currencyValues = currencyValues,
            showBottomSheet = currencyDialog,
            onCurrencySelected = { newValue ->
                viewModel.setDefaultCurrency(newValue)
                selectedCurrencyName.value = currencyNames[currencyValues.indexOf(newValue)]
            }
        )

    }
}