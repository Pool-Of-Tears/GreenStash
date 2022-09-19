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

package com.starry.greenstash.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.color.DynamicColors
import com.starry.greenstash.R
import com.starry.greenstash.database.ItemDatabase
import com.starry.greenstash.databinding.ActivityMainBinding
import com.starry.greenstash.ui.viewmodels.SharedViewModel
import com.starry.greenstash.utils.*
import dagger.hilt.android.AndroidEntryPoint
import de.raphaelebner.roomdatabasebackup.core.RoomBackup
import java.util.concurrent.Executor
import javax.inject.Inject

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
    private lateinit var roomBackup: RoomBackup

    @Inject // Inject nav options.
    lateinit var navOptions: NavOptions

    @Inject // Inject item database instance.
    lateinit var itemDatabase: ItemDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // attach shared view model.
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        // setup default setting values
        PreferenceManager.setDefaultValues(this, R.xml.settings, false)
        settingPerf = PreferenceManager.getDefaultSharedPreferences(this)
        // Setup app theme.
        settingPerf.getString("display", "system")?.let { setAppTheme(it) }
        // set dynamic colors.
        settingPerf.getBoolean("material_you", false)
            .let { if (it) DynamicColors.applyToActivityIfAvailable(this) }

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
                        getString(R.string.auth_successful).toToast(this@MainActivity)
                        // make app contents visible after successful authentication.
                        binding.root.visible()
                        (sharedViewModel as SharedViewModel).appUnlocked = true
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        /*
                        On auth error check if user can still authenticate or no, i.e. make sure
                        that user has not removed authentication from the device and fingerprint
                        hardware is available. if not then make app contents visible and disable
                        app lock to avoid user from becoming unable to access their data.
                        */
                        val biometricManager = BiometricManager.from(this@MainActivity)
                        if (biometricManager.canAuthenticate(AppConstants.AUTHENTICATORS) != BiometricManager.BIOMETRIC_SUCCESS) {
                            binding.root.visible()
                            (sharedViewModel as SharedViewModel).appUnlocked = true
                            val perfEditor = settingPerf.edit()
                            perfEditor.putBoolean("app_lock", false)
                            perfEditor.apply()
                        } else {
                            finish() // close the app.
                        }
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
        if (settingPerf.getBoolean(AppConstants.FIRST_RUN, true)) {
            showCurrencyDialog()
        }
        //  initialize & setup room backup instance
        roomBackup = RoomBackup(this)
            .database(itemDatabase)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (navController.graph.findNode(item.itemId) != null) {
            navController.navigate(item.itemId, null, navOptions)
        } else {
            when (item.itemId) {
                R.id.actionBackup -> showBackupDialog()
            }
        }
        return super.onOptionsItemSelected(item)

        // Handle action bar item clicks here.
        // return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showBackupDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.backup_menu)

        val backupView = bottomSheetDialog.findViewById<LinearLayout>(R.id.backupData)
        val restoreView = bottomSheetDialog.findViewById<LinearLayout>(R.id.restoreData)

        backupView!!.setOnClickListener {
            try {
                roomBackup.backup()
            } catch (exc: NullPointerException) {
                exc.printStackTrace()
            }
        }
        restoreView!!.setOnClickListener {
            try {
                roomBackup.restore()
            } catch (exc: NullPointerException) {
                exc.printStackTrace()
            }
        }
        bottomSheetDialog.show()
    }

    private fun showCurrencyDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.currency_chooser_menu)
        bottomSheetDialog.setCancelable(false)

        val currencySpinner = bottomSheetDialog.findViewById<Spinner>(R.id.currencyChooserSpinner)
        val currencySaveBtn = bottomSheetDialog.findViewById<Button>(R.id.btnSetupCurrency)

        val currEntries = resources.getStringArray(R.array.currency_entries)
        val currValues = resources.getStringArray(R.array.currency_values)
        val spinArrayAdp =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currEntries)
        currencySpinner!!.adapter = spinArrayAdp

        currencySaveBtn!!.setOnClickListener {
            val choice = currValues[currEntries.indexOf(currencySpinner.selectedItem.toString())]
            val perfEditor = settingPerf.edit()
            perfEditor.putString("currency", choice)
            perfEditor.putBoolean(AppConstants.FIRST_RUN, false)
            perfEditor.apply()

            Handler(Looper.getMainLooper()).postDelayed(
                {
                    bottomSheetDialog.hide()
                }, 250
            )
        }

        bottomSheetDialog.show()
    }
}