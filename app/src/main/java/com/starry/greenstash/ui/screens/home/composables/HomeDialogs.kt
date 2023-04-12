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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.starry.greenstash.R
import com.starry.greenstash.utils.Utils

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
