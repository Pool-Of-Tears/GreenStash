package com.starry.greenstash.ui.navigation

const val EDIT_GOAL_ARG_KEY = "editGoal"

sealed class Screens(val route: String) {
    object InputScreen : Screens("input_screen?$EDIT_GOAL_ARG_KEY={$EDIT_GOAL_ARG_KEY}") {
        fun withGoalToEdit(goalId: String): String {
            return route.replace("{$EDIT_GOAL_ARG_KEY}", goalId)
        }
    }
}