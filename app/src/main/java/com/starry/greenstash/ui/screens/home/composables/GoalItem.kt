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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

@Composable
fun GoalItem(
    title: String,
    primaryText: String,
    secondaryText: String,
    goalProgress: Float,
    goalImage: Bitmap?,
    onDepositClicked: () -> Unit,
    onWithdrawClicked: () -> Unit,
    onInfoClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = primaryText,
                    modifier = Modifier.padding(start = 8.dp, top = 6.dp),
                    lineHeight = 1.2f.em,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = secondaryText,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                    lineHeight = 1.1f.em,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            /** Goal Buttons */
            Row(modifier = Modifier.padding(3.dp)) {

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


// TODO: Expose parameters & rename this function
@Composable
fun GoalItem_X() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                5.dp
            )
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .clipToBounds()) {
            Row {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.ic_nav_home),
                    contentDescription = null,
                    modifier = Modifier.size(210.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            }

            Icons.AutoMirrored.Filled

            Column(modifier = Modifier.padding(10.dp)) {
                LinearProgressIndicator(
                    progress = { 0.6f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )

                Row {
                    Column {
                        Spacer(modifier = Modifier.height(50.dp))
                        Icon(
                            modifier = Modifier.size(56.dp),
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_nav_home),
                            contentDescription = stringResource(id = R.string.info_button_description),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Home Decorations",
                            modifier = Modifier.padding(start = 4.dp, top = 10.dp),
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "$1000.00",
                                modifier = Modifier.padding(start = 4.dp),
                                fontSize = 26.sp,
                                fontFamily = greenstashNumberFont,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "12 days left",
                                modifier = Modifier.padding(start = 4.dp, top = 12.dp),
                                fontSize = 18.sp,
                                fontFamily = greenstashFont,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun GoalItemPV() {
    /*GoalItem(
        title = "New Genshin Character",
        primaryText = "You're off to a great start!\nCurrently  saved $0.00 out of $1,000.00.",
        secondaryText = "You have until 26/05/2023 (85) days left.\nYou need to save around $58.83/day, $416.67/week, $2,500.00/month.",
        goalProgress = 0.6f,
        goalImage = null,
        onDepositClicked = { },
        onWithdrawClicked = { },
        onInfoClicked = { },
        onEditClicked = { }) {

    }*/

    GoalItem_X()
}