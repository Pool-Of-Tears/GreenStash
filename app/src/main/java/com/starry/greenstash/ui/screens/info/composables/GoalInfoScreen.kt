package com.starry.greenstash.ui.screens.info.composables

import android.content.Context
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.starry.greenstash.R
import com.starry.greenstash.database.Goal
import com.starry.greenstash.database.Transaction
import com.starry.greenstash.database.TransactionType
import com.starry.greenstash.ui.common.ExpandableCard
import com.starry.greenstash.ui.common.ExpandableTextCard
import com.starry.greenstash.ui.screens.info.viewmodels.InfoViewModel
import com.starry.greenstash.ui.screens.settings.viewmodels.DateStyle
import com.starry.greenstash.utils.PreferenceUtils
import com.starry.greenstash.utils.Utils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


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
                .verticalScroll(rememberScrollState())
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val compositionResult: LottieCompositionResult = rememberLottieComposition(
                        spec = LottieCompositionSpec.RawRes(R.raw.transactions_loading_lottie)
                    )
                    val progressAnimation by animateLottieCompositionAsState(
                        compositionResult.value,
                        isPlaying = true,
                        iterations = LottieConstants.IterateForever,
                        speed = 1f
                    )

                    LottieAnimation(
                        composition = compositionResult.value,
                        progress = progressAnimation,
                        modifier = Modifier.size(320.dp),
                        enableMergePaths = true
                    )

                }
            } else {
                val currencySymbol =
                    PreferenceUtils.getString(PreferenceUtils.DEFAULT_CURRENCY, "$")!!
                val progressPercent =
                    ((state.goalData!!.getCurrentlySavedAmount() / state.goalData.goal.targetAmount) * 100).toInt()

                GoalInfoCard(
                    currencySymbol,
                    Utils.formatCurrency(Utils.roundDecimal(state.goalData.goal.targetAmount)),
                    Utils.formatCurrency(Utils.roundDecimal(state.goalData.getCurrentlySavedAmount())),
                    daysLeft = getRemainingDaysText(context, state.goalData.goal),
                    progress = progressPercent.toFloat() / 100
                )
                if (state.goalData.goal.additionalNotes.isNotEmpty() && state.goalData.goal.additionalNotes.isNotBlank()) {
                    GoalNotesCard(
                        notesText = state.goalData.goal.additionalNotes
                    )
                }
                if (state.goalData.transactions.isNotEmpty()) {
                    TransactionCard(state.goalData.transactions.reversed(), currencySymbol)
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val compositionResult: LottieCompositionResult = rememberLottieComposition(
                            spec = LottieCompositionSpec.RawRes(R.raw.no_transactions_lottie)
                        )
                        val progressAnimation by animateLottieCompositionAsState(
                            compositionResult.value,
                            isPlaying = true,
                            iterations = LottieConstants.IterateForever,
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

@ExperimentalMaterial3Api
@Composable
fun TransactionCard(transactions: List<Transaction>, currencySymbol: String) {
    ExpandableCard(
        title = stringResource(id = R.string.info_transaction_card_title), expanded = true
    ) {
        transactions.forEach {
            TransactionItem(
                transactionType = it.type,
                amount = "$currencySymbol ${Utils.formatCurrency(Utils.roundDecimal(it.amount))}",
                date = it.getTransactionDate()
            )
        }
    }
}

@Composable
fun TransactionItem(transactionType: TransactionType, amount: String, date: String) {
    val iconDrawable: Int = if (transactionType == TransactionType.Deposit) {
        R.drawable.ic_transaction_deposit
    } else {
        R.drawable.ic_transaction_wirhdraw
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row {
            Icon(
                imageVector = ImageVector.vectorResource(id = iconDrawable),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = amount, fontWeight = FontWeight.Medium, fontSize = 16.sp)
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


fun getRemainingDaysText(context: Context, goal: Goal): String {
    if (goal.deadline.isNotEmpty() && goal.deadline.isNotBlank()) {
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
            goal.deadline.split("/").reversed().joinToString(separator = "/")
        }
        val endDate = if (goal.deadline.split("/")
                .first().length == 2 && preferredDateFormat != DateStyle.DateMonthYear.pattern
        ) {
            reverseDate(goal.deadline)
        } else if (goal.deadline.split("/")
                .first().length == 4 && preferredDateFormat != DateStyle.YearMonthDate.pattern
        ) {
            reverseDate(goal.deadline)
        } else {
            goal.deadline
        }

        val startDateValue: LocalDate = LocalDate.parse(startDate, dateFormatter)
        val endDateValue: LocalDate = LocalDate.parse(endDate, dateFormatter)
        val days: Long = ChronoUnit.DAYS.between(startDateValue, endDateValue)
        return context.getString(R.string.info_card_remaining_days).format(days)
    } else {
        return context.getString(R.string.info_card_no_deadline_set)
    }
}

@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@Composable
@Preview(showBackground = true)
fun GoalInfoPV() {
    GoalInfoScreen(goalId = "", navController = rememberNavController())
}