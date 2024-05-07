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

import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.starry.greenstash.R
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.ui.theme.greenstashNumberFont
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun GoalItemClassic(
    title: String,
    primaryText: String,
    secondaryText: String,
    goalProgress: Float,
    goalImage: Bitmap?,
    isGoalCompleted: Boolean,
    onDepositClicked: () -> Unit,
    onWithdrawClicked: () -> Unit,
    onInfoClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onArchivedClicked: () -> Unit
) {
    val progress by animateFloatAsState(targetValue = goalProgress, label = "goal progress")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                5.dp
            )
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column {
            AsyncImage(
                modifier = Modifier
                    .height(210.dp)
                    .fillMaxWidth(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(goalImage ?: R.drawable.default_goal_image)
                    .crossfade(true).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(40.dp)),
            )

            /** Title, Primary & Secondary text */
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = title,
                    modifier = Modifier.padding(start = 8.dp),
                    fontWeight = FontWeight.Medium,
                    lineHeight = 1.2f.em,
                    fontSize = 18.sp,
                    fontFamily = greenstashFont,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = primaryText,
                    modifier = Modifier.padding(start = 8.dp, top = 6.dp),
                    lineHeight = 1.2f.em,
                    fontSize = 14.sp,
                    fontFamily = greenstashFont,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = secondaryText,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                    lineHeight = 1.1f.em,
                    fontSize = 14.sp,
                    fontFamily = greenstashFont,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            /** Goal Buttons */
            Row(modifier = Modifier.padding(3.dp)) {
                if (isGoalCompleted) {
                    TextButton(
                        onClick = { onArchivedClicked() },
                        modifier = Modifier.padding(end = 2.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.archive_button).uppercase(),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = greenstashFont
                        )
                    }
                } else {
                    TextButton(
                        onClick = { onDepositClicked() },
                        modifier = Modifier.padding(end = 2.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.deposit_button).uppercase(),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = greenstashFont
                        )
                    }
                }
                TextButton(
                    onClick = { onWithdrawClicked() },
                ) {
                    Text(
                        text = stringResource(id = R.string.withdraw_button).uppercase(),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = greenstashFont
                    )
                }

                Spacer(
                    modifier = Modifier
                        .height(1.dp)
                        .weight(1f)
                )

                IconButton(onClick = { onInfoClicked() }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_goal_info),
                        contentDescription = stringResource(id = R.string.info_button_description),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = { onEditClicked() }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_goal_edit),
                        contentDescription = stringResource(id = R.string.edit_button_description),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = { onDeleteClicked() }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_goal_delete),
                        contentDescription = stringResource(id = R.string.delete_button_description),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalItemCompact(
    title: String,
    savedAmount: String,
    daysLeftText: String,
    goalProgress: Float,
    goalIcon: ImageVector,
    isGoalCompleted: Boolean,
    onDepositClicked: () -> Unit,
    onWithdrawClicked: () -> Unit,
    onInfoClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onArchivedClicked: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { direction ->
            when (direction) {
                SwipeToDismissBoxValue.EndToStart -> {
                    coroutineScope.launch {
                        delay(180) // allow the swipe to settle.
                        withContext(Dispatchers.Main) { onEditClicked() }
                    }
                }

                SwipeToDismissBoxValue.StartToEnd -> {
                    coroutineScope.launch {
                        delay(180) // allow the swipe to settle.
                        withContext(Dispatchers.Main) { onDeleteClicked() }
                    }
                }

                SwipeToDismissBoxValue.Settled -> {}
            }
            false // Don't allow it to settle on dismissed state.
        }
    )

    val context = LocalContext.current
    val dismissDirection = swipeState.dismissDirection
    val shape = RoundedCornerShape(18.dp)
    val progress by animateFloatAsState(targetValue = goalProgress, label = "progress")

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
            val iconDescription by remember(dismissDirection) {
                derivedStateOf {
                    when (dismissDirection) {
                        SwipeToDismissBoxValue.EndToStart -> context.getString(R.string.edit_button_description)
                        SwipeToDismissBoxValue.StartToEnd -> context.getString(R.string.delete_button_description)
                        // Placeholder string, not used anywhere.
                        SwipeToDismissBoxValue.Settled -> context.getString(R.string.info_button_description)
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
                    contentDescription = iconDescription,
                    modifier = Modifier.scale(scale)
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(shape = shape),
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        content = {
            Card(
                onClick = onInfoClicked,
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = shape
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = goalIcon,
                            contentDescription = null,
                            modifier = Modifier.size(200.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .clip(RoundedCornerShape(50.dp)),
                        )

                        Row(modifier = Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Column {
                                Spacer(modifier = Modifier.height(40.dp))
                                Icon(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .padding(start = 6.dp),
                                    imageVector = goalIcon,
                                    contentDescription = title,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }

                            Row {
                                if (isGoalCompleted) {
                                    IconButton(
                                        onClick = { onArchivedClicked() },
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .offset((10).dp)
                                            .size(54.dp)
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(20.dp),
                                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_compact_goal_archve),
                                            contentDescription = stringResource(id = R.string.archive_button),
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = { onDepositClicked() },
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .offset((10).dp)
                                            .size(54.dp)
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(20.dp),
                                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_compact_goal_deposit),
                                            contentDescription = stringResource(id = R.string.deposit_button),
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { onWithdrawClicked() }, modifier = Modifier
                                        .padding(top = 4.dp)
                                        .offset((-2).dp)
                                        .size(54.dp)
                                ) {
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_compact_goal_withdraw),
                                        contentDescription = stringResource(id = R.string.withdraw_button),
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }

                        }
                        Text(
                            text = title,
                            modifier = Modifier.padding(start = 4.dp, top = 10.dp),
                            fontWeight = FontWeight.Normal,
                            fontFamily = greenstashFont,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = savedAmount,
                                modifier = Modifier.padding(start = 4.dp),
                                fontSize = 24.sp,
                                fontFamily = greenstashNumberFont,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )

                            Text(
                                text = daysLeftText,
                                modifier = Modifier.padding(top = 18.dp),
                                fontSize = 16.sp,
                                fontFamily = greenstashFont,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    )
}

@ExperimentalMaterial3Api
@Composable
@Preview(showBackground = true)
fun GoalItemsPV() {
    Column(modifier = Modifier.padding(10.dp)) {
        GoalItemClassic(
            title = "Home Decorations",
            primaryText = "You're off to a great start!\nCurrently  saved $0.00 out of $1,000.00.",
            secondaryText = "You have until 26/05/2023 (85) days left.\nYou need to save around $58.83/day, $416.67/week, $2,500.00/month.",
            goalProgress = 0.6f,
            goalImage = null,
            isGoalCompleted = false,
            onDepositClicked = { },
            onWithdrawClicked = { },
            onInfoClicked = { },
            onEditClicked = { },
            onDeleteClicked = { },
            onArchivedClicked = { },
        )

        Spacer(modifier = Modifier.height(10.dp))

        GoalItemCompact(
            title = "Home Decorations",
            savedAmount = "$1,000.00",
            daysLeftText = "Goal Achieved! 🎉",
            goalProgress = 0.8f,
            goalIcon = ImageVector.vectorResource(id = R.drawable.ic_nav_rating),
            isGoalCompleted = true,
            onDepositClicked = {},
            onWithdrawClicked = {},
            onInfoClicked = {},
            onEditClicked = {},
            onDeleteClicked = {},
            onArchivedClicked = {},
        )
    }
}