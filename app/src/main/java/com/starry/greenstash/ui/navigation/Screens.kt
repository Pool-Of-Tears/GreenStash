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

const val DW_GOAL_ID_ARG_KEY = "dwGoal"
const val DW_TRANSACTION_TYPE_ARG_KEY = "dwTransactionType"
const val EDIT_GOAL_ARG_KEY = "editGoal"
const val GOAL_INFO_ARG_KEY = "goalId"

sealed class Screens(val route: String) {

    // Deposit / Withdraw Screens

    data object DWScreen :
        Screens("deposit_withdraw_screen/{$DW_GOAL_ID_ARG_KEY}/{$DW_TRANSACTION_TYPE_ARG_KEY}") {
        fun withGoalId(goalId: String, trasactionType: String): String {
            return route.replace("{$DW_GOAL_ID_ARG_KEY}", goalId)
                .replace("{$DW_TRANSACTION_TYPE_ARG_KEY}", trasactionType)
        }
    }

    // New Goal / Edit Goal Screen
    data object InputScreen : Screens("input_screen?$EDIT_GOAL_ARG_KEY={$EDIT_GOAL_ARG_KEY}") {
        fun withGoalToEdit(goalId: String): String {
            return route.replace("{$EDIT_GOAL_ARG_KEY}", goalId)
        }
    }

    // Goal Info Screen
    data object GoalInfoScreen : Screens("goal_info_screen/{$GOAL_INFO_ARG_KEY}") {
        fun withGoalId(goalId: String): String {
            return route.replace("{$GOAL_INFO_ARG_KEY}", goalId)
        }
    }

    // Settings Screens
    data object GoalCardStyle : Screens("goal_card_style")
    data object AboutScreen : Screens("about_screen")
    data object OSLScreen : Screens("osl_screen")

    // Goal Achieved Screen
    data object CongratsScreen : Screens("goal_achieved_screen")

    // Welcome / Onboarding Screen
    data object WelcomeScreen : Screens("welcome_screen")
}