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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.starry.greenstash.R
import com.starry.greenstash.ui.screens.settings.DateStyle
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.utils.weakHapticFeedback
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DateTimeCard(
    selectedDateTime: LocalDateTime,
    dateTimeStyle: () -> DateStyle,
    onClick: () -> Unit
) {
    val view = LocalView.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .clickable {
                view.weakHapticFeedback()
                onClick()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Row {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_dw_date),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = selectedDateTime.format(
                        DateTimeFormatter.ofPattern(dateTimeStyle().pattern)
                    ),
                    fontFamily = greenstashFont,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            Row {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_dw_time),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = selectedDateTime.format(DateTimeFormatter.ofPattern("h:mm a")),
                    fontFamily = greenstashFont,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun DateTimeCardPreview() {
    DateTimeCard(
        selectedDateTime = LocalDateTime.now(),
        dateTimeStyle = { DateStyle.DateMonthYear },
        onClick = {}
    )
}
