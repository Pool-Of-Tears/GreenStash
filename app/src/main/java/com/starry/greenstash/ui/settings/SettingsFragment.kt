package com.starry.greenstash.ui.settings

import android.os.Bundle
import android.view.Menu
import androidx.preference.PreferenceFragmentCompat
import com.starry.greenstash.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()

    }
}