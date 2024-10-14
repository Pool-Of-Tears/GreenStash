package com.starry.greenstash.ui.navigation

import kotlinx.serialization.Serializable


@Serializable
data class DWScreen(val goalId: String, val transactionType: String)


@Serializable
data class InputScreen(val goalId: String? = null)


@Serializable
data class GoalInfoScreen(val goalId: String)

@Serializable
object AboutScreen

@Serializable
object OSLScreen

@Serializable
object GoalCardStyleScreen


// Goal Achieved Screen
@Serializable
object CongratsScreen

// Welcome / Onboarding Screen
@Serializable
object WelcomeScreen



