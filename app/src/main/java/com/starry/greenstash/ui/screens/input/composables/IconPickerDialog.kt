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


package com.starry.greenstash.ui.screens.input.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.starry.greenstash.R
import com.starry.greenstash.ui.screens.input.IconItem
import com.starry.greenstash.ui.screens.input.IconsState
import com.starry.greenstash.ui.screens.input.InputViewModel
import com.starry.greenstash.ui.theme.greenstashFont
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconPickerDialog(
    viewModel: InputViewModel,
    showDialog: MutableState<Boolean>,
    onIconSelected: (IconItem?) -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.iconState

    LaunchedEffect(key1 = true) {
        viewModel.updateIconSearch(context = context, search = "")
    }

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    if (showDialog.value) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                coroutineScope.launch {
                    sheetState.hide()
                    delay(300)
                    showDialog.value = false
                }
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.99f)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        SearchTextField(
                            viewModel = viewModel,
                            onSearchChanged = { search ->
                                viewModel.updateIconSearch(
                                    context = context,
                                    search = search
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        IconsGirdList(
                            iconState = state,
                            onIconClick = { viewModel.updateCurrentIcon(it) }
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Spacer(modifier = Modifier.weight(1f))

                            // Cancel button
                            TextButton(onClick = {
                                if (viewModel.iconState.value.searchText.isNotEmpty()) {
                                    viewModel.updateIconSearch(context = context, search = "")
                                } else {
                                    coroutineScope.launch {
                                        sheetState.hide()
                                        delay(300)
                                        showDialog.value = false
                                    }
                                }
                            }) {
                                Text(
                                    text = stringResource(id = R.string.cancel),
                                    fontFamily = greenstashFont
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Confirm button
                            Button(onClick = {
                                onIconSelected(state.currentIcon)
                                coroutineScope.launch {
                                    sheetState.hide()
                                    delay(300)
                                    showDialog.value = false
                                }
                            }) {
                                Text(
                                    text = stringResource(id = R.string.confirm),
                                    fontFamily = greenstashFont,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }

}


@Composable
private fun SearchTextField(
    viewModel: InputViewModel,
    onSearchChanged: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(0.9f),
        label = {
            Text(
                text = stringResource(id = R.string.home_search_label),
                fontFamily = greenstashFont
            )
        },
        value = viewModel.iconState.value.searchText,
        onValueChange = { onSearchChanged(it) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
            disabledContainerColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        ),
        trailingIcon = {
            Icon(
                Icons.Filled.Search, stringResource(id = R.string.home_search_label),
            )
        },
        maxLines = 1,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun IconsGirdList(
    iconState: IconsState,
    onIconClick: (IconItem) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(180.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (iconState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val icons = iconState.icons
                LazyColumn(modifier = Modifier.padding(8.dp)) {
                    items(
                        items = icons,
                        itemContent = { iconItems ->
                            IconListRow(
                                icons = iconItems,
                                selectedIcon = iconState.currentIcon,
                                onClick = { onIconClick(it) }
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun IconListRow(
    icons: List<IconItem>,
    selectedIcon: IconItem? = null,
    onClick: (IconItem) -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        icons.forEach {
            IconItem(
                icon = it,
                selected = it == selectedIcon,
                onClick = { onClick(it) }
            )
        }
    }
}

@Composable
private fun IconItem(
    icon: IconItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected)
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
    else
        MaterialTheme.colorScheme.surface

    Column(
        modifier = Modifier
            .width(100.dp)
            .padding(10.dp)
            .clickable { onClick() }
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var text = "Not found"
        var vector = Icons.Filled.Image
        var color = MaterialTheme.colorScheme.error


        if (icon.image != null) {
            text = icon.name
            color = MaterialTheme.colorScheme.primary
            vector = icon.image!!
        }

        Icon(
            vector, icon.name,
            tint = color,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = text,
            fontSize = MaterialTheme.typography.labelSmall.fontSize,
            fontFamily = greenstashFont,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}
