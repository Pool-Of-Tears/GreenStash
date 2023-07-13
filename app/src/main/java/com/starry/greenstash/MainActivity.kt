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

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.starry.greenstash.database.core.AppDatabase
import com.starry.greenstash.ui.navigation.NavGraph
import com.starry.greenstash.ui.screens.settings.viewmodels.SettingsViewModel
import com.starry.greenstash.ui.screens.settings.viewmodels.ThemeMode
import com.starry.greenstash.ui.theme.GreenStashTheme
import com.starry.greenstash.utils.PreferenceUtils
import com.starry.greenstash.utils.Utils
import com.starry.greenstash.utils.toToast
import dagger.hilt.android.AndroidEntryPoint
import de.raphaelebner.roomdatabasebackup.core.RoomBackup
import java.util.concurrent.Executor
import javax.inject.Inject

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@AndroidEntryPoint
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
class MainActivity : AppCompatActivity() {

    lateinit var settingsViewModel: SettingsViewModel
    lateinit var mainViewModel: MainViewModel

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private lateinit var roomBackup: RoomBackup

    @Inject
    lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PreferenceUtils.initialize(this)
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Setup app theme according to user's settings.
        settingsViewModel.setUpAppTheme()

        // show splash screen until we figure out start nav destination.
        installSplashScreen().setKeepOnScreenCondition {
            mainViewModel.isLoading.value
        }

        val appLockStatus = PreferenceUtils.getBoolean(PreferenceUtils.APP_LOCK, false)

        if (appLockStatus && !mainViewModel.appUnlocked) {
            executor = ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        getString(R.string.auth_successful).toToast(this@MainActivity)
                        // make app contents visible after successful authentication.
                        setAppContents()
                        mainViewModel.appUnlocked = true
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
                            setAppContents()
                            mainViewModel.appUnlocked = true
                            PreferenceUtils.putBoolean(PreferenceUtils.APP_LOCK, false)
                        } else {
                            finish() // close the app.
                        }
                    }
                })

            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.bio_lock_title))
                .setSubtitle(getString(R.string.bio_lock_subtitle))
                .setAllowedAuthenticators(Utils.getAuthenticators())
                .build()

            biometricPrompt.authenticate(promptInfo)

        } else {
            setAppContents()
        }

        //  initialize & setup room backup instance
        roomBackup = RoomBackup(this)
            .database(appDatabase)
            .enableLogDebug(true)
            .backupLocation(RoomBackup.BACKUP_FILE_LOCATION_CUSTOM_DIALOG)
            .customBackupFileName("GreenStash-${System.currentTimeMillis()}.backup")
            .apply {
                onCompleteListener { success, message, _ ->
                    if (success) restartApp(
                        Intent(
                            this@MainActivity,
                            MainActivity::class.java
                        )
                    ) else Toast.makeText(
                        this@MainActivity,
                        message, Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    fun setAppContents() {
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
                    val navController = rememberAnimatedNavController()
                    val screen by mainViewModel.startDestination
                    NavGraph(navController = navController, screen)
                }
            }
        }
    }

    fun backupDatabase() {
        try {
            roomBackup.backup()
        } catch (exc: NullPointerException) {
            exc.printStackTrace()
        }
    }

    fun restoreDatabase() {
        try {
            roomBackup.restore()
        } catch (exc: NullPointerException) {
            exc.printStackTrace()
        }
    }
}