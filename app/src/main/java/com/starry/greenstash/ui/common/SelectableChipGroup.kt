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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.starry.greenstash.ui.theme.greenstashFont

@Composable
fun SelectableChipGroup(
    modifier: Modifier,
    choices: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Row {
            choices.forEach { it ->
                Chip(
                    title = it,
                    selected = selected,
                    onSelected = { onSelected(it) }
                )
            }
        }
    }

}

@Composable
fun Chip(
    title: String,
    selected: String,
    onSelected: (String) -> Unit
) {
    val isSelected = selected == title
    Box(
        modifier = Modifier
            .padding(end = 10.dp)
            .height(35.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable(
                onClick = {
                    onSelected(title)
                }
            )
    ) {
        Row(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            AnimatedVisibility(visible = isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "check",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp,
                fontFamily = greenstashFont
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectableChipGroupPV() {
    SelectableChipGroup(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        choices = listOf("High", "Normal", "Low"),
        selected = "Normal",
        onSelected = { }
    )
}





