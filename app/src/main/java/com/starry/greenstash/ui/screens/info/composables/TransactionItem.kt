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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.starry.greenstash.MainActivity
import com.starry.greenstash.R
import com.starry.greenstash.database.transaction.Transaction
import com.starry.greenstash.database.transaction.TransactionType
import com.starry.greenstash.ui.screens.info.viewmodels.InfoViewModel
import com.starry.greenstash.ui.screens.settings.viewmodels.ThemeMode
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.utils.getActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun TransactionItem(
    transactions: List<Transaction>,
    currencySymbol: String,
    viewModel: InfoViewModel
) {
    val haptic = LocalHapticFeedback.current

    transactions.forEach { transaction ->
        val showEditSheet = remember { mutableStateOf(false) }
        val showDeleteDialog = remember { mutableStateOf(false) }

        val coroutineScope = rememberCoroutineScope()
        val swipeState = rememberSwipeToDismissBoxState(
            confirmValueChange = { direction ->
                when (direction) {
                    SwipeToDismissBoxValue.EndToStart -> {
                        coroutineScope.launch {
                            delay(180) // allow the swipe to settle.
                            withContext(Dispatchers.Main) { showEditSheet.value = true }
                        }
                    }

                    SwipeToDismissBoxValue.StartToEnd -> {
                        coroutineScope.launch {
                            delay(180) // allow the swipe to settle.
                            withContext(Dispatchers.Main) { showDeleteDialog.value = true }
                        }
                    }

                    SwipeToDismissBoxValue.Settled -> {}
                }
                false // Don't allow it to settle on dismissed state.
            }
        )

        val dismissDirection = swipeState.dismissDirection

        SwipeToDismissBox(
            state = swipeState,
            backgroundContent = {
                val color by animateColorAsState(
                    when (dismissDirection) {
                        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.primary
                        SwipeToDismissBoxValue.StartToEnd -> Color.Red.copy(alpha = 0.5f)
                        SwipeToDismissBoxValue.Settled -> Color.Transparent
                    }, label = "color"
                )
                val alignment by remember(dismissDirection) {
                    derivedStateOf {
                        when (dismissDirection) {
                            SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                            SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                            SwipeToDismissBoxValue.Settled -> Alignment.Center
                        }
                    }
                }
                val icon by remember(dismissDirection) {
                    derivedStateOf {
                        when (dismissDirection) {
                            SwipeToDismissBoxValue.EndToStart -> R.drawable.ic_goal_edit
                            SwipeToDismissBoxValue.StartToEnd -> R.drawable.ic_goal_delete
                            // Placeholder icon, not used anywhere.
                            SwipeToDismissBoxValue.Settled -> R.drawable.ic_goal_info
                        }
                    }
                }

                val scale by animateFloatAsState(
                    if (swipeState.dismissDirection != SwipeToDismissBoxValue.Settled) 1f else 0.75f,
                    label = "scale"
                )

                Box(
                    Modifier
                        .fillMaxSize()
                        .background(color)
                        .padding(horizontal = 20.dp),
                    contentAlignment = alignment
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.scale(scale)
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .clip(shape = RoundedCornerShape(8.dp)),
            enableDismissFromStartToEnd = true,
            enableDismissFromEndToStart = true,
            content = {
                TransactionCard(transaction = transaction, currencySymbol = currencySymbol)
            }
        )

        EditTransactionSheet(
            transaction = transaction,
            showEditTransaction = showEditSheet,
            viewModel = viewModel
        )

        if (showDeleteDialog.value) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            AlertDialog(onDismissRequest = {
                showDeleteDialog.value = false
            }, title = {
                Text(
                    text = stringResource(id = R.string.goal_delete_confirmation),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = greenstashFont,
                )
            }, confirmButton = {
                FilledTonalButton(
                    onClick = {
                        showDeleteDialog.value = false
                        viewModel.deleteTransaction(transaction)
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(stringResource(id = R.string.confirm), fontFamily = greenstashFont)
                }
            }, dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog.value = false
                }) {
                    Text(stringResource(id = R.string.cancel), fontFamily = greenstashFont)
                }
            },
                icon = {
                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
                }
            )
        }
    }

}

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun TransactionCard(transaction: Transaction, currencySymbol: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
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
                        fontFamily = greenstashFont,
                        fontSize = 16.sp,
                        color = amountColor
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = transaction.getTransactionDate(),
                        fontWeight = FontWeight.Medium,
                        fontFamily = greenstashFont,
                        fontSize = 16.sp
                    )
                }

                if (transaction.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = transaction.notes,
                        fontWeight = FontWeight.Medium,
                        fontFamily = greenstashFont,
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
