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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.date_time.DateTimeDialog
import com.maxkeppeler.sheets.date_time.models.DateTimeSelection
import com.starry.greenstash.R
import com.starry.greenstash.database.transaction.Transaction
import com.starry.greenstash.database.transaction.TransactionType
import com.starry.greenstash.ui.common.DateTimeCard
import com.starry.greenstash.ui.screens.info.InfoViewModel
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.utils.Utils
import com.starry.greenstash.utils.toToast
import com.starry.greenstash.utils.validateAmount
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.util.TimeZone

@ExperimentalMaterial3Api
@Composable
fun EditTransactionSheet(
    transaction: Transaction,
    showEditTransaction: MutableState<Boolean>,
    viewModel: InfoViewModel,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    val selectedDateTime = remember {
        val instant = Instant.ofEpochMilli(transaction.timeStamp)
        mutableStateOf<LocalDateTime>(
            LocalDateTime.ofInstant(
                instant,
                TimeZone.getDefault().toZoneId()
            )
        )
    }
    val dateTimeDialogState = rememberUseCaseState(visible = false)
    val (selectedTransactionType, onTransactionTypeSelected) = remember {
        mutableStateOf(transaction.type.name)
    }

    DateTimeDialog(
        state = dateTimeDialogState,
        selection = DateTimeSelection.DateTime(
            selectedDate = selectedDateTime.value.toLocalDate(),
            selectedTime = selectedDateTime.value.toLocalTime(),
        ) { newDateTime -> selectedDateTime.value = newDateTime }
    )

    if (showEditTransaction.value) {
        LaunchedEffect(key1 = true) {
            viewModel.setEditAmountAndNotes(transaction)
        }

        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch {
                    sheetState.hide()
                    delay(300)
                    showEditTransaction.value = false
                }
            },
            sheetState = sheetState,
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectableGroup(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedTransactionType == TransactionType.Deposit.name,
                                    onClick = { onTransactionTypeSelected(TransactionType.Deposit.name) },
                                )
                                Text(
                                    text = TransactionType.Deposit.name,
                                    fontFamily = greenstashFont
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedTransactionType == TransactionType.Withdraw.name,
                                    onClick = { onTransactionTypeSelected(TransactionType.Withdraw.name) },
                                )
                                Text(
                                    text = TransactionType.Withdraw.name,
                                    fontFamily = greenstashFont
                                )
                            }
                        }
                    }

                    DateTimeCard(
                        selectedDateTime = selectedDateTime.value,
                        dateTimeStyle = { viewModel.getDateStyle() },
                        onClick = { dateTimeDialogState.show() }
                    )

                    OutlinedTextField(
                        value = viewModel.editGoalState.amount,
                        onValueChange = { newText ->
                            viewModel.editGoalState =
                                viewModel.editGoalState.copy(
                                    amount = Utils.getValidatedNumber(
                                        newText
                                    )
                                )
                        },
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
                        ),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )

                    OutlinedTextField(
                        value = viewModel.editGoalState.notes,
                        onValueChange = { newText ->
                            viewModel.editGoalState = viewModel.editGoalState.copy(notes = newText)
                        },
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
                        ),
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    )

                    Button(
                        onClick = {
                            if (!viewModel.editGoalState.amount.validateAmount()) {
                                context.getString(R.string.amount_empty_err).toToast(context)
                            } else {
                                viewModel.updateTransaction(
                                    transaction = transaction,
                                    transactionTime = selectedDateTime.value,
                                    transactionType = selectedTransactionType,
                                )
                                coroutineScope.launch {
                                    sheetState.hide()
                                    delay(300)
                                    showEditTransaction.value = false
                                }
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            text = stringResource(id = R.string.info_edit_transaction_button),
                            fontFamily = greenstashFont
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                }
            }
        )
    }
}
