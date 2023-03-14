package com.starry.greenstash.ui.screens.home.composables

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.starry.greenstash.ui.screens.home.viewmodels.SearchWidgetState
import com.starry.greenstash.ui.screens.settings.viewmodels.DateStyle
import com.starry.greenstash.utils.PreferenceUtils
import com.starry.greenstash.utils.Utils
import com.starry.greenstash.utils.validateAmount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = hiltViewModel()
    val allGoals = viewModel.allGoals.observeAsState(listOf()).value

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val items = listOf(DrawerScreens.Home, DrawerScreens.Backups, DrawerScreens.Settings)
    val selectedItem = remember { mutableStateOf(items[0]) }

    val searchWidgetState by viewModel.searchWidgetState
    val searchTextState by viewModel.searchTextState

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet(drawerShape = RoundedCornerShape(4.dp)) {
                Spacer(Modifier.height(14.dp))

                Text(
                    text = stringResource(id = R.string.app_name),
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                )

                Divider(
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )

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
                            coroutineScope.launch { drawerState.close() }
                            selectedItem.value = item
                            navController.navigate(item.route)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }) {
        Scaffold(modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackBarHostState) },
            topBar = {
                MainAppBar(
                    searchWidgetState = searchWidgetState,
                    searchTextState = searchTextState,
                    onTextChange = { viewModel.updateSearchTextState(newValue = it) },
                    onMenuClicked = { coroutineScope.launch { drawerState.open() } },
                    onCloseClicked = { viewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED) },
                    onSearchClicked = { println("Meow >~<") },
                    onSearchTriggered = { viewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED) }
                )
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
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (allGoals.isEmpty()) {
                    var showNoGoalsAnimation by remember { mutableStateOf(false) }

                    LaunchedEffect(key1 = true, block = {
                        delay(200)
                        showNoGoalsAnimation = true
                    })

                    if (showNoGoalsAnimation) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val compositionResult: LottieCompositionResult =
                                rememberLottieComposition(
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
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 12.dp, end = 12.dp)
                            )

                            Spacer(modifier = Modifier.weight(2f))
                        }
                    }

                } else {

                    if (searchTextState.isNotEmpty() && searchTextState.isNotBlank()) {
                        val filteredList: ArrayList<GoalWithTransactions> = ArrayList()
                        for (goalItem in allGoals) {
                            if (goalItem.goal.title.lowercase(Locale.getDefault())
                                    .contains(searchTextState.lowercase(Locale.getDefault()))
                            ) {
                                filteredList.add(goalItem)
                            }
                        }
                        if (allGoals.isNotEmpty() && filteredList.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val compositionResult: LottieCompositionResult =
                                    rememberLottieComposition(
                                        spec = LottieCompositionSpec.RawRes(R.raw.goal_not_found_lottie)
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
                                    text = stringResource(id = R.string.search_goal_not_found),
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
                                items(
                                    filteredList.size,
                                    key = { k -> k },
                                    contentType = { 0 }) { idx ->
                                    val item = filteredList[idx]
                                    GoalLazyColumnItem(
                                        context = context,
                                        viewModel = viewModel,
                                        item = item,
                                        coroutineScope = coroutineScope,
                                        snackBarHostState = snackBarHostState,
                                        navController = navController
                                    )
                                }
                            }
                        }

                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            items(allGoals.size, key = { k -> k }, contentType = { 0 }) { idx ->
                                val item = allGoals[idx]
                                GoalLazyColumnItem(
                                    context = context,
                                    viewModel = viewModel,
                                    item = item,
                                    coroutineScope = coroutineScope,
                                    snackBarHostState = snackBarHostState,
                                    navController = navController
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@ExperimentalMaterial3Api
@Composable
fun MainAppBar(
    searchWidgetState: SearchWidgetState,
    searchTextState: String,
    onTextChange: (String) -> Unit,
    onMenuClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    onSearchTriggered: () -> Unit
) {
    when (searchWidgetState) {
        SearchWidgetState.CLOSED -> {
            DefaultAppBar(
                onMenuClicked = onMenuClicked,
                onSearchClicked = onSearchTriggered
            )
        }
        SearchWidgetState.OPENED -> {
            SearchAppBar(
                text = searchTextState,
                onTextChange = onTextChange,
                onCloseClicked = onCloseClicked,
                onSearchClicked = onSearchClicked
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun DefaultAppBar(onMenuClicked: () -> Unit, onSearchClicked: () -> Unit) {
    CenterAlignedTopAppBar(title = {
        Text(
            stringResource(id = R.string.home_screen_header),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }, navigationIcon = {
        IconButton(onClick = { onMenuClicked() }) {
            Icon(
                imageVector = Icons.Filled.Menu, contentDescription = null
            )
        }
    }, actions = {
        IconButton(onClick = { onSearchClicked() }) {
            Icon(
                imageVector = Icons.Filled.Search, contentDescription = null
            )
        }
    }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
            4.dp
        )
    )
    )
}

@ExperimentalMaterial3Api
@Composable
fun SearchAppBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(
            4.dp
        )
    ) {
        TextField(modifier = Modifier
            .fillMaxWidth(),
            value = text,
            onValueChange = {
                onTextChange(it)
            },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.home_search_label),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            singleLine = true,
            leadingIcon = {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (text.isNotEmpty()) {
                            onTextChange("")
                        } else {
                            onCloseClicked()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text)
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            ))
    }
}

@ExperimentalMaterial3Api
@Composable
fun GoalLazyColumnItem(
    context: Context,
    viewModel: HomeViewModel,
    item: GoalWithTransactions,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    navController: NavController
) {
    val progressPercent =
        ((item.getCurrentlySavedAmount() / item.goal.targetAmount) * 100).toInt()

    val openDeleteDialog = remember { mutableStateOf(false) }
    val openDepositDialog = remember { mutableStateOf(false) }
    val openWithdrawDialog = remember { mutableStateOf(false) }

    GoalItem(title = item.goal.title,
        primaryText = buildPrimaryText(context, progressPercent, item),
        secondaryText = buildSecondaryText(context, item),
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
                viewModel.deposit(item.goal, amountDouble, notes)
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

@ExperimentalMaterial3Api
@Composable
fun ActionDialogs(
    openDeleteDialog: MutableState<Boolean>,
    openDepositDialog: MutableState<Boolean>,
    openWithdrawDialog: MutableState<Boolean>,
    onDeleteConfirmed: () -> Unit,
    onDepositConfirmed: (amount: String, notes: String) -> Unit,
    onWithdrawConfirmed: (amount: String, notes: String) -> Unit
) {
    if (openDeleteDialog.value) {
        AlertDialog(onDismissRequest = {
            openDeleteDialog.value = false
        }, title = {
            Text(
                text = stringResource(id = R.string.goal_delete_confirmation),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }, confirmButton = {
            TextButton(onClick = {
                openDeleteDialog.value = false
                onDeleteConfirmed()
            }) {
                Text(stringResource(id = R.string.dialog_confirm_button))
            }
        }, dismissButton = {
            TextButton(onClick = {
                openDeleteDialog.value = false
            }) {
                Text(stringResource(id = R.string.cancel))
            }
        })
    }

    if (openDepositDialog.value) {
        val depositTextValue = remember { mutableStateOf("") }
        val transactionNotes = remember { mutableStateOf("") }

        AlertDialog(onDismissRequest = {
            openDepositDialog.value = false
        }, title = {
            Text(
                text = stringResource(id = R.string.deposit_dialog_title),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }, confirmButton = {
            TextButton(onClick = {
                openDepositDialog.value = false
                onDepositConfirmed(
                    depositTextValue.value,
                    transactionNotes.value
                )
            }) {
                Text(stringResource(id = R.string.dialog_confirm_button))
            }
        }, dismissButton = {
            TextButton(onClick = {
                openDepositDialog.value = false
            }) {
                Text(stringResource(id = R.string.cancel))
            }
        }, text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = depositTextValue.value,
                    onValueChange = { newText ->
                        depositTextValue.value = Utils.getValidatedNumber(newText)
                    },
                    modifier = Modifier.fillMaxWidth(0.95f),
                    label = {
                        Text(text = stringResource(id = R.string.transaction_dialog_amount_label))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_amount),
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
                    ),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                /*
                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = transactionNotes.value,
                    onValueChange = { newText -> transactionNotes.value = newText },
                    modifier = Modifier.fillMaxWidth(0.95f),
                    label = {
                        Text(text = stringResource(id = R.string.input_additional_notes))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_additional_notes),
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
                    ),
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                )
                 */
            }
        })
    }

    if (openWithdrawDialog.value) {
        val withdrawTextValue = remember { mutableStateOf("") }
        val transactionNotes = remember { mutableStateOf("") }

        AlertDialog(onDismissRequest = {
            openWithdrawDialog.value = false
        }, title = {
            Text(
                text = stringResource(id = R.string.withdraw_dialog_title),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }, confirmButton = {
            TextButton(onClick = {
                openWithdrawDialog.value = false
                onWithdrawConfirmed(
                    withdrawTextValue.value,
                    transactionNotes.value
                )
            }) {
                Text(stringResource(id = R.string.dialog_confirm_button))
            }
        }, dismissButton = {
            TextButton(onClick = {
                openWithdrawDialog.value = false
            }) {
                Text(stringResource(id = R.string.cancel))
            }
        }, text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = withdrawTextValue.value,
                    onValueChange = { newText ->
                        withdrawTextValue.value = Utils.getValidatedNumber(newText)
                    },
                    modifier = Modifier.fillMaxWidth(0.95f),
                    label = {
                        Text(text = stringResource(id = R.string.transaction_dialog_amount_label))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_amount),
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
                    ),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                /*
                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = transactionNotes.value,
                    onValueChange = { newText -> transactionNotes.value = newText },
                    modifier = Modifier.fillMaxWidth(0.95f),
                    label = {
                        Text(text = stringResource(id = R.string.input_additional_notes))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_additional_notes),
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
                    ),
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                )
                 */
            }
        })
    }
}


private fun buildPrimaryText(
    context: Context, progressPercent: Int, item: GoalWithTransactions
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
        "$defCurrency${Utils.formatCurrency(item.getCurrentlySavedAmount())}",
        "$defCurrency${Utils.formatCurrency(item.goal.targetAmount)}"
    )
    return text
}

private fun buildSecondaryText(context: Context, item: GoalWithTransactions): String {
    val remainingAmount = (item.goal.targetAmount - item.getCurrentlySavedAmount())
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
            val endDate = if (item.goal.deadline.split("/")
                    .first().length == 2 && preferredDateFormat != DateStyle.DateMonthYear.pattern
            ) {
                reverseDate(item.goal.deadline)
            } else if (item.goal.deadline.split("/")
                    .first().length == 4 && preferredDateFormat != DateStyle.YearMonthDate.pattern
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
            var text = context.getString(R.string.goal_days_left).format(endDate, days) + "\n"
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