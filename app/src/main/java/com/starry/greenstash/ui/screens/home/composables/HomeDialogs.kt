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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import com.starry.greenstash.R
import com.starry.greenstash.ui.theme.greenstashFont

@ExperimentalMaterial3Api
@Composable
fun HomeDialogs(
    openDeleteDialog: MutableState<Boolean>,
    onDeleteConfirmed: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    if (openDeleteDialog.value) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        AlertDialog(onDismissRequest = {
            openDeleteDialog.value = false
        }, title = {
            Text(
                text = stringResource(id = R.string.goal_delete_confirmation),
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = greenstashFont,
            )
        }, confirmButton = {
            FilledTonalButton(
                onClick = {
                    openDeleteDialog.value = false
                    onDeleteConfirmed()
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
                openDeleteDialog.value = false
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
