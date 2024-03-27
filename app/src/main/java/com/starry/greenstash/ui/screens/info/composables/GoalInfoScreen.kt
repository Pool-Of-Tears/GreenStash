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


package com.starry.greenstash.ui.screens.info.composables

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.starry.greenstash.MainActivity
import com.starry.greenstash.R
import com.starry.greenstash.database.goal.GoalPriority
import com.starry.greenstash.database.goal.GoalPriority.High
import com.starry.greenstash.database.goal.GoalPriority.Low
import com.starry.greenstash.database.goal.GoalPriority.Normal
import com.starry.greenstash.database.transaction.Transaction
import com.starry.greenstash.database.transaction.TransactionType
import com.starry.greenstash.ui.common.DotIndicator
import com.starry.greenstash.ui.common.ExpandableTextCard
import com.starry.greenstash.ui.screens.info.viewmodels.InfoViewModel
import com.starry.greenstash.ui.screens.settings.viewmodels.ThemeMode
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.ui.theme.greenstashNumberFont
import com.starry.greenstash.utils.Utils
import com.starry.greenstash.utils.getActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox


@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun GoalInfoScreen(goalId: String, navController: NavController) {

    val viewModel: InfoViewModel = hiltViewModel()
    val state = viewModel.state
    val context = LocalContext.current

    LaunchedEffect(key1 = true, block = { viewModel.loadGoalData(goalId.toLong()) })

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(modifier = Modifier.fillMaxWidth(), title = {
            Text(
                text = stringResource(id = R.string.info_screen_header),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = greenstashFont
            )
        }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null
                )
            }
        }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
        )
        )
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(it)
        ) {
            val goalData = state.goalData?.collectAsState(initial = null)?.value

            if (goalData == null) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val currencySymbol = viewModel.getDefaultCurrencyValue()
                val progressPercent =
                    ((goalData.getCurrentlySavedAmount() / goalData.goal.targetAmount) * 100).toInt()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    GoalInfoCard(
                        currencySymbol = currencySymbol,
                        targetAmount = goalData.goal.targetAmount,
                        savedAmount = goalData.getCurrentlySavedAmount(),
                        daysLeftText = viewModel.goalTextUtils.getRemainingDaysText(
                            context, goalData
                        ),
                        progress = progressPercent.toFloat() / 100
                    )
                    GoalPriorityCard(goalPriority = goalData.goal.priority)
                    if (goalData.goal.additionalNotes.isNotEmpty() && goalData.goal.additionalNotes.isNotBlank()) {
                        GoalNotesCard(
                            notesText = goalData.goal.additionalNotes
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    if (goalData.transactions.isNotEmpty()) {
                        TransactionCard(goalData.transactions.reversed(), currencySymbol, viewModel)
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val compositionResult: LottieCompositionResult =
                                rememberLottieComposition(
                                    spec = LottieCompositionSpec.RawRes(R.raw.no_transactions_lottie)
                                )
                            val progressAnimation by animateLottieCompositionAsState(
                                compositionResult.value,
                                isPlaying = true,
                                iterations = 1,
                                speed = 1f
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            LottieAnimation(
                                composition = compositionResult.value,
                                progress = progressAnimation,
                                modifier = Modifier.size(320.dp),
                                enableMergePaths = true
                            )

                            Text(
                                text = stringResource(id = R.string.info_goal_no_transactions),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(start = 12.dp, end = 12.dp)
                            )

                            Spacer(modifier = Modifier.weight(2f))
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun GoalInfoCard(
    currencySymbol: String,
    targetAmount: Double,
    savedAmount: Double,
    daysLeftText: String,
    progress: Float
) {
    val formattedTargetAmount =
        Utils.formatCurrency(Utils.roundDecimal(targetAmount), currencySymbol)
    val formattedSavedAmount =
        Utils.formatCurrency(Utils.roundDecimal(savedAmount), currencySymbol)
    val animatedProgress = animateFloatAsState(targetValue = progress, label = "progress")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 12.dp, bottom = 4.dp, start = 12.dp, end = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                4.dp
            )
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.info_card_title),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formattedSavedAmount,
                fontWeight = FontWeight.Bold,
                fontSize = 38.sp,
                fontFamily = greenstashNumberFont,
                modifier = Modifier.padding(start = 8.dp)
            )


            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = stringResource(
                    id = R.string.info_card_remaining_amount,
                    formattedTargetAmount
                ),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { animatedProgress.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .padding(start = 12.dp, end = 12.dp)
                    .clip(RoundedCornerShape(40.dp)),
                color = MaterialTheme.colorScheme.secondary,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${(progress * 100).toInt()}% | $daysLeftText",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
        }
    }
}

@Composable
fun GoalPriorityCard(goalPriority: GoalPriority) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 12.dp, start = 12.dp, end = 12.dp, top = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                4.dp
            )
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val indicatorColor = when (goalPriority) {
                High -> Color(0xFFFFA200)
                Normal -> Color.Green
                Low -> Color.Blue
            }
            Box(modifier = Modifier.padding(start = 8.dp)) {
                DotIndicator(modifier = Modifier.size(8.2f.dp), color = indicatorColor)
            }
            Text(
                modifier = Modifier.padding(start = 14.dp),
                text = stringResource(id = R.string.info_goal_priority).format(goalPriority.name),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun GoalNotesCard(notesText: String) {
    ExpandableTextCard(
        title = stringResource(id = R.string.info_notes_card_title), description = notesText
    )
}

@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun TransactionCard(
    transactions: List<Transaction>,
    currencySymbol: String,
    viewModel: InfoViewModel
) {
    val settingsVM = (LocalContext.current.getActivity() as MainActivity).settingsViewModel
    transactions.forEach {
        val deleteAction = SwipeAction(
            icon = painterResource(
                id = if (settingsVM.getCurrentTheme() == ThemeMode.Light)
                    R.drawable.ic_goal_delete else R.drawable.ic_goal_delete_white
            ),
            background = Color.Red,
            onSwipe = {
                viewModel.deleteTransaction(it)
            }
        )
        SwipeableActionsBox(
            endActions = listOf(deleteAction),
            swipeThreshold = 85.dp,
            content = { TransactionItem(it, currencySymbol) }
        )
    }

}

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun TransactionItem(transaction: Transaction, currencySymbol: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                4.dp
            )
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            val amountPrefix: String
            val amountColor: Color
            val activity = LocalContext.current.getActivity() as MainActivity

            if (transaction.type == TransactionType.Deposit) {
                amountPrefix = "+"
                amountColor = if (activity.settingsViewModel.getCurrentTheme() == ThemeMode.Light) {
                    Color(0xFF037d50)
                } else {
                    Color(0xFF04df8f)
                }
            } else {
                amountPrefix = "-"
                amountColor = if (activity.settingsViewModel.getCurrentTheme() == ThemeMode.Light) {
                    Color(0xFFd90000)
                } else {
                    Color(0xFFff1515)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row {
                    Text(
                        text = "$amountPrefix$currencySymbol${transaction.amount}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = amountColor
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = transaction.getTransactionDate(),
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }

                if (transaction.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = transaction.notes,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )
                }
            }
        }

    }
}

@ExperimentalCoroutinesApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@Composable
@Preview
fun GoalInfoPV() {
    GoalInfoScreen(goalId = "", navController = rememberNavController())
}