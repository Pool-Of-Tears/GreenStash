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


package com.starry.greenstash.ui.navigation

import com.starry.greenstash.R
import kotlinx.serialization.Serializable

@Serializable
sealed class DrawerScreens(val nameResId: Int, val iconResId: Int) : BaseScreen() {

    companion object {
        fun getAllItems() = listOf(Home, Archive, Backups, Settings)
    }

    @Serializable
    data object Home : DrawerScreens(R.string.drawer_home, R.drawable.ic_nav_home)

    @Serializable
    data object Archive :
        DrawerScreens(R.string.drawer_archive, R.drawable.ic_nav_archive)

    @Serializable
    data object Backups :
        DrawerScreens(R.string.drawer_backup, R.drawable.ic_nav_backups)

    @Serializable
    data object Settings :
        DrawerScreens(R.string.drawer_settings, R.drawable.ic_nav_settings)
}


