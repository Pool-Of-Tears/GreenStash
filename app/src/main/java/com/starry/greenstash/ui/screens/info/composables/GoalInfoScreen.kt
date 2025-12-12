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

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.starry.greenstash.R
import com.starry.greenstash.database.goal.GoalPriority
import com.starry.greenstash.database.goal.GoalPriority.High
import com.starry.greenstash.database.goal.GoalPriority.Low
import com.starry.greenstash.database.goal.GoalPriority.Normal
import com.starry.greenstash.ui.common.ExpandableTextCard
import com.starry.greenstash.ui.common.TipCard
import com.starry.greenstash.ui.screens.info.InfoViewModel
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.ui.theme.greenstashNumberFont
import com.starry.greenstash.utils.GoalTextUtils
import com.starry.greenstash.utils.NumberUtils
import com.starry.greenstash.utils.Utils
import com.starry.greenstash.utils.displayName
import com.starry.greenstash.utils.weakHapticFeedback
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalInfoScreen(goalId: String, navController: NavController) {
    val view = LocalView.current
    val context = LocalContext.current

    val viewModel: InfoViewModel = hiltViewModel()
    val state = viewModel.state

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true, block = { viewModel.loadGoalData(goalId.toLong()) })

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = stringResource(id = R.string.info_screen_header),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = greenstashFont
                    )
                }, navigationIcon = {
                    IconButton(onClick = {
                        view.weakHapticFeedback()
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                })
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(it)
        ) {
            val goalData = state.goalData?.collectAsState(initial = null)?.value

            Crossfade(
                targetState = goalData == null,
                label = "GoalDataLoading"
            ) { isGoalDataLoading ->
                if (isGoalDataLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    val currencySymbol = viewModel.getDefaultCurrencyValue()
                    val progressPercent =
                        ((goalData!!.getCurrentlySavedAmount() / goalData.goal.targetAmount) * 100).toInt()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        GoalInfoCard(
                            currencySymbol = currencySymbol,
                            targetAmount = goalData.goal.targetAmount,
                            savedAmount = goalData.getCurrentlySavedAmount(),
                            daysLeftText = GoalTextUtils.getRemainingDaysText(
                                context = context,
                                goalItem = goalData,
                                dateStyle = viewModel.getDateStyle()
                            ),
                            progress = progressPercent.toFloat() / 100
                        )
                        GoalPriorityCard(
                            goalPriority = goalData.goal.priority,
                            reminders = goalData.goal.reminder
                        )
                        if (goalData.goal.additionalNotes.isNotBlank()) {
                            GoalNotesCard(
                                notesText = goalData.goal.additionalNotes
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                        if (goalData.transactions.isNotEmpty()) {
                            // Show tooltip for swipe functionality.
                            val showTransactionSwipeTip = remember { mutableStateOf(false) }
                            LaunchedEffect(key1 = true) {
                                if (viewModel.shouldShowTransactionTip()) {
                                    delay(800) // Don't show immediately.
                                    showTransactionSwipeTip.value = true
                                }
                            }

                            TipCard(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                description = stringResource(id = R.string.info_transaction_swipe_tip),
                                showTipCard = showTransactionSwipeTip.value,
                                onDismissRequest = {
                                    showTransactionSwipeTip.value = false
                                    viewModel.transactionTipDismissed()
                                }
                            )

                            // Show transaction items.
                            TransactionItems(
                                goalData.getOrderedTransactions(),
                                currencySymbol,
                                viewModel
                            )

                        } else {
                            NoTransactionAnim()
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
        NumberUtils.formatCurrency(NumberUtils.roundDecimal(targetAmount), currencySymbol)
    val formattedSavedAmount =
        NumberUtils.formatCurrency(NumberUtils.roundDecimal(savedAmount), currencySymbol)
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
                fontFamily = greenstashFont,
                modifier = Modifier.padding(start = 12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formattedSavedAmount,
                fontWeight = FontWeight.Bold,
                fontSize = 38.sp,
                fontFamily = greenstashNumberFont,
                maxLines = 3,
                lineHeight = 1.3f.em,
                overflow = TextOverflow.Ellipsis,
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
                fontFamily = greenstashFont,
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
                    fontFamily = greenstashFont,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
        }
    }
}

@Composable
fun GoalPriorityCard(goalPriority: GoalPriority, reminders: Boolean) {
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
            val (reminderIcon, reminderText) = when (reminders) {
                true -> Pair(
                    Icons.Filled.NotificationsActive,
                    stringResource(id = R.string.info_reminder_status_on)
                )

                false -> Pair(
                    Icons.Filled.NotificationsOff,
                    stringResource(id = R.string.info_reminder_status_off)
                )
            }

            Box(modifier = Modifier.padding(start = 8.dp)) {
                PriorityIndicator(modifier = Modifier.size(13.dp), color = indicatorColor)
            }
            Text(
                modifier = Modifier.padding(start = 12.dp),
                text = stringResource(id = R.string.info_goal_priority).format(goalPriority.displayName()),
                fontWeight = FontWeight.Medium,
                fontFamily = greenstashFont
            )

            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = reminderIcon, contentDescription = reminderText)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun GoalNotesCard(notesText: String) {
    val extractedUrl = Utils.extractFirstUrl(notesText)
    ExpandableTextCard(
        title = stringResource(id = R.string.info_notes_card_title),
        description = notesText,
        showCopyButton = true,
        urlToOpen = extractedUrl
    )
}

@Composable
private fun NoTransactionAnim() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val compositionResult: LottieCompositionResult =
            rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(R.raw.no_transaction_found_lottie)
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
            progress = { progressAnimation },
            modifier = Modifier.size(320.dp),
            enableMergePaths = true
        )

        Text(
            text = stringResource(id = R.string.info_goal_no_transactions),
            fontFamily = greenstashFont,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp)
                .offset(y = (-16).dp)
        )

        Spacer(modifier = Modifier.weight(2f))
    }
}


@Composable
@Preview
fun GoalInfoPV() {
    GoalInfoScreen(goalId = "", navController = rememberNavController())
}