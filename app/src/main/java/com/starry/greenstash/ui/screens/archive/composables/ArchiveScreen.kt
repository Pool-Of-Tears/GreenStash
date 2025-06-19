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


package com.starry.greenstash.ui.screens.archive.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.starry.greenstash.R
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.ui.screens.archive.ArchiveViewModel
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.utils.Constants
import com.starry.greenstash.utils.ImageUtils
import com.starry.greenstash.utils.NumberUtils
import com.starry.greenstash.utils.weakHapticFeedback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScreen(navController: NavController) {
    val view = LocalView.current
    val context = LocalContext.current
    val viewModel: ArchiveViewModel = hiltViewModel()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val archivedGoals by viewModel.archivedGoals.collectAsState(initial = listOf())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.archive_screen_header),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = greenstashFont
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        view.weakHapticFeedback()
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (archivedGoals.isEmpty()) {
                var showNoGoalsAnimation by remember { mutableStateOf(false) }
                LaunchedEffect(key1 = true, block = {
                    delay(200)
                    showNoGoalsAnimation = true
                })
                AnimatedVisibility(
                    visible = showNoGoalsAnimation,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    NoArchivedGoals()
                }
            } else {
                var showArchivedGoals by remember { mutableStateOf(false) }
                LaunchedEffect(key1 = true, block = {
                    delay(400)
                    showArchivedGoals = true
                })
                if (!showArchivedGoals) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                AnimatedVisibility(
                    visible = showArchivedGoals,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(
                            count = archivedGoals.size,
                            key = { index -> archivedGoals[index].goal.goalId }
                        ) { index ->
                            val goalItem = archivedGoals[index]
                            ArchivedLazyItem(
                                modifier = Modifier.animateItem(
                                    fadeInSpec = null,
                                    fadeOutSpec = null
                                ),
                                goalItem = goalItem,
                                defaultCurrency = viewModel.getDefaultCurrency(),
                                onRestoreConfirmed = {
                                    viewModel.restoreGoal(goalItem.goal)
                                    coroutineScope.launch {
                                        snackBarHostState.showSnackbar(
                                            message = context.getString(R.string.goal_restore_success),
                                            actionLabel = context.getString(R.string.ok),
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                },
                                onDeleteConfirmed = {
                                    viewModel.deleteGoal(goalItem.goal)
                                    coroutineScope.launch {
                                        snackBarHostState.showSnackbar(
                                            message = context.getString(R.string.goal_delete_success),
                                            actionLabel = context.getString(R.string.ok),
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}

@Composable
private fun ArchivedLazyItem(
    modifier: Modifier,
    goalItem: GoalWithTransactions,
    defaultCurrency: String,
    onRestoreConfirmed: () -> Unit,
    onDeleteConfirmed: () -> Unit
) {
    Box(modifier = modifier) {
        val goalIcon by remember(goalItem.goal.goalIconId) {
            mutableStateOf(
                ImageUtils.createIconVector(
                    goalItem.goal.goalIconId ?: Constants.DEFAULT_GOAL_ICON_ID
                )!!
            )
        }

        val showRestoreDialog = remember { mutableStateOf(false) }
        val showDeleteDialog = remember { mutableStateOf(false) }

        ArchivedGoalItem(
            title = goalItem.goal.title,
            icon = goalIcon,
            savedAmount = NumberUtils.formatCurrency(
                goalItem.getCurrentlySavedAmount(),
                defaultCurrency
            ),
            onRestoreClicked = {
                showRestoreDialog.value = true
            },
            onDeleteClicked = {
                showDeleteDialog.value = true
            }
        )

        ArchiveDialogs(
            showRestoreDialog = showRestoreDialog,
            showDeleteDialog = showDeleteDialog,
            onRestoreConfirmed = onRestoreConfirmed,
            onDeleteConfirmed = onDeleteConfirmed
        )
    }
}

