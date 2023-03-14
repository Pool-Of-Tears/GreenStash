package com.starry.greenstash.ui.screens.welcome.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.starry.greenstash.R
import com.starry.greenstash.ui.navigation.DrawerScreens
import com.starry.greenstash.ui.screens.welcome.viewmodels.WelcomeViewModel
import com.starry.greenstash.utils.PreferenceUtils

@Composable
fun WelcomeScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: WelcomeViewModel = hiltViewModel()

    val currencyEntries = context.resources.getStringArray(R.array.currency_entries)
    val currencyValues = context.resources.getStringArray(R.array.currency_values)

    val currencyValue = currencyEntries[currencyValues.indexOf(
        PreferenceUtils.getString(
            PreferenceUtils.DEFAULT_CURRENCY, currencyValues.first()
        )
    )]

    val currencyDialog = remember { mutableStateOf(false) }
    val (selectedCurrencyOption, onCurrencyOptionSelected) = remember {
        mutableStateOf(currencyValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
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
            modifier = Modifier.padding(start = 40.dp, end = 35.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { currencyDialog.value = true },
                modifier = Modifier
                    .padding(top = 80.dp, bottom = 16.dp)
                    .height(50.dp)
                    .fillMaxWidth(0.8f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = currencyValue,
                    fontSize = 16.sp,
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
                            )
                        }
                    }
                }
            }, confirmButton = {
                TextButton(onClick = {
                    currencyDialog.value = false
                    val choice =
                        currencyValues[currencyEntries.indexOf(selectedCurrencyOption)]
                    PreferenceUtils.putString(PreferenceUtils.DEFAULT_CURRENCY, choice)
                }) {
                    Text(stringResource(id = R.string.dialog_confirm_button))
                }
            }, dismissButton = {
                TextButton(onClick = {
                    currencyDialog.value = false
                }) {
                    Text(stringResource(id = R.string.cancel))
                }
            })
        }
    }
}