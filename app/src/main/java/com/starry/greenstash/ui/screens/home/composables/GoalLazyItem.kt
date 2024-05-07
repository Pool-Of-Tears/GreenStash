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


package com.starry.greenstash.ui.screens.home.composables

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.starry.greenstash.MainActivity
import com.starry.greenstash.R
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.database.transaction.TransactionType
import com.starry.greenstash.ui.navigation.Screens
import com.starry.greenstash.ui.screens.home.GoalCardStyle
import com.starry.greenstash.ui.screens.home.HomeViewModel
import com.starry.greenstash.utils.Constants
import com.starry.greenstash.utils.GoalTextUtils
import com.starry.greenstash.utils.ImageUtils
import com.starry.greenstash.utils.Utils
import com.starry.greenstash.utils.getActivity
import com.starry.greenstash.utils.strongHapticFeedback
import com.starry.greenstash.utils.weakHapticFeedback
import kotlinx.coroutines.launch


@Composable
fun GoalLazyColumnItem(
    context: Context,
    viewModel: HomeViewModel,
    item: GoalWithTransactions,
    snackBarHostState: SnackbarHostState,
    navController: NavController,
    currentIndex: Int
) {
    val settingsVM = (context.getActivity() as MainActivity).settingsViewModel
    val goalCardStyle = settingsVM.goalCardStyle.observeAsState().value!!

    val coroutineScope = rememberCoroutineScope()
    val progressPercent = remember(item.goal.goalId) {
        ((item.getCurrentlySavedAmount() / item.goal.targetAmount) * 100).toInt()
    }

    val openDeleteDialog = remember { mutableStateOf(false) }
    val localView = LocalView.current

    when (goalCardStyle) {
        GoalCardStyle.Classic -> {
            GoalItemClassic(title = item.goal.title,
                primaryText = GoalTextUtils.buildPrimaryText(
                    context = context,
                    progressPercent = progressPercent,
                    goalItem = item,
                    currencyCode = viewModel.getDefaultCurrency()
                ),
                secondaryText = GoalTextUtils.buildSecondaryText(
                    context = context,
                    goalItem = item,
                    currencyCode = viewModel.getDefaultCurrency(),
                    datePattern = viewModel.getDateFormatPattern()
                ),
                goalProgress = progressPercent.toFloat() / 100,
                goalImage = item.goal.goalImage,
                onDepositClicked = {
                    localView.weakHapticFeedback()
                    if (item.getCurrentlySavedAmount() >= item.goal.targetAmount) {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(context.getString(R.string.goal_already_achieved))
                        }
                    } else {
                        navController.navigate(
                            Screens.DWScreen.withGoalId(
                                goalId = item.goal.goalId.toString(),
                                trasactionType = TransactionType.Deposit.name
                            )
                        )
                    }
                },
                onWithdrawClicked = {
                    localView.weakHapticFeedback()
                    if (item.getCurrentlySavedAmount() == 0f.toDouble()) {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(context.getString(R.string.withdraw_button_error))
                        }
                    } else {
                        navController.navigate(
                            Screens.DWScreen.withGoalId(
                                goalId = item.goal.goalId.toString(),
                                trasactionType = TransactionType.Withdraw.name
                            )
                        )
                    }
                },
                onInfoClicked = {
                    localView.weakHapticFeedback()
                    navController.navigate(
                        Screens.GoalInfoScreen.withGoalId(
                            goalId = item.goal.goalId.toString()
                        )
                    )
                },
                onEditClicked = {
                    localView.weakHapticFeedback()
                    navController.navigate(
                        Screens.InputScreen.withGoalToEdit(
                            goalId = item.goal.goalId.toString()
                        )
                    )
                },
                onDeleteClicked = {
                    localView.strongHapticFeedback()
                    openDeleteDialog.value = true
                }
            )

        }

        GoalCardStyle.Compact -> {
            if (currentIndex == 0) {
                Spacer(modifier = Modifier.height(5.dp))
            }
            val goalIcon by remember(item.goal.goalIconId) {
                mutableStateOf(
                    ImageUtils.createIconVector(
                        item.goal.goalIconId ?: Constants.DEFAULT_GOAL_ICON_ID
                    )!!
                )
            }

            GoalItemCompact(
                title = item.goal.title,
                savedAmount = Utils.formatCurrency(
                    item.getCurrentlySavedAmount(),
                    viewModel.getDefaultCurrency()
                ),
                daysLeftText = GoalTextUtils.getRemainingDaysText(
                    context = context,
                    goalItem = item,
                    datePattern = viewModel.getDateFormatPattern()
                ),
                goalProgress = progressPercent.toFloat() / 100,
                goalIcon = goalIcon,
                onDepositClicked = {
                    localView.weakHapticFeedback()
                    if (item.getCurrentlySavedAmount() >= item.goal.targetAmount) {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(context.getString(R.string.goal_already_achieved))
                        }
                    } else {
                        navController.navigate(
                            Screens.DWScreen.withGoalId(
                                goalId = item.goal.goalId.toString(),
                                trasactionType = TransactionType.Deposit.name
                            )
                        )
                    }
                },
                onWithdrawClicked = {
                    localView.weakHapticFeedback()
                    if (item.getCurrentlySavedAmount() == 0f.toDouble()) {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(context.getString(R.string.withdraw_button_error))
                        }
                    } else {
                        navController.navigate(
                            Screens.DWScreen.withGoalId(
                                goalId = item.goal.goalId.toString(),
                                trasactionType = TransactionType.Withdraw.name
                            )
                        )
                    }
                },
                onInfoClicked = {
                    localView.weakHapticFeedback()
                    navController.navigate(
                        Screens.GoalInfoScreen.withGoalId(
                            goalId = item.goal.goalId.toString()
                        )
                    )
                },
                onEditClicked = {
                    localView.weakHapticFeedback()
                    navController.navigate(
                        Screens.InputScreen.withGoalToEdit(
                            goalId = item.goal.goalId.toString()
                        )
                    )
                },
                onDeleteClicked = {
                    localView.strongHapticFeedback()
                    openDeleteDialog.value = true
                }
            )
        }
    }

    HomeDialogs(
        openDeleteDialog = openDeleteDialog,
        onDeleteConfirmed = {
            viewModel.deleteGoal(item.goal)
            /*coroutineScope.launch {
                snackBarHostState.showSnackbar(context.getString(R.string.goal_delete_success))
            }*/
        }
    )
}
