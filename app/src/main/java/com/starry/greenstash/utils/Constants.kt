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

package com.starry.greenstash.utils

import android.os.Build
import androidx.biometric.BiometricManager.Authenticators.*

object AppConstants {
    const val DATE_FORMAT = "dd/MM/yyyy"
    /*
    https://developer.android.com/reference/androidx/biometric/BiometricPrompt.PromptInfo.Builder#setAllowedAuthenticators(int)
     */
    val AUTHENTICATORS = if (Build.VERSION.SDK_INT == 28 || Build.VERSION.SDK_INT == 29) {
        BIOMETRIC_WEAK or DEVICE_CREDENTIAL
    } else {
        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
    }
}