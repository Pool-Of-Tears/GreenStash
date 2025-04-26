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

import kotlinx.serialization.Serializable

sealed class OtherScreens : BaseScreen() {

    @Serializable
    data class DWScreen(val goalId: String, val transactionType: String)

    @Serializable
    data class InputScreen(val goalId: String? = null)

    @Serializable
    data class GoalInfoScreen(val goalId: String)

    @Serializable
    data object AboutScreen

    @Serializable
    data object OSLScreen

    @Serializable
    data object GoalCardStyleScreen


    // Goal Achieved Screen
    @Serializable
    data object CongratsScreen

    // Welcome / Onboarding Screen
    @Serializable
    data object WelcomeScreen : OtherScreens()
}


