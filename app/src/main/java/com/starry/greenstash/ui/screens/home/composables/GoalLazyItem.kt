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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.navigation.NavController
import com.starry.greenstash.R
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.ui.navigation.Screens
import com.starry.greenstash.ui.screens.home.viewmodels.HomeViewModel
import com.starry.greenstash.utils.GoalTextUtils
import com.starry.greenstash.utils.Utils
import com.starry.greenstash.utils.validateAmount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun GoalLazyColumnItem(
    context: Context,
    viewModel: HomeViewModel,
    item: GoalWithTransactions,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    bottomSheetState: ModalBottomSheetState,
    navController: NavController
) {
    val progressPercent =
        ((item.getCurrentlySavedAmount() / item.goal.targetAmount) * 100).toInt()

    val openDeleteDialog = remember { mutableStateOf(false) }
    val openDepositDialog = remember { mutableStateOf(false) }
    val openWithdrawDialog = remember { mutableStateOf(false) }

    val hapticFeedback = LocalHapticFeedback.current

    GoalItem(title = item.goal.title,
        primaryText = GoalTextUtils.buildPrimaryText(context, progressPercent, item),
        secondaryText = GoalTextUtils.buildSecondaryText(context, item),
        goalProgress = progressPercent.toFloat() / 100,
        goalImage = item.goal.goalImage,
        onDepositClicked = {
            if (item.getCurrentlySavedAmount() >= item.goal.targetAmount) {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(context.getString(R.string.goal_already_achieved))
                }
            } else {
                openDepositDialog.value = true
            }
        },
        onWithdrawClicked = {
            if (item.getCurrentlySavedAmount() == 0f.toDouble()) {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(context.getString(R.string.withdraw_btn_error))
                }
            } else {
                openWithdrawDialog.value = true
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

    ActionDialogs(
        openDeleteDialog = openDeleteDialog,
        openDepositDialog = openDepositDialog,
        openWithdrawDialog = openWithdrawDialog,
        onDeleteConfirmed = {
            viewModel.deleteGoal(item.goal)
            coroutineScope.launch {
                snackBarHostState.showSnackbar(context.getString(R.string.goal_delete_success))
            }
        }, onDepositConfirmed = { amount, notes ->
            if (!amount.validateAmount()) {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(context.getString(R.string.amount_empty_err))
                }
            } else if (item.getCurrentlySavedAmount() >= item.goal.targetAmount) {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(context.getString(R.string.goal_already_achieved))
                }
            } else {
                val amountDouble = Utils.roundDecimal(amount.toDouble())
                viewModel.deposit(item.goal, amountDouble, notes, onGoalAchieved = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    coroutineScope.launch {
                        if (!bottomSheetState.isVisible) {
                            bottomSheetState.show()
                        }
                    }
                })
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(context.getString(R.string.deposit_successful))
                }
            }
        }, onWithdrawConfirmed = { amount, notes ->
            if (!amount.validateAmount()) {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(context.getString(R.string.amount_empty_err))
                }
            } else {
                val amountDouble = Utils.roundDecimal(amount.toDouble())
                if (amountDouble > item.getCurrentlySavedAmount()) {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(context.getString(R.string.withdraw_overflow_error))
                    }
                } else {
                    viewModel.withdraw(item.goal, amountDouble, notes)
                }
            }
        }
    )
}
