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
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavController
import com.starry.greenstash.R
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.database.transaction.TransactionType
import com.starry.greenstash.ui.navigation.Screens
import com.starry.greenstash.ui.screens.home.viewmodels.GoalCardStyle
import com.starry.greenstash.ui.screens.home.viewmodels.HomeViewModel
import com.starry.greenstash.utils.Constants
import com.starry.greenstash.utils.ImageUtils
import com.starry.greenstash.utils.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun GoalLazyColumnItem(
    context: Context,
    viewModel: HomeViewModel,
    item: GoalWithTransactions,
    snackBarHostState: SnackbarHostState,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val progressPercent = remember {
        ((item.getCurrentlySavedAmount() / item.goal.targetAmount) * 100).toInt()
    }

    val openDeleteDialog = remember { mutableStateOf(false) }

    when (viewModel.goalCardStyle) {
        GoalCardStyle.Classic -> {
            GoalItemClassic(title = item.goal.title,
                primaryText = viewModel.goalTextUtil.buildPrimaryText(
                    context,
                    progressPercent,
                    item
                ),
                secondaryText = viewModel.goalTextUtil.buildSecondaryText(context, item),
                goalProgress = progressPercent.toFloat() / 100,
                goalImage = item.goal.goalImage,
                onDepositClicked = {
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
                    navController.navigate(
                        Screens.GoalInfoScreen.withGoalId(
                            goalId = item.goal.goalId.toString()
                        )
                    )
                },
                onEditClicked = {
                    navController.navigate(
                        Screens.InputScreen.withGoalToEdit(
                            goalId = item.goal.goalId.toString()
                        )
                    )
                },
                onDeleteClicked = { openDeleteDialog.value = true })

            HomeDialogs(
                openDeleteDialog = openDeleteDialog,
                onDeleteConfirmed = {
                    viewModel.deleteGoal(item.goal)
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(context.getString(R.string.goal_delete_success))
                    }
                }
            )
        }

        GoalCardStyle.Compact -> {
            val goalIcon = remember {
                ImageUtils.createIconVector(
                    item.goal.goalIconId ?: Constants.DEFAULT_GOAL_ICON_ID
                )!!
            }
            GoalItemCompact(
                title = item.goal.title,
                savedAmount = Utils.formatCurrency(
                    item.getCurrentlySavedAmount(),
                    viewModel.getDefaultCurrency()
                ),
                daysLeftText = viewModel.goalTextUtil.getRemainingDaysText(context, item),
                goalProgress = progressPercent.toFloat() / 100,
                goalIcon = goalIcon,
                onDepositClicked = {
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
                    navController.navigate(
                        Screens.GoalInfoScreen.withGoalId(
                            goalId = item.goal.goalId.toString()
                        )
                    )
                },
                onEditClicked = {
                    navController.navigate(
                        Screens.InputScreen.withGoalToEdit(
                            goalId = item.goal.goalId.toString()
                        )
                    )
                },
                onDeleteClicked = {
                    println("Delete Clicked")
                    openDeleteDialog.value = true
                }
            )
        }
    }

    HomeDialogs(
        openDeleteDialog = openDeleteDialog,
        onDeleteConfirmed = {
            viewModel.deleteGoal(item.goal)
            coroutineScope.launch {
                snackBarHostState.showSnackbar(context.getString(R.string.goal_delete_success))
            }
        }
    )
}
