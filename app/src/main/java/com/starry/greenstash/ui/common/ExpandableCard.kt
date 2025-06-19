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


package com.starry.greenstash.ui.common

import android.content.ClipData
import android.os.Build
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.starry.greenstash.R
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.utils.toToast
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun ExpandableCard(
    title: String,
    titleFontSize: TextUnit = 16.sp,
    titleFontWeight: FontWeight = FontWeight.Bold,
    titleFontFamily: FontFamily = greenstashFont,
    shape: Shape = RoundedCornerShape(8.dp),
    padding: Dp = 12.dp,
    expanded: Boolean = false,
    content: @Composable () -> Unit
) {
    var expandedState by remember { mutableStateOf(expanded) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f, label = "expandable card rotation state"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 12.dp, end = 12.dp, top = 4.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                4.dp
            )
        ),
        shape = shape,
        onClick = {
            expandedState = !expandedState
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(6f)
                        .padding(start = 12.dp),
                    text = title,
                    fontSize = titleFontSize,
                    fontWeight = titleFontWeight,
                    fontFamily = titleFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(
                    modifier = Modifier
                        .weight(1f)
                        .rotate(rotationState),
                    onClick = {
                        expandedState = !expandedState
                    }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }
            if (expandedState) {
                content()
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ExpandableTextCard(
    title: String,
    titleFontSize: TextUnit = 16.sp,
    titleFontWeight: FontWeight = FontWeight.Bold,
    titleFontFamily: FontFamily = greenstashFont,
    description: String,
    descriptionFontSize: TextUnit = 14.sp,
    descriptionFontWeight: FontWeight = FontWeight.Normal,
    descriptionFontFamily: FontFamily = greenstashFont,
    shape: Shape = RoundedCornerShape(8.dp),
    padding: Dp = 12.dp,
    showCopyButton: Boolean = false,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()

    ExpandableCard(
        title = title,
        titleFontSize = titleFontSize,
        titleFontWeight = titleFontWeight,
        titleFontFamily = titleFontFamily,
        shape = shape,
        padding = padding
    ) {
        Text(
            text = description,
            fontSize = descriptionFontSize,
            fontWeight = descriptionFontWeight,
            fontFamily = descriptionFontFamily,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp)
        )
        if (showCopyButton) {
            FilledTonalButton(
                onClick = {
                    coroutineScope.launch {
                        val clipData = ClipData.newPlainText("", description)
                        clipboard.setClipEntry(ClipEntry(clipData))
                    }
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                        context.getString(R.string.info_copy_notes_alert).toToast(context)
                    }
                },
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            ) {
                Row {
                    Icon(
                        Icons.Filled.ContentCopy,
                        contentDescription = stringResource(R.string.info_copy_notes_icon_desc),
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                    Text(
                        text = stringResource(id = R.string.info_copy_notes_button),
                        fontFamily = greenstashFont,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
