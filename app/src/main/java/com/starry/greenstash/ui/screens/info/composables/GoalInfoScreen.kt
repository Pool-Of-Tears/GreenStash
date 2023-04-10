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

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.starry.greenstash.MainActivity
import com.starry.greenstash.R
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.database.transaction.Transaction
import com.starry.greenstash.database.transaction.TransactionType
import com.starry.greenstash.ui.common.ExpandableCard
import com.starry.greenstash.ui.common.ExpandableTextCard
import com.starry.greenstash.ui.screens.info.viewmodels.InfoViewModel
import com.starry.greenstash.ui.screens.settings.viewmodels.DateStyle
import com.starry.greenstash.ui.screens.settings.viewmodels.ThemeMode
import com.starry.greenstash.utils.PreferenceUtils
import com.starry.greenstash.utils.Utils
import com.starry.greenstash.utils.getActivity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


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
            )
        }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack, contentDescription = null
                )
            }
        }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
        )
        )
    }, content = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(it)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val currencySymbol =
                    PreferenceUtils.getString(PreferenceUtils.DEFAULT_CURRENCY, "$")!!
                val progressPercent =
                    ((state.goalData!!.getCurrentlySavedAmount() / state.goalData.goal.targetAmount) * 100).toInt()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    GoalInfoCard(
                        currencySymbol,
                        Utils.formatCurrency(Utils.roundDecimal(state.goalData.goal.targetAmount)),
                        Utils.formatCurrency(Utils.roundDecimal(state.goalData.getCurrentlySavedAmount())),
                        daysLeft = getRemainingDaysText(context, state.goalData),
                        progress = progressPercent.toFloat() / 100
                    )
                    if (state.goalData.goal.additionalNotes.isNotEmpty() && state.goalData.goal.additionalNotes.isNotBlank()) {
                        GoalNotesCard(
                            notesText = state.goalData.goal.additionalNotes
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    if (state.goalData.transactions.isNotEmpty()) {
                        TransactionCard(state.goalData.transactions.reversed(), currencySymbol)
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
    })
}


@Composable
fun GoalInfoCard(
    currencySymbol: String,
    targetAmount: String,
    savedAmount: String,
    daysLeft: String,
    progress: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(12.dp),
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

            Row {
                Text(
                    text = currencySymbol,
                    fontWeight = FontWeight.Bold,
                    fontSize = 38.sp,
                    modifier = Modifier.padding(start = 12.dp)
                )
                Text(
                    text = savedAmount,
                    fontWeight = FontWeight.Bold,
                    fontSize = 38.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = stringResource(id = R.string.info_card_remaining_amount).format("$currencySymbol $targetAmount"),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = progress,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .padding(start = 12.dp, end = 12.dp)
                    .clip(RoundedCornerShape(40.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = daysLeft,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
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

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun TransactionCard(transactions: List<Transaction>, currencySymbol: String) {
    ExpandableCard(
        title = stringResource(id = R.string.info_transaction_card_title), expanded = true
    ) {
        transactions.forEach {
            TransactionItem(
                transactionType = it.type,
                amount = "$currencySymbol${Utils.formatCurrency(Utils.roundDecimal(it.amount))}",
                date = it.getTransactionDate()
            )
        }
    }
}

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun TransactionItem(transactionType: TransactionType, amount: String, date: String) {
    val amountPrefix: String
    val amountColor: Color
    val activity = LocalContext.current.getActivity() as MainActivity

    if (transactionType == TransactionType.Deposit) {
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
                text = "$amountPrefix$amount",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = amountColor
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = date, fontWeight = FontWeight.Medium, fontSize = 16.sp)
        }
        Divider(
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .clip(RoundedCornerShape(50.dp)),
            thickness = 0.8.dp
        )
    }

}


fun getRemainingDaysText(context: Context, goalItem: GoalWithTransactions): String {
    if (goalItem.getCurrentlySavedAmount() >= goalItem.goal.targetAmount) {
        return context.getString(R.string.info_card_goal_achieved)
    } else {
        if (goalItem.goal.deadline.isNotEmpty() && goalItem.goal.deadline.isNotBlank()) {
            // calculate remaining days between today and endDate (deadline).
            val preferredDateFormat = PreferenceUtils.getString(
                PreferenceUtils.DATE_FORMAT, DateStyle.DateMonthYear.pattern
            )
            val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(preferredDateFormat)
            val startDate = LocalDateTime.now().format(dateFormatter)

            /**
             * If date format is set as DD/MM/YYYY but date in database is saved
             * in YYYY/MM/DD format, then reverse the date string before parsing.
             */
            val reverseDate: (String) -> String = {
                goalItem.goal.deadline.split("/").reversed().joinToString(separator = "/")
            }
            val endDate = if (goalItem.goal.deadline.split("/")
                    .first().length == 2 && preferredDateFormat != DateStyle.DateMonthYear.pattern
            ) {
                reverseDate(goalItem.goal.deadline)
            } else if (goalItem.goal.deadline.split("/")
                    .first().length == 4 && preferredDateFormat != DateStyle.YearMonthDate.pattern
            ) {
                reverseDate(goalItem.goal.deadline)
            } else {
                goalItem.goal.deadline
            }

            val startDateValue: LocalDate = LocalDate.parse(startDate, dateFormatter)
            val endDateValue: LocalDate = LocalDate.parse(endDate, dateFormatter)
            val days: Long = ChronoUnit.DAYS.between(startDateValue, endDateValue)
            return context.getString(R.string.info_card_remaining_days).format(days)
        } else {
            return context.getString(R.string.info_card_no_deadline_set)
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@Composable
@Preview(showBackground = true)
fun GoalInfoPV() {
    GoalInfoScreen(goalId = "", navController = rememberNavController())
}