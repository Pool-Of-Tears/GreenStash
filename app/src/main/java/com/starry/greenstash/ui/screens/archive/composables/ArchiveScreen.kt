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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Face2
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.starry.greenstash.R
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.ui.screens.archive.ArchiveViewModel
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.ui.theme.greenstashNumberFont
import com.starry.greenstash.utils.Constants
import com.starry.greenstash.utils.ImageUtils
import com.starry.greenstash.utils.NumberUtils
import com.starry.greenstash.utils.weakHapticFeedback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ArchiveScreen(navController: NavController) {
    val view = LocalView.current
    val context = LocalContext.current
    val viewModel: ArchiveViewModel = hiltViewModel()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val archivedGoals by viewModel.archivedGoals.collectAsState(initial = listOf())

    Scaffold(modifier = Modifier
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
                                modifier = Modifier.animateItemPlacement(),
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

@Composable
private fun ArchivedGoalItem(
    title: String,
    icon: ImageVector?,
    savedAmount: String,
    onRestoreClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    val view = LocalView.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon ?: Icons.Filled.Image,
                    contentDescription = title,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .clickable {
                            view.weakHapticFeedback()
                            onRestoreClicked()
                        }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(9.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .clickable {
                            view.weakHapticFeedback()
                            onDeleteClicked()
                        }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(9.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontFamily = greenstashFont,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = savedAmount,
                fontSize = 22.sp,
                fontFamily = greenstashNumberFont,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun ArchiveDialogs(
    showRestoreDialog: MutableState<Boolean>,
    showDeleteDialog: MutableState<Boolean>,
    onRestoreConfirmed: () -> Unit,
    onDeleteConfirmed: () -> Unit
) {
    if (showRestoreDialog.value) {
        AlertDialog(onDismissRequest = {
            showRestoreDialog.value = false
        }, title = {
            Text(
                text = stringResource(id = R.string.goal_restore_confirmation),
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = greenstashFont,
                fontSize = 18.sp
            )
        }, confirmButton = {
            FilledTonalButton(
                onClick = {
                    showRestoreDialog.value = false
                    onRestoreConfirmed()
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(stringResource(id = R.string.confirm), fontFamily = greenstashFont)
            }
        }, dismissButton = {
            TextButton(onClick = {
                showRestoreDialog.value = false
            }) {
                Text(stringResource(id = R.string.cancel), fontFamily = greenstashFont)
            }
        },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null
                )
            }
        )
    }

    if (showDeleteDialog.value) {
        AlertDialog(onDismissRequest = {
            showDeleteDialog.value = false
        }, title = {
            Text(
                text = stringResource(id = R.string.goal_delete_confirmation),
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = greenstashFont,
                fontSize = 18.sp
            )
        }, confirmButton = {
            FilledTonalButton(
                onClick = {
                    showDeleteDialog.value = false
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
                showDeleteDialog.value = false
            }) {
                Text(stringResource(id = R.string.cancel), fontFamily = greenstashFont)
            }
        },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null
                )
            }
        )
    }
}


@Composable
private fun NoArchivedGoals() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val compositionResult: LottieCompositionResult =
            rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(R.raw.no_goal_found_lottie)
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
            progress = { progressAnimation },
            modifier = Modifier.size(320.dp),
            enableMergePaths = true
        )

        Text(
            text = stringResource(id = R.string.archive_empty),
            fontFamily = greenstashFont,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp)
                .offset(y = (-16).dp)
        )

        Spacer(modifier = Modifier.weight(2f))
    }
}

@Preview
@Composable
fun ArchiveItemPV() {
    ArchivedGoalItem(
        title = "Home Decorations",
        icon = Icons.Filled.Face2,
        savedAmount = "₹10,000",
        onRestoreClicked = {},
        onDeleteClicked = {}
    )
}

