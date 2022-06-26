/*
MIT License

Copyright (c) 2022 Stɑrry Shivɑm // This file is part of GreenStash.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.starry.greenstash

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rejowan.cutetoast.CuteToast
import com.starry.greenstash.databinding.ActivityMainBinding
import com.starry.greenstash.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var settingPerf: SharedPreferences
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var sharedViewModel: AndroidViewModel
    private lateinit var navOptions: NavOptions
    private val FIRST_START = "first_start"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Change from splash theme to app theme.
        setTheme(R.style.Theme_GreenStash)

        // build navigation options.
        val navOptionsBuilder = NavOptions.Builder()
        navOptionsBuilder.setEnterAnim(R.anim.slide_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)
        navOptions = navOptionsBuilder.build()

        // attach shared view model.
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        // Setup app theme.
        settingPerf = PreferenceManager.getDefaultSharedPreferences(this)
        settingPerf.getString("display", "system")?.let { setAppTheme(it) }

        // inflate main activity layout.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Check if app lock is set.
        val lockStatus = settingPerf.getBoolean("app_lock", false)
        if (lockStatus && !(sharedViewModel as SharedViewModel).appUnlocked) {
            // hide app contents until auth is successful.
            binding.root.invisible()
            executor = ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        CuteToast.ct(
                            this@MainActivity,
                            "Authentication Successful!",
                            CuteToast.LENGTH_SHORT,
                            CuteToast.HAPPY, true
                        ).show()
                        // make app contents visible after successful authentication.
                        binding.root.visible()
                        (sharedViewModel as SharedViewModel).appUnlocked = true
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        /*
                        on auth error make app contents visible and disable
                        app lock, auth error can happen when fingerprint or
                        password becomes unavailable or removed, contents will
                        stay hidden in that case making user unable to access
                        their data.
                        */
                        binding.root.visible()
                        (sharedViewModel as SharedViewModel).appUnlocked = true
                        val perfEditor = settingPerf.edit()
                        perfEditor.putBoolean("app_lock", false)
                        perfEditor.apply()
                    }
                })

            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.bio_lock_title))
                .setSubtitle(getString(R.string.bio_lock_subtitle))
                .setAllowedAuthenticators(AppConstants.AUTHENTICATORS)
                .build()

            biometricPrompt.authenticate(promptInfo)

        }

        // setup navigation controller.
        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // ask user to setup preferred currency when opening app for first time
        if (settingPerf.getBoolean(FIRST_START, true)) {
            showCurrencyDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (navController.graph.findNode(item.itemId) != null) {
            navController.navigate(item.itemId, null, navOptions)
        }
        return super.onOptionsItemSelected(item)

        // Handle action bar item clicks here.
        // return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showCurrencyDialog() {
        val currEntries = resources.getStringArray(R.array.currency_entries)
        val currValues = resources.getStringArray(R.array.currency_values)
        val builder = MaterialAlertDialogBuilder(this)
        val perfEditor = settingPerf.edit()
        // currency symbol.
        val defaultChoiceIndex = 48
        var choice = currValues[defaultChoiceIndex]
        // build currency chooser dialog.
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.setup_currency))
        builder.setSingleChoiceItems(currEntries, defaultChoiceIndex) { _, which ->
            choice = currValues[which]
        }
        builder.setPositiveButton("OK") { _, _ ->
            perfEditor.putString("currency", choice)
            perfEditor.putBoolean(FIRST_START, false)
            perfEditor.apply()
        }
        builder.setNegativeButton("Later") { _, _ ->
            perfEditor.putBoolean(FIRST_START, false)
            perfEditor.apply()
        }
        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }
}