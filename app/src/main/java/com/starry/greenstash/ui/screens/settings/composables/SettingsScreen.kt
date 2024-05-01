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


package com.starry.greenstash.ui.screens.settings.composables

import android.os.Build
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.starry.greenstash.MainActivity
import com.starry.greenstash.R
import com.starry.greenstash.ui.common.CurrencyPicker
import com.starry.greenstash.ui.common.CurrencyPickerData
import com.starry.greenstash.ui.navigation.Screens
import com.starry.greenstash.ui.screens.home.GoalCardStyle
import com.starry.greenstash.ui.screens.settings.DateStyle
import com.starry.greenstash.ui.screens.settings.SettingsViewModel
import com.starry.greenstash.ui.screens.settings.ThemeMode
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.utils.Utils
import com.starry.greenstash.utils.getActivity
import com.starry.greenstash.utils.toToast
import com.starry.greenstash.utils.weakHapticFeedback
import java.util.concurrent.Executor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val view = LocalView.current
    val context = LocalContext.current
    val viewModel = (context.getActivity() as MainActivity).settingsViewModel
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(title = {
                Text(
                    stringResource(id = R.string.settings_screen_header),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = greenstashFont
                )
            }, navigationIcon = {
                IconButton(onClick = {
                    view.weakHapticFeedback()
                    navController.navigateUp()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }, scrollBehavior = scrollBehavior, colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                scrolledContainerColor = MaterialTheme.colorScheme.surface,
            )
            )
        },
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            /** Display Settings */
            item { DisplaySettings(viewModel = viewModel, navController = navController) }

            /** Locales Setting */
            item { LocaleSettings(viewModel = viewModel) }

            /** Security Settings. */
            item { SecuritySettings(viewModel = viewModel) }

            /** About Setting */
            item { MiscSettings(navController = navController) }
        }
    }
}

@Composable
private fun DisplaySettings(viewModel: SettingsViewModel, navController: NavController) {
    val context = LocalContext.current

    // Theme related values.
    val themeValue = when (viewModel.getThemeValue()) {
        ThemeMode.Light.ordinal -> stringResource(id = R.string.theme_dialog_option1)
        ThemeMode.Dark.ordinal -> stringResource(id = R.string.theme_dialog_option2)
        else -> stringResource(id = R.string.theme_dialog_option3)
    }
    val themeDialog = remember { mutableStateOf(false) }
    val themeRadioOptions = listOf(
        stringResource(id = R.string.theme_dialog_option1),
        stringResource(id = R.string.theme_dialog_option2),
        stringResource(id = R.string.theme_dialog_option3)
    )
    val (selectedThemeOption, onThemeOptionSelected) = remember {
        mutableStateOf(themeValue)
    }

    // Material You related values.
    val materialYouSwitch = remember {
        mutableStateOf(viewModel.getMaterialYouValue())
    }

    val goalStyleValue = when (viewModel.getGoalCardStyleValue()) {
        GoalCardStyle.Classic.ordinal -> stringResource(id = R.string.goal_card_option1)
        GoalCardStyle.Compact.ordinal -> stringResource(id = R.string.goal_card_option2)
        else -> stringResource(id = R.string.goal_card_option1)
    }

    Spacer(modifier = Modifier.height(8.dp))

    SettingsContainer {
        SettingsCategory(title = stringResource(id = R.string.display_settings_title))
        SettingsItem(title = stringResource(id = R.string.theme_setting),
            description = themeValue,
            icon = ImageVector.vectorResource(id = R.drawable.ic_settings_theme),
            onClick = { themeDialog.value = true })

        SettingsItem(
            title = stringResource(id = R.string.material_you_setting),
            description = stringResource(
                id = R.string.material_you_setting_desc
            ),
            icon = ImageVector.vectorResource(id = R.drawable.ic_settings_material_you),
            switchState = materialYouSwitch,
            onCheckChange = { newValue ->
                materialYouSwitch.value = newValue

                if (newValue) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        viewModel.setMaterialYou(true)
                    } else {
                        materialYouSwitch.value = false
                        context.getString(R.string.material_you_error)
                            .toToast(context)
                    }
                } else {
                    viewModel.setMaterialYou(false)
                }
            }
        )

        SettingsItem(
            title = stringResource(id = R.string.goal_card_setting),
            description = goalStyleValue,
            icon = Icons.Filled.Style,
            onClick = { navController.navigate(Screens.GoalCardStyle.route) }
        )

        if (themeDialog.value) {
            AlertDialog(onDismissRequest = {
                themeDialog.value = false
            }, title = {
                Text(
                    text = stringResource(id = R.string.theme_dialog_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = greenstashFont,
                )
            }, text = {
                Column(
                    modifier = Modifier.selectableGroup(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    themeRadioOptions.forEach { text ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .selectable(
                                    selected = (text == selectedThemeOption),
                                    onClick = { onThemeOptionSelected(text) },
                                    role = Role.RadioButton,
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = (text == selectedThemeOption),
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
                FilledTonalButton(
                    onClick = {
                        themeDialog.value = false
                        when (selectedThemeOption) {
                            context.getString(R.string.theme_dialog_option1) -> {
                                viewModel.setTheme(ThemeMode.Light)
                            }

                            context.getString(R.string.theme_dialog_option2) -> {
                                viewModel.setTheme(ThemeMode.Dark)
                            }

                            context.getString(R.string.theme_dialog_option3) -> {
                                viewModel.setTheme(ThemeMode.Auto)
                            }
                        }
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        stringResource(id = R.string.theme_dialog_apply_button),
                        fontFamily = greenstashFont
                    )
                }
            }, dismissButton = {
                TextButton(onClick = {
                    themeDialog.value = false
                }) {
                    Text(
                        stringResource(id = R.string.cancel),
                        fontFamily = greenstashFont
                    )
                }
            })
        }
    }
}

@Composable
private fun LocaleSettings(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    // Date related values.
    val dateValue = if (viewModel.getDateStyleValue() == DateStyle.YearMonthDate.pattern
    ) {
        "YYYY/MM/DD"
    } else {
        "DD/MM/YYYY"
    }

    val dateDialog = remember { mutableStateOf(false) }
    val dateRadioOptions = listOf("DD/MM/YYYY", "YYYY/MM/DD")
    val (selectedDateOption, onDateOptionSelected) = remember {
        mutableStateOf(dateValue)
    }

    // Currency related values.
    val currencyDialog = remember { mutableStateOf(false) }
    val currencyNames = context.resources.getStringArray(R.array.currency_names)
    val currencyValues = context.resources.getStringArray(R.array.currency_values)

    val selectedCurrencyName = remember {
        mutableStateOf(currencyNames[currencyValues.indexOf(viewModel.getDefaultCurrencyValue())])
    }


    SettingsContainer {
        SettingsCategory(title = stringResource(id = R.string.locales_setting_title))
        SettingsItem(title = stringResource(id = R.string.date_format_setting),
            description = dateValue,
            icon = ImageVector.vectorResource(id = R.drawable.ic_settings_calender),
            onClick = { dateDialog.value = true })

        SettingsItem(title = stringResource(id = R.string.preferred_currency_setting),
            description = selectedCurrencyName.value,
            icon = ImageVector.vectorResource(id = R.drawable.ic_settings_currency),
            onClick = { currencyDialog.value = true })

        if (dateDialog.value) {
            AlertDialog(onDismissRequest = {
                dateDialog.value = false
            }, title = {
                Text(
                    text = stringResource(id = R.string.date_format_dialog_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = greenstashFont
                )
            }, text = {
                Column(
                    modifier = Modifier.selectableGroup(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    dateRadioOptions.forEach { text ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .selectable(
                                    selected = (text == selectedDateOption),
                                    onClick = { onDateOptionSelected(text) },
                                    role = Role.RadioButton,
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = (text == selectedDateOption),
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
                                fontFamily = greenstashFont
                            )
                        }
                    }
                }
            }, confirmButton = {
                FilledTonalButton(
                    onClick = {
                        dateDialog.value = false
                        when (selectedDateOption) {
                            "DD/MM/YYYY" -> {
                                viewModel.setDateStyle(DateStyle.DateMonthYear.pattern)
                            }

                            "YYYY/MM/DD" -> {
                                viewModel.setDateStyle(DateStyle.YearMonthDate.pattern)
                            }
                        }
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        stringResource(id = R.string.confirm),
                        fontFamily = greenstashFont
                    )
                }
            }, dismissButton = {
                TextButton(onClick = {
                    dateDialog.value = false
                }) {
                    Text(
                        stringResource(id = R.string.cancel),
                        fontFamily = greenstashFont
                    )
                }
            })
        }

        CurrencyPicker(
            defaultCurrencyValue = viewModel.getDefaultCurrencyValue()
                ?: currencyValues.first(),
            currencyPickerData = CurrencyPickerData(
                currencyNames = currencyNames,
                currencyValues = currencyValues,
            ),
            showBottomSheet = currencyDialog,
            onCurrencySelected = { newValue ->
                viewModel.setDefaultCurrency(newValue)
                selectedCurrencyName.value =
                    currencyNames[currencyValues.indexOf(newValue)]
            }
        )
    }
}

@Composable
private fun SecuritySettings(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val appLockSwitch = remember { mutableStateOf(viewModel.getAppLockValue()) }

    lateinit var executor: Executor
    lateinit var biometricPrompt: BiometricPrompt
    lateinit var promptInfo: BiometricPrompt.PromptInfo

    SettingsContainer {
        SettingsCategory(title = stringResource(id = R.string.security_settings_title))
        SettingsItem(
            title = stringResource(id = R.string.app_lock_setting),
            description = stringResource(id = R.string.app_lock_setting_desc),
            icon = ImageVector.vectorResource(id = R.drawable.ic_settings_app_lock),
            switchState = appLockSwitch,
            onCheckChange = { newValue ->
                appLockSwitch.value = newValue
                if (newValue) {
                    val mainActivity = context.getActivity() as MainActivity
                    executor = ContextCompat.getMainExecutor(context)
                    biometricPrompt = BiometricPrompt(mainActivity, executor,
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationError(
                                errorCode: Int, errString: CharSequence
                            ) {
                                super.onAuthenticationError(errorCode, errString)
                                context.getString(R.string.auth_error).format(errString)
                                    .toToast(context)
                                // disable preference switch manually on auth error.
                                appLockSwitch.value = false
                            }

                            override fun onAuthenticationSucceeded(
                                result: BiometricPrompt.AuthenticationResult
                            ) {
                                super.onAuthenticationSucceeded(result)
                                context.getString(R.string.auth_successful)
                                    .toToast(context)
                                mainActivity.mainViewModel.setAppUnlocked(true)
                                viewModel.setAppLock(true)
                            }

                            override fun onAuthenticationFailed() {
                                super.onAuthenticationFailed()
                                context.getString(R.string.auth_failed).toToast(context)
                                // disable preference switch manually on auth error.
                                appLockSwitch.value = false
                            }
                        })

                    promptInfo = BiometricPrompt.PromptInfo.Builder()
                        .setTitle(context.getString(R.string.bio_lock_title))
                        .setSubtitle(context.getString(R.string.bio_lock_subtitle))
                        .setAllowedAuthenticators(Utils.getAuthenticators()).build()

                    biometricPrompt.authenticate(promptInfo)
                } else {
                    viewModel.setAppLock(false)
                }
            }
        )
    }
}

@Composable
private fun MiscSettings(navController: NavController) {
    SettingsContainer {
        SettingsCategory(title = stringResource(id = R.string.misc_setting_title))
        SettingsItem(
            title = stringResource(id = R.string.license_setting),
            description = stringResource(id = R.string.license_setting_desc),
            icon = ImageVector.vectorResource(id = R.drawable.ic_settings_osl),
            onClick = { navController.navigate(Screens.OSLScreen.route) }
        )
        SettingsItem(
            title = stringResource(id = R.string.app_info_setting),
            description = stringResource(id = R.string.app_info_setting_desc),
            icon = ImageVector.vectorResource(id = R.drawable.ic_settings_about),
            onClick = { navController.navigate(Screens.AboutScreen.route) }
        )
    }
    Spacer(modifier = Modifier.height(2.dp)) // Last item padding.
}

@Composable
private fun SettingsContainer(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                3.dp
            )
        ),
    ) {
        Column(modifier = Modifier.padding(top = 2.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingsCategory(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 14.sp,
        fontFamily = greenstashFont,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .padding(horizontal = 14.dp)
    )
}