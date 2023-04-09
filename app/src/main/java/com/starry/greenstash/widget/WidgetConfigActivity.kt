package com.starry.greenstash.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.starry.greenstash.ui.screens.settings.viewmodels.SettingsViewModel
import com.starry.greenstash.ui.screens.settings.viewmodels.ThemeMode
import com.starry.greenstash.ui.theme.GreenStashTheme
import com.starry.greenstash.utils.PreferenceUtils

@ExperimentalAnimationApi
class WidgetConfigActivity : AppCompatActivity() {
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PreferenceUtils.initialize(this)
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        settingsViewModel.setUpAppTheme()

        setContent {
            GreenStashTheme(settingsViewModel = settingsViewModel) {
                val systemUiController = rememberSystemUiController()
                systemUiController.setNavigationBarColor(
                    color = MaterialTheme.colorScheme.background,
                    darkIcons = settingsViewModel.getCurrentTheme() == ThemeMode.Light
                )

                systemUiController.setStatusBarColor(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                    darkIcons = settingsViewModel.getCurrentTheme() == ThemeMode.Light
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "hello world!")
                    }
                }
            }

            var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
            intent.extras?.let {
                appWidgetId = it.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
            }

            if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                finish()
            } else {

                val resultIntent = Intent()
                resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                setResult(RESULT_OK)
                finish()
            }
        }
    }
}