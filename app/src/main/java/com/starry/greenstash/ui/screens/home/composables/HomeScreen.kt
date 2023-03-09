package com.starry.greenstash.ui.screens.home.composables

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.starry.greenstash.database.GoalWithTransactions
import com.starry.greenstash.ui.navigation.DrawerScreens
import com.starry.greenstash.ui.navigation.Screens
import com.starry.greenstash.ui.screens.home.viewmodels.HomeViewModel
import com.starry.greenstash.ui.screens.settings.viewmodels.DateStyle
import com.starry.greenstash.utils.PreferenceUtils
import com.starry.greenstash.utils.Utils
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = hiltViewModel()
    val allGoals = viewModel.allGoals.observeAsState(listOf()).value

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val items = listOf(DrawerScreens.Home, DrawerScreens.Backups, DrawerScreens.Settings)
    val selectedItem = remember { mutableStateOf(items[0]) }

    ModalNavigationDrawer(drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet(drawerShape = RoundedCornerShape(4.dp)) {
                Spacer(Modifier.height(14.dp))
                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = item.icon),
                                contentDescription = null
                            )
                        },
                        label = { Text(item.name) },
                        selected = item == selectedItem.value,
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedItem.value = item
                            navController.navigate(item.route)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }) {
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(title = {
                    Text(
                        stringResource(id = R.string.home_screen_header),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }, navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Filled.Menu, contentDescription = null
                        )
                    }
                }, actions = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Filled.Search, contentDescription = null
                        )
                    }
                })
            },

            floatingActionButton = {
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(end = 10.dp, bottom = 12.dp),
                    onClick = { navController.navigate(Screens.InputScreen.route) },
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Filled.Add, contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.new_goal_fab),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                if (allGoals.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val compositionResult: LottieCompositionResult = rememberLottieComposition(
                            spec = LottieCompositionSpec.RawRes(R.raw.no_goal_set_lottie)
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
                            text = stringResource(id = R.string.no_goal_set),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp)
                        )

                        Spacer(modifier = Modifier.weight(2f))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        items(allGoals.size, key = { id -> id }) { idx ->
                            val item = allGoals[idx]
                            val progressPercent =
                                ((item.getCurrentAmount() / item.goal.targetAmount) * 100).toInt()

                            GoalItem(title = item.goal.title,
                                primaryText = buildPrimaryText(context, progressPercent, item),
                                secondaryText = buildSecondaryText(context, item),
                                goalProgress = 0.5f,
                                goalImage = item.goal.goalImage,
                                onDepositClicked = { /*TODO*/ },
                                onWithdrawClicked = { /*TODO*/ },
                                onInfoClicked = { /*TODO*/ },
                                onEditClicked = { /*TODO*/ },
                                onDeleteClicked = { viewModel.deleteGoal(item.goal) })
                        }
                    }
                }
            }
        }
    }
}


private fun buildPrimaryText(
    context: Context,
    progressPercent: Int,
    item: GoalWithTransactions
): String {
    var text: String = when {
        progressPercent <= 25 -> {
            context.getString(R.string.progress_greet1)
        }
        progressPercent in 26..50 -> {
            context.getString(R.string.progress_greet2)
        }
        progressPercent in 51..75 -> {
            context.getString(R.string.progress_greet3)
        }
        progressPercent in 76..99 -> {
            context.getString(R.string.progress_greet4)
        }
        else -> {
            context.getString(R.string.progress_greet5)
        }
    }
    val defCurrency = PreferenceUtils.getString(PreferenceUtils.DEFAULT_CURRENCY, "")
    text += if (progressPercent < 100) {
        "\n" + context.getString(R.string.currently_saved_incomplete)
    } else {
        "\n" + context.getString(R.string.currently_saved_complete)
    }
    text = text.format(
        "$defCurrency${Utils.formatCurrency(item.getCurrentAmount())}",
        "$defCurrency${Utils.formatCurrency(item.goal.targetAmount)}"
    )
    return text
}

private fun buildSecondaryText(context: Context, item: GoalWithTransactions): String {
    val remainingAmount = (item.goal.targetAmount - item.getCurrentAmount())
    if ((remainingAmount > 0f)) {
        if (item.goal.deadline.isNotEmpty() && item.goal.deadline.isNotBlank()) {
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
                item.goal.deadline.split("/").reversed().joinToString(separator = "/")
            }
            val endDate =
                if (item.goal.deadline.split("/").first().length == 2
                    && preferredDateFormat != DateStyle.DateMonthYear.pattern
                ) {
                    reverseDate(item.goal.deadline)
                } else if (item.goal.deadline.split("/").first().length == 4
                    && preferredDateFormat != DateStyle.YearMonthDate.pattern
                ) {
                    reverseDate(item.goal.deadline)
                } else {
                    item.goal.deadline
                }

            val startDateValue: LocalDate = LocalDate.parse(startDate, dateFormatter)
            val endDateValue: LocalDate = LocalDate.parse(endDate, dateFormatter)
            val days: Long = ChronoUnit.DAYS.between(startDateValue, endDateValue)
            val defCurrency = PreferenceUtils.getString(PreferenceUtils.DEFAULT_CURRENCY, "")
            // build description string.
            var text =
                context.getString(R.string.goal_days_left).format(endDate, days) + "\n"
            if (days > 2) {
                text += context.getString(R.string.goal_approx_saving).format(
                    "$defCurrency${
                        Utils.formatCurrency(
                            Utils.roundDecimal(
                                remainingAmount / days
                            )
                        )
                    }"
                )
                text += context.getString(R.string.goal_approx_saving_day)
                if (days > 14) {
                    val weeks = days / 7
                    text = text.dropLast(1) // remove full stop
                    text += ", $defCurrency${Utils.formatCurrency(Utils.roundDecimal(remainingAmount / weeks))}/${
                        context.getString(
                            R.string.goal_approx_saving_week
                        )
                    }"
                    if (days > 60) {
                        val months = days / 30
                        text = text.dropLast(1) // remove full stop
                        text += ", $defCurrency${
                            Utils.formatCurrency(
                                Utils.roundDecimal(
                                    remainingAmount / months
                                )
                            )
                        }/${
                            context.getString(
                                R.string.goal_approx_saving_month
                            )
                        }"
                    }
                }
            }
            return text
        } else {
            return context.getString(R.string.no_goal_deadline_set)
        }
    } else {
        return context.getString(R.string.goal_achieved_desc)
    }

}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
@Preview
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}