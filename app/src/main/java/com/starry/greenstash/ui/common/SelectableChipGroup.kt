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
import androidx.compose.material.icons.rounded.Check
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
                    imageVector = Icons.Rounded.Check,
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





