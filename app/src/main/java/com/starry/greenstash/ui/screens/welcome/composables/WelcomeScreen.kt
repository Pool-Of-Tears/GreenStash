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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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
import com.starry.greenstash.ui.navigation.DrawerScreens
import com.starry.greenstash.ui.screens.welcome.viewmodels.WelcomeViewModel
import com.starry.greenstash.ui.theme.greenstashFont

@Composable
fun WelcomeScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: WelcomeViewModel = hiltViewModel()

    val currencyEntries = context.resources.getStringArray(R.array.currency_entries)
    val currencyValues = context.resources.getStringArray(R.array.currency_values)
    val currencyValue = currencyEntries[currencyValues.indexOf(viewModel.getDefaultCurrencyValue())]

    val currencyDialog = remember { mutableStateOf(false) }
    val (selectedCurrencyOption, onCurrencyOptionSelected) = remember {
        mutableStateOf(currencyValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
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

        Spacer(modifier = Modifier.weight(1f))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            LottieAnimation(
                composition = compositionResult.value,
                progress = progressAnimation,
                modifier = Modifier.size(300.dp),
                enableMergePaths = true
            )
        }
        Text(
            text = stringResource(id = R.string.welcome_screen_text),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            fontFamily = greenstashFont,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp, end = 35.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedButton(
                onClick = { currencyDialog.value = true },
                modifier = Modifier
                    .padding(top = 50.dp, bottom = 16.dp)
                    .height(50.dp)
                    .fillMaxWidth(0.8f),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = currencyValue,
                    fontSize = 16.sp,
                    fontFamily = greenstashFont,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Button(
                onClick = {
                    viewModel.saveOnBoardingState(completed = true)
                    navController.popBackStack()
                    navController.navigate(DrawerScreens.Home.route)
                },
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(0.8f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.welcome_screen_button),
                    fontSize = 16.sp,
                    fontFamily = greenstashFont,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (currencyDialog.value) {
            AlertDialog(onDismissRequest = {
                currencyDialog.value = false
            }, title = {
                Text(
                    text = stringResource(id = R.string.currency_dialog_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = greenstashFont,
                )
            }, text = {
                Column(
                    modifier = Modifier
                        .selectableGroup()
                        .verticalScroll(
                            rememberScrollState()
                        ),
                    verticalArrangement = Arrangement.Center,
                ) {
                    currencyEntries.forEach { text ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .selectable(
                                    selected = (text == selectedCurrencyOption),
                                    onClick = { onCurrencyOptionSelected(text) },
                                    role = Role.RadioButton,
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = (text == selectedCurrencyOption),
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = MaterialTheme.colorScheme.inversePrimary,
                                    disabledSelectedColor = Color.Black,
                                    disabledUnselectedColor = Color.Black
                                ),
                            )
                            Text(
                                text = text,
                                modifier = Modifier.padding(start = 16.dp),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = greenstashFont,
                            )
                        }
                    }
                }
            }, confirmButton = {
                TextButton(onClick = {
                    currencyDialog.value = false
                    val choice = currencyValues[currencyEntries.indexOf(selectedCurrencyOption)]
                    viewModel.setDefaultCurrency(choice)
                }) {
                    Text(stringResource(id = R.string.confirm), fontFamily = greenstashFont)
                }
            }, dismissButton = {
                TextButton(onClick = {
                    currencyDialog.value = false
                }) {
                    Text(stringResource(id = R.string.cancel), fontFamily = greenstashFont)
                }
            })
        }
    }
}