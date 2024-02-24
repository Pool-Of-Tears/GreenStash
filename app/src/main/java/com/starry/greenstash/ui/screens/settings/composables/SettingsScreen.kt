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
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
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
import com.starry.greenstash.ui.navigation.Screens
import com.starry.greenstash.ui.screens.settings.viewmodels.DateStyle
import com.starry.greenstash.ui.screens.settings.viewmodels.ThemeMode
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.utils.Utils
import com.starry.greenstash.utils.getActivity
import com.starry.greenstash.utils.toToast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.concurrent.Executor


@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel = (context.getActivity() as MainActivity).settingsViewModel
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    lateinit var executor: Executor
    lateinit var biometricPrompt: BiometricPrompt
    lateinit var promptInfo: BiometricPrompt.PromptInfo

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
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }, scrollBehavior = scrollBehavior, colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    4.dp
                )
            )
            )
        },
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            /** Display Settings */
            item {
                val themeValue = when (viewModel.getThemeValue()) {
                    ThemeMode.Light.ordinal -> "Light"
                    ThemeMode.Dark.ordinal -> "Dark"
                    else -> "System"
                }
                val themeDialog = remember { mutableStateOf(false) }
                val themeRadioOptions = listOf("Light", "Dark", "System")
                val (selectedThemeOption, onThemeOptionSelected) = remember {
                    mutableStateOf(themeValue)
                }

                val materialYouSwitch = remember {
                    mutableStateOf(viewModel.getMaterialYouValue())
                }

                Column(
                    modifier = Modifier.padding(top = 10.dp)
                ) {
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
                                    context.getString(R.string.material_you_error).toToast(context)
                                }
                            } else {
                                viewModel.setMaterialYou(false)
                            }
                        }
                    )

                    if (themeDialog.value) {
                        AlertDialog(onDismissRequest = {
                            themeDialog.value = false
                        }, title = {
                            Text(
                                text = stringResource(id = R.string.theme_dialog_title),
                                color = MaterialTheme.colorScheme.onSurface,
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
                                        )
                                    }
                                }
                            }
                        }, confirmButton = {
                            TextButton(onClick = {
                                themeDialog.value = false

                                when (selectedThemeOption) {
                                    "Light" -> {
                                        viewModel.setTheme(ThemeMode.Light)
                                    }

                                    "Dark" -> {
                                        viewModel.setTheme(ThemeMode.Dark)
                                    }

                                    "System" -> {
                                        viewModel.setTheme(ThemeMode.Auto)
                                    }
                                }
                            }) {
                                Text(stringResource(id = R.string.theme_dialog_apply_button))
                            }
                        }, dismissButton = {
                            TextButton(onClick = {
                                themeDialog.value = false
                            }) {
                                Text(stringResource(id = R.string.cancel))
                            }
                        })
                    }
                }
            }

            /** Locales Setting */
            item {

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

                val currencyEntries = context.resources.getStringArray(R.array.currency_entries)
                val currencyValues = context.resources.getStringArray(R.array.currency_values)

                val currencyValue = currencyEntries[
                    currencyValues.indexOf(viewModel.getDefaultCurrencyValue())
                ]

                val currencyDialog = remember { mutableStateOf(false) }
                val (selectedCurrencyOption, onCurrencyOptionSelected) = remember {
                    mutableStateOf(currencyValue)
                }


                Column(
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    SettingsCategory(title = stringResource(id = R.string.locales_setting_title))
                    SettingsItem(title = stringResource(id = R.string.date_format_setting),
                        description = dateValue,
                        icon = ImageVector.vectorResource(id = R.drawable.ic_settings_calender),
                        onClick = { dateDialog.value = true })

                    SettingsItem(title = stringResource(id = R.string.preferred_currency_setting),
                        description = currencyValue,
                        icon = ImageVector.vectorResource(id = R.drawable.ic_settings_currency),
                        onClick = { currencyDialog.value = true })

                    if (dateDialog.value) {
                        AlertDialog(onDismissRequest = {
                            dateDialog.value = false
                        }, title = {
                            Text(
                                text = stringResource(id = R.string.date_format_dialog_title),
                                color = MaterialTheme.colorScheme.onSurface,
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
                                        )
                                    }
                                }
                            }
                        }, confirmButton = {
                            TextButton(onClick = {
                                dateDialog.value = false

                                when (selectedDateOption) {
                                    "DD/MM/YYYY" -> {
                                        viewModel.setDateStyle(DateStyle.DateMonthYear.pattern)
                                    }

                                    "YYYY/MM/DD" -> {
                                        viewModel.setDateStyle(DateStyle.YearMonthDate.pattern)
                                    }
                                }
                            }) {
                                Text(stringResource(id = R.string.dialog_confirm_button))
                            }
                        }, dismissButton = {
                            TextButton(onClick = {
                                dateDialog.value = false
                            }) {
                                Text(stringResource(id = R.string.cancel))
                            }
                        })
                    }

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
                                viewModel.setDefaultCurrency(choice)
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

            /** Security Settings. */
            item {
                val appLockSwitch = remember { mutableStateOf(viewModel.getAppLockValue()) }

                Column(
                    modifier = Modifier.padding(top = 10.dp)
                ) {
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
                                            mainActivity.mainViewModel.appUnlocked = true
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

            /** About Setting */
            item {
                Column(
                    modifier = Modifier.padding(top = 10.dp)
                ) {
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
            }
        }
    }
}

@Composable
fun SettingsCategory(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .padding(horizontal = 14.dp)
    )
}