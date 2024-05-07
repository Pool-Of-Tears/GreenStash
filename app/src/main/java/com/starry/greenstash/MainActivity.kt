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


package com.starry.greenstash

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.starry.greenstash.ui.navigation.NavGraph
import com.starry.greenstash.ui.screens.other.AppLockedScreen
import com.starry.greenstash.ui.screens.settings.SettingsViewModel
import com.starry.greenstash.ui.theme.AdjustEdgeToEdge
import com.starry.greenstash.ui.theme.GreenStashTheme
import com.starry.greenstash.utils.Utils
import com.starry.greenstash.utils.toToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var settingsViewModel: SettingsViewModel
    lateinit var mainViewModel: MainViewModel

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // show splash screen until we figure out start nav destination.
        installSplashScreen().setKeepOnScreenCondition {
            mainViewModel.isLoading.value
        }

        enableEdgeToEdge() // enable edge to edge for the activity.

        // refresh reminders
        mainViewModel.refreshReminders()

        val appLockStatus = settingsViewModel.getAppLockValue()
        val showAppContents = mutableStateOf(false)

        // check if app lock is enabled and user has not unlocked the app.
        if (appLockStatus && !mainViewModel.isAppUnlocked()) {
            executor = ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        // make app contents visible after successful authentication.
                        showAppContents.value = true
                        mainViewModel.setAppUnlocked(true)
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        /**
                         * On auth error check if user can still authenticate or no, i.e. make sure
                         * that user has not removed authentication from the device and fingerprint
                         * hardware is available. if not then make app contents visible and disable
                         *  app lock to avoid user from becoming unable to access their data.
                         */
                        val biometricManager = BiometricManager.from(this@MainActivity)
                        if (biometricManager.canAuthenticate(Utils.getAuthenticators()) != BiometricManager.BIOMETRIC_SUCCESS) {
                            // make app contents visible.
                            showAppContents.value = true
                            // disable app lock.
                            mainViewModel.setAppUnlocked(true)
                            // disable app lock in settings.
                            settingsViewModel.setAppLock(false)
                            // show error message.
                            getString(R.string.app_lock_unable_to_authenticate).toToast(this@MainActivity)
                        } else {
                            showAppContents.value = false
                        }
                    }
                })

            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.bio_lock_title))
                .setSubtitle(getString(R.string.bio_lock_subtitle))
                .setAllowedAuthenticators(Utils.getAuthenticators())
                .build()

        } else {
            showAppContents.value = true
        }

        // set app contents based on the value of showAppContents.
        setAppContents(showAppContents)
    }

    private fun setAppContents(showAppContents: State<Boolean>) {
        setContent {
            GreenStashTheme(settingsViewModel = settingsViewModel) {
                // fix status bar icon color in dark mode.
                AdjustEdgeToEdge(
                    activity = this,
                    themeState = settingsViewModel.getCurrentTheme()
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val startDestination by mainViewModel.startDestination

                    Crossfade(
                        targetState = showAppContents,
                        label = "AppLockCrossFade",
                        animationSpec = tween(500)
                    ) { showAppContents ->
                        // show app contents only if user has authenticated.
                        if (showAppContents.value) {
                            NavGraph(navController = navController, startDestination)
                        } else {
                            // show app locked screen if user has not authenticated.
                            AppLockedScreen(onAuthRequest = {
                                biometricPrompt.authenticate(promptInfo)
                            })
                        }
                    }
                }
            }
        }
    }
}