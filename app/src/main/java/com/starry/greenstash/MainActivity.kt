package com.starry.greenstash

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.starry.greenstash.ui.screens.main.MainScreen
import com.starry.greenstash.ui.screens.settings.viewmodels.SettingsViewModel
import com.starry.greenstash.ui.screens.settings.viewmodels.ThemeMode
import com.starry.greenstash.ui.theme.GreenStashTheme
import com.starry.greenstash.utils.PreferenceUtils

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
class MainActivity : AppCompatActivity() {

    lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PreferenceUtils.initialize(this)
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        when (PreferenceUtils.getInt(PreferenceUtils.APP_THEME, ThemeMode.Auto.ordinal)) {
            ThemeMode.Auto.ordinal -> settingsViewModel.setTheme(ThemeMode.Auto)
            ThemeMode.Dark.ordinal -> settingsViewModel.setTheme(ThemeMode.Dark)
            ThemeMode.Light.ordinal -> settingsViewModel.setTheme(ThemeMode.Light)
        }

        settingsViewModel.setMaterialYou(
            PreferenceUtils.getBoolean(
                PreferenceUtils.MATERIAL_YOU, Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            )
        )

        setContent {
            GreenStashTheme(settingsViewModel = settingsViewModel) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(settingsViewModel)
                }
            }
        }
    }
}