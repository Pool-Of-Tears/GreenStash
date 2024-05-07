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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face2
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.starry.greenstash.ui.screens.archive.ArchiveViewModel
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.ui.theme.greenstashNumberFont

@Composable
fun ArchiveScreen(navController: NavController) {
    val viewModel: ArchiveViewModel = hiltViewModel()
    ArchivedGoalItem(
        title = "Home Decorations",
        icon = Icons.Filled.Face2,
        savedAmount = "₹10,000",
        onRestoreClicked = {},
        onDeleteClicked = {}
    )
}

@Composable
fun ArchivedGoalItem(
    title: String,
    icon: ImageVector?,
    savedAmount: String,
    onRestoreClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon ?: Icons.Filled.Image,
                    contentDescription = title,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .clickable { onRestoreClicked() }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .clickable { onDeleteClicked() }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
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

