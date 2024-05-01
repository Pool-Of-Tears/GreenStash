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


package com.starry.greenstash.ui.screens.settings.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.starry.greenstash.MainActivity
import com.starry.greenstash.R
import com.starry.greenstash.ui.screens.home.GoalCardStyle
import com.starry.greenstash.ui.screens.home.composables.GoalItemClassic
import com.starry.greenstash.ui.screens.home.composables.GoalItemCompact
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.utils.getActivity
import com.starry.greenstash.utils.weakHapticFeedback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalCardStyle(navController: NavController) {
    val view = LocalView.current
    val context = navController.context

    val settingsVM = (context.getActivity() as MainActivity).settingsViewModel
    val currentStyle = settingsVM.goalCardStyle.observeAsState().value!!
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        LargeTopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text(
                    text = stringResource(id = R.string.goal_card_settings_header),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = greenstashFont,
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
            }, scrollBehavior = scrollBehavior, colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                scrolledContainerColor = MaterialTheme.colorScheme.surface,
            )
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .animateContentSize()
            ) {
                Text(
                    text = "Preview",
                    modifier = Modifier.padding(start = 16.dp, top = 14.dp),
                    fontFamily = greenstashFont,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                AnimatedContent(
                    targetState = currentStyle,
                    label = "GoalStyleAnimation"
                ) { state ->
                    when (state) {
                        GoalCardStyle.Classic -> {
                            GoalItemClassic(
                                title = "Home Decorations",
                                primaryText = "You're off to a great start!\nCurrently  saved $500.00 out of $5,000.00.",
                                secondaryText = "You have until 26/05/2023 (85) days left.\nYou need to save around $58.83/day, $416.67/week, $2,500.00/month.",
                                goalProgress = 0.6f,
                                goalImage = null,
                                onDepositClicked = { },
                                onWithdrawClicked = { },
                                onInfoClicked = { },
                                onEditClicked = { }) {

                            }
                        }

                        GoalCardStyle.Compact -> {
                            GoalItemCompact(
                                title = "Home Decorations",
                                savedAmount = "$1,000.00",
                                daysLeftText = "12 days left",
                                goalProgress = 0.8f,
                                goalIcon = ImageVector.vectorResource(id = R.drawable.ic_nav_backups),
                                onDepositClicked = {},
                                onWithdrawClicked = {},
                                onInfoClicked = {},
                                onEditClicked = {},
                                onDeleteClicked = {}
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                )

            ) {
                val onOptionSelected: (GoalCardStyle) -> Unit = { opt ->
                    settingsVM.setGoalCardStyle(opt)
                }
                val goalStyleToString: (GoalCardStyle) -> String = { opt ->
                    when (opt) {
                        GoalCardStyle.Classic -> context.getString(R.string.goal_card_option1)
                        GoalCardStyle.Compact -> context.getString(R.string.goal_card_option2)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    GoalCardStyle.entries.forEach { option ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (option == currentStyle),
                                    onClick = { onOptionSelected(option) }
                                )
                                .padding(horizontal = 4.dp)
                        ) {
                            RadioButton(
                                selected = (option == currentStyle),
                                onClick = { onOptionSelected(option) }
                            )
                            Text(
                                text = goalStyleToString(option),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(start = 10.dp, top = 12.dp),
                                fontFamily = greenstashFont
                            )
                        }
                    }
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            val showCompactTip = remember { mutableStateOf(false) }
            LaunchedEffect(key1 = currentStyle) {
                if (currentStyle == GoalCardStyle.Compact) {
                    delay(600)
                    showCompactTip.value = true
                } else {
                    delay(700)
                    showCompactTip.value = false
                }
            }
            AnimatedVisibility(
                visible = showCompactTip.value,
                enter = slideInVertically { it / 2 } + expandVertically(expandFrom = Alignment.Top) + fadeIn(
                    initialAlpha = 0.3f
                ),
                exit = slideOutVertically() + shrinkVertically() + fadeOut(),
            ) {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.goal_card_settings_tip),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                        fontFamily = greenstashFont
                    )
                }
            }
        }

    }
}

@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Preview
@Composable
private fun PV() {
    GoalCardStyle(navController = rememberNavController())
}