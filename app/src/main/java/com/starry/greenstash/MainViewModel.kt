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


package com.starry.greenstash

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.database.goal.GoalDao
import com.starry.greenstash.other.WelcomeDataStore
import com.starry.greenstash.reminder.ReminderManager
import com.starry.greenstash.ui.navigation.BaseScreen
import com.starry.greenstash.ui.navigation.DrawerScreens
import com.starry.greenstash.ui.navigation.OtherScreens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val welcomeDataStore: WelcomeDataStore,
    private val goalDao: GoalDao,
    private val reminderManager: ReminderManager
) : ViewModel() {
    /**
     * Store app lock status to avoid asking for authentication
     * when activity restarts like when changing app or device
     * theme or when changing device orientation.
     */
    private var _appUnlocked = false

    private val _isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _startDestination: MutableState<BaseScreen> =
        mutableStateOf(OtherScreens.WelcomeScreen)
    val startDestination: State<BaseScreen> = _startDestination

    companion object {
        // Must be same as the one in AndroidManifest.xml
        const val LAUNCHER_SHORTCUT_SCHEME = "greenstash_lc_shortcut"

        // Key to get goalId from intent.
        const val LC_SHORTCUT_GOAL_ID = "lc_shortcut_goal_id"

        // Key to detect new goal shortcut.
        const val LC_SHORTCUT_NEW_GOAL = "lc_shortcut_new_goal"
    }

    init {
        viewModelScope.launch {
            welcomeDataStore.readOnBoardingState().collect { completed ->
                if (completed) {
                    _startDestination.value = DrawerScreens.Home
                } else {
                    _startDestination.value = OtherScreens.WelcomeScreen
                }

                delay(120)
                _isLoading.value = false
            }
        }
    }

    fun isAppUnlocked(): Boolean = _appUnlocked

    fun setAppUnlocked(value: Boolean) {
        _appUnlocked = value
    }

    fun refreshReminders() {
        viewModelScope.launch(Dispatchers.IO) {
            reminderManager.checkAndScheduleReminders(goalDao.getAllGoals())
        }
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun buildDynamicShortcuts(
        context: Context,
        limit: Int,
        onComplete: (List<ShortcutInfo>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // get all goals and filter top goals up to limit by priority
            val goals = goalDao.getAllGoals()
            val topGoals = goals.sortedByDescending { goalItem ->
                goalItem.goal.priority.value
            }.take(limit - 1).map { it.goal } // -1 for new goal shortcut

            val newGoalShortcut = ShortcutInfo.Builder(context, "new_goal").apply {
                setShortLabel(context.getString(R.string.new_goal_fab))
                setIcon(Icon.createWithResource(context, R.drawable.ic_shortcut_new_goal))
                setIntent(Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse("$LAUNCHER_SHORTCUT_SCHEME://newGoal")
                    putExtra(LC_SHORTCUT_NEW_GOAL, true)
                })
            }.build()

            val shortcuts = listOf(newGoalShortcut) + topGoals.map { goal ->
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse("$LAUNCHER_SHORTCUT_SCHEME://goalId")
                    putExtra(LC_SHORTCUT_GOAL_ID, goal.goalId)
                }

                ShortcutInfo.Builder(context, goal.goalId.toString()).apply {
                    setShortLabel(goal.title)
                    setIcon(Icon.createWithResource(context, R.drawable.ic_widget_config_item))
                    setIntent(intent)
                }.build()
            }

            withContext(Dispatchers.Main) { onComplete(shortcuts) }
        }

    }
}
