package com.starry.greenstash.ui.screens.settings.composables

import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import androidx.navigation.NavController
import com.starry.greenstash.MainActivity
import com.starry.greenstash.R
import com.starry.greenstash.ui.screens.settings.common.SettingsItem
import com.starry.greenstash.ui.screens.settings.viewmodels.ThemeMode
import com.starry.greenstash.ui.theme.poppinsFont
import com.starry.greenstash.utils.PreferenceUtils
import com.starry.greenstash.utils.getActivity
import com.starry.greenstash.utils.toToast


@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun SettingsScreen(navController: NavController) {
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
                    overflow = TextOverflow.Ellipsis
                )
            }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack, contentDescription = null
                    )
                }
            }, scrollBehavior = scrollBehavior
            )
        },
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            /** Display Settings */
            item {
                val displayValue = when (PreferenceUtils.getInt(
                    PreferenceUtils.APP_THEME, ThemeMode.Auto.ordinal
                )) {
                    ThemeMode.Light.ordinal -> "Light"
                    ThemeMode.Dark.ordinal -> "Dark"
                    else -> "System"
                }
                val displayDialog = remember { mutableStateOf(false) }
                val radioOptions = listOf("Light", "Dark", "System")
                val (selectedOption, onOptionSelected) = remember { mutableStateOf(displayValue) }

                val materialYouSwitch = remember {
                    mutableStateOf(
                        PreferenceUtils.getBoolean(
                            PreferenceUtils.MATERIAL_YOU,
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                        )
                    )
                }

                Column(
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.display_settings_title),
                        fontFamily = poppinsFont,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .padding(horizontal = 14.dp)
                    )
                    SettingsItem(title = stringResource(id = R.string.theme_setting),
                        description = displayValue,
                        icon = ImageVector.vectorResource(id = R.drawable.ic_settings_theme),
                        onClick = { displayDialog.value = true })

                    SettingsItem(
                        title = stringResource(id = R.string.material_you_setting),
                        description = stringResource(
                            id = R.string.material_you_setting_desc
                        ),
                        icon = ImageVector.vectorResource(id = R.drawable.ic_settings_material_you),
                        switchState = materialYouSwitch
                    )

                    if (materialYouSwitch.value) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            viewModel.setMaterialYou(true)
                            PreferenceUtils.putBoolean(PreferenceUtils.MATERIAL_YOU, true)
                        } else {
                            materialYouSwitch.value = false
                            stringResource(id = R.string.material_you_error).toToast(context)
                        }
                    } else {
                        viewModel.setMaterialYou(false)
                        PreferenceUtils.putBoolean(PreferenceUtils.MATERIAL_YOU, false)
                    }


                    if (displayDialog.value) {
                        AlertDialog(onDismissRequest = {
                            displayDialog.value = false
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
                                radioOptions.forEach { text ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(46.dp)
                                            .selectable(
                                                selected = (text == selectedOption),
                                                onClick = { onOptionSelected(text) },
                                                role = Role.RadioButton,
                                            ),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        RadioButton(
                                            selected = (text == selectedOption),
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
                                            fontFamily = poppinsFont
                                        )
                                    }
                                }
                            }
                        }, confirmButton = {
                            TextButton(onClick = {
                                displayDialog.value = false

                                when (selectedOption) {
                                    "Light" -> {
                                        viewModel.setTheme(
                                            ThemeMode.Light
                                        )
                                        PreferenceUtils.putInt(
                                            PreferenceUtils.APP_THEME, ThemeMode.Light.ordinal
                                        )
                                    }
                                    "Dark" -> {
                                        viewModel.setTheme(
                                            ThemeMode.Dark
                                        )
                                        PreferenceUtils.putInt(
                                            PreferenceUtils.APP_THEME, ThemeMode.Dark.ordinal
                                        )
                                    }
                                    "System" -> {
                                        viewModel.setTheme(
                                            ThemeMode.Auto
                                        )
                                        PreferenceUtils.putInt(
                                            PreferenceUtils.APP_THEME, ThemeMode.Auto.ordinal
                                        )
                                    }
                                }
                            }) {
                                Text(stringResource(id = R.string.theme_dialog_apply_button))
                            }
                        }, dismissButton = {
                            TextButton(onClick = {
                                displayDialog.value = false
                            }) {
                                Text(stringResource(id = R.string.cancel))
                            }
                        })
                    }
                }
            }
        }
    }
}