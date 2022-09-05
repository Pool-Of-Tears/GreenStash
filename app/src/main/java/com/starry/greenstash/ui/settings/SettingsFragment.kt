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

package com.starry.greenstash.ui.settings

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.color.DynamicColors
import com.starry.greenstash.R
import com.starry.greenstash.utils.AppConstants
import com.starry.greenstash.utils.SharedViewModel
import com.starry.greenstash.utils.setAppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var sharedViewModel: SharedViewModel

    @Inject
    lateinit var navOptions: NavOptions

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setHasOptionsMenu(true)

        // attach shared view model.
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        val displayPerf: Preference? = findPreference("display")
        displayPerf!!.setOnPreferenceChangeListener { _, newValue ->
            setAppTheme(newValue.toString())
            true // return status.
        }

        val materialYouPerf: Preference? = findPreference("material_you")
        // disable material you switch on android version < 12
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            materialYouPerf!!.isEnabled = false
            (materialYouPerf as SwitchPreferenceCompat).isChecked = false
        }
        materialYouPerf!!.setOnPreferenceChangeListener { _, newValue ->
            if (newValue.toString().toBoolean() && !DynamicColors.isDynamicColorAvailable()) {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.material_you_unavailable),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.relaunch_app),
                    Toast.LENGTH_SHORT
                ).show()
            }
            true // return status
        }

        val appLockPerf: Preference? = findPreference("app_lock")
        appLockPerf!!.setOnPreferenceChangeListener { preference, newValue ->
            val isChecked: Boolean = newValue.toString().toBoolean()
            // if app locked is enabled, show user biometric authentication
            // prompt to verify if user actually owns the device.
            if (isChecked) {
                executor = ContextCompat.getMainExecutor(requireContext())
                biometricPrompt = BiometricPrompt(this, executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(
                            errorCode: Int,
                            errString: CharSequence
                        ) {
                            super.onAuthenticationError(errorCode, errString)
                            Toast.makeText(
                                requireContext(),
                                "Authentication error: $errString",
                                Toast.LENGTH_SHORT
                            ).show()
                            // disable preference switch manually on auth error.
                            (preference as SwitchPreferenceCompat).isChecked = false
                        }

                        override fun onAuthenticationSucceeded(
                            result: BiometricPrompt.AuthenticationResult
                        ) {
                            super.onAuthenticationSucceeded(result)
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.auth_successful),
                                Toast.LENGTH_SHORT
                            ).show()
                            sharedViewModel.appUnlocked = true
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.auth_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                            // disable preference switch manually on auth fail.
                            (preference as SwitchPreferenceCompat).isChecked = false
                        }
                    })

                promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(requireContext().getString(R.string.bio_lock_title))
                    .setSubtitle(requireContext().getString(R.string.bio_lock_subtitle))
                    .setAllowedAuthenticators(AppConstants.AUTHENTICATORS)
                    .build()

                biometricPrompt.authenticate(promptInfo)

            }

            true // return status.
        }

        val licensePerf: Preference? = findPreference("license")
        licensePerf!!.setOnPreferenceClickListener {
            findNavController().navigate(R.id.OSLFragment, null, navOptions)
            true // return status.
        }

        val aboutPerf: Preference? = findPreference("about")
        aboutPerf!!.setOnPreferenceClickListener {
            findNavController().navigate(R.id.AboutFragment, null, navOptions)
            true // return status.
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()

    }
}