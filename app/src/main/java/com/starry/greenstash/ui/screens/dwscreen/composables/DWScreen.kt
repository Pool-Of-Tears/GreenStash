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


package com.starry.greenstash.ui.screens.dwscreen.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.date_time.DateTimeDialog
import com.maxkeppeler.sheets.date_time.models.DateTimeConfig
import com.maxkeppeler.sheets.date_time.models.DateTimeSelection
import com.starry.greenstash.R
import com.starry.greenstash.database.transaction.TransactionType
import com.starry.greenstash.ui.common.DateTimeCard
import com.starry.greenstash.ui.navigation.DrawerScreens
import com.starry.greenstash.ui.navigation.OtherScreens
import com.starry.greenstash.ui.screens.dwscreen.DWViewModel
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.utils.NumberUtils
import com.starry.greenstash.utils.validateAmount
import com.starry.greenstash.utils.weakHapticFeedback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DWScreen(goalId: String, transactionTypeName: String, navController: NavController) {
    val view = LocalView.current
    val context = LocalContext.current
    val viewModel: DWViewModel = hiltViewModel()

    val selectedDateTime = remember {
        mutableStateOf<LocalDateTime>(LocalDateTime.now())
    }
    val dateTimeDialogState = rememberUseCaseState(visible = false)

    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val transactionType = viewModel.convertTransactionType(transactionTypeName)
    val showTransactionAddedAnim = remember { mutableStateOf(false) }

    DateTimeDialog(
        state = dateTimeDialogState,
        selection = DateTimeSelection.DateTime(
            selectedDate = selectedDateTime.value.toLocalDate(),
            selectedTime = selectedDateTime.value.toLocalTime(),
        ) { newDateTime ->
            selectedDateTime.value = newDateTime
        },
        config = DateTimeConfig(locale = Locale.US)
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = if (transactionType == TransactionType.Deposit)
                            stringResource(id = R.string.deposit_screen_title)
                        else stringResource(id = R.string.withdraw_screen_title),
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
                }
            )
        }) { paddingValues ->

        if (showTransactionAddedAnim.value) {
            TransactionAddedAnimation(transactionType)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState(), reverseScrolling = true),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Deposit or Withdraw animation
                MainDWAnimation(transactionType)

                DateTimeCard(
                    selectedDateTime = selectedDateTime.value,
                    dateTimeStyle = { viewModel.getDateStyle() },
                    onClick = { dateTimeDialogState.show() }
                )

                DWInputFields(
                    amountValue = viewModel.state.amount,
                    notesValue = viewModel.state.notes,
                    onAmountChange = { amount ->
                        viewModel.state =
                            viewModel.state.copy(amount = NumberUtils.getValidatedNumber(amount))
                    },
                    onNotesChange = { notes ->
                        viewModel.state = viewModel.state.copy(notes = notes)
                    }
                )

                Button(
                    onClick = {
                        if (!viewModel.state.amount.validateAmount()) {
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar(context.getString(R.string.amount_empty_err))
                            }
                        } else {
                            when (transactionType) {
                                TransactionType.Deposit -> {
                                    viewModel.deposit(
                                        goalId = goalId.toLong(),
                                        dateTime = selectedDateTime.value,
                                        onGoalAchieved = {
                                            coroutineScope.launch {
                                                showTransactionAddedAnim.value = true
                                                delay(1100)
                                                withContext(Dispatchers.Main) {
                                                    navController.navigate(OtherScreens.CongratsScreen)
                                                }
                                            }
                                        }, onComplete = {
                                            navigateToHome(
                                                navController,
                                                coroutineScope,
                                                showTransactionAddedAnim
                                            )
                                        }
                                    )
                                }

                                TransactionType.Withdraw -> {
                                    viewModel.withdraw(
                                        goalId = goalId.toLong(),
                                        dateTime = selectedDateTime.value,
                                        onWithDrawOverflow = {
                                            coroutineScope.launch {
                                                snackBarHostState.showSnackbar(
                                                    context.getString(R.string.withdraw_overflow_error)
                                                )
                                            }
                                        },
                                        onComplete = {
                                            navigateToHome(
                                                navController,
                                                coroutineScope,
                                                showTransactionAddedAnim
                                            )
                                        }
                                    )
                                }

                                TransactionType.Invalid -> {
                                    throw IllegalArgumentException("Invalid transaction type")
                                }
                            }
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = if (transactionType == TransactionType.Deposit)
                            stringResource(id = R.string.deposit_button)
                        else stringResource(id = R.string.withdraw_button),
                        fontFamily = greenstashFont
                    )
                }

            }
        }

    }
}


@Composable
private fun MainDWAnimation(transactionType: TransactionType) {
    val compositionResult: LottieCompositionResult = rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(
            if (transactionType == TransactionType.Deposit) R.raw.dw_deposit_lottie
            else R.raw.dw_withdraw_lottie
        )
    )
    val progressAnimation by animateLottieCompositionAsState(
        compositionResult.value,
        isPlaying = true,
        iterations = 1,
        speed = 1f
    )

    LottieAnimation(
        composition = compositionResult.value,
        progress = { progressAnimation },
        modifier = Modifier
            .size(280.dp)
            .padding(top = 28.dp),
        enableMergePaths = true
    )
}

@Composable
private fun DWInputFields(
    amountValue: String,
    notesValue: String,
    onAmountChange: (String) -> Unit,
    onNotesChange: (String) -> Unit
) {
    OutlinedTextField(
        value = amountValue,
        onValueChange = { newText -> onAmountChange(newText) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 4.dp),
        label = {
            Text(
                text = stringResource(id = R.string.transaction_amount),
                fontFamily = greenstashFont
            )
        },
        leadingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_amount),
                contentDescription = null
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = 0.25f
            ),
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = 0.25f
            ),
        ),
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )

    OutlinedTextField(
        value = notesValue,
        onValueChange = { newText -> onNotesChange(newText) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 2.dp),
        label = {
            Text(
                text = stringResource(id = R.string.input_additional_notes),
                fontFamily = greenstashFont
            )
        },
        leadingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_additional_notes),
                contentDescription = null
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = 0.25f
            ),
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = 0.25f
            ),
        ),
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    )

}

@Composable
private fun TransactionAddedAnimation(transactionType: TransactionType) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val compositionResult: LottieCompositionResult = rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(R.raw.transaction_added_lottie)
        )
        val progressAnimation by animateLottieCompositionAsState(
            compositionResult.value,
            isPlaying = true,
            iterations = 1,
            speed = 1.4f
        )

        Spacer(modifier = Modifier.weight(1f))

        LottieAnimation(
            composition = compositionResult.value,
            progress = { progressAnimation },
            modifier = Modifier.size(320.dp)
        )

        Text(
            text = if (transactionType == TransactionType.Deposit)
                stringResource(id = R.string.deposit_successful)
            else stringResource(id = R.string.withdraw_successful),
            fontWeight = FontWeight.SemiBold,
            fontFamily = greenstashFont,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.weight(1.4f))
    }
}

private fun navigateToHome(
    navController: NavController,
    coroutineScope: CoroutineScope,
    showTransactionAddedAnim: MutableState<Boolean>
) {
    coroutineScope.launch {
        showTransactionAddedAnim.value = true
        delay(1100)
        withContext(Dispatchers.Main) {
            navController.popBackStack(DrawerScreens.Home, true)
            navController.navigate(DrawerScreens.Home)
        }
    }
}

@ExperimentalMaterial3Api
@Preview
@Composable
private fun PV() {
    DWScreen("", "", rememberNavController())
}