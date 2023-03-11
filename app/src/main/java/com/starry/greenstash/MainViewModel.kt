package com.starry.greenstash

import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    /**
     * Storing app lock status to avoid asking for authentication
     * when activity restarts like when changing app or device
     * theme or when changing device orientation.
    */
    var appUnlocked = false
}