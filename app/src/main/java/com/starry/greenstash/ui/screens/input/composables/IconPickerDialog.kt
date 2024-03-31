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

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.starry.greenstash.R
import com.starry.greenstash.ui.screens.input.viewmodels.IconItem
import com.starry.greenstash.ui.screens.input.viewmodels.IconsState
import com.starry.greenstash.ui.screens.input.viewmodels.InputViewModel
import com.starry.greenstash.ui.theme.greenstashFont
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
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

    if (showDialog.value) {
        Dialog(
            onDismissRequest = {}
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

                        Spacer(modifier = Modifier.height(10.dp))

                        IconsList(
                            iconState = state,
                            onIconClick = { viewModel.updateCurrentIcon(it) }
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Spacer(modifier = Modifier.weight(1f))

                            TextButton(onClick = {
                                if (viewModel.iconState.value.searchText.isNotEmpty()) {
                                    viewModel.updateIconSearch(context = context, search = "")
                                } else {
                                    showDialog.value = false
                                }
                            }) {
                                Text(
                                    text = stringResource(id = R.string.cancel),
                                    fontFamily = greenstashFont
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Button(onClick = {
                                onIconSelected(state.currentIcon)
                                showDialog.value = false
                            }) {
                                Text(
                                    text = stringResource(id = R.string.confirm),
                                    fontFamily = greenstashFont,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}

@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun SearchTextField(
    viewModel: InputViewModel,
    onSearchChanged: (String) -> Unit
) {

    Text(
        modifier = Modifier.padding(12.dp),
        text = stringResource(id = R.string.input_icon_dialog),
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.SemiBold,
        fontFamily = greenstashFont,
        fontSize = 20.sp
    )

    OutlinedTextField(
        modifier = Modifier.height(60.dp),
        label = {
            Text(
                text = stringResource(id = R.string.home_search_label),
                color = MaterialTheme.colorScheme.primary,
                fontFamily = greenstashFont
            )
        },
        value = viewModel.iconState.value.searchText,
        onValueChange = { onSearchChanged(it) },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.primary
        ),
        trailingIcon = {
            Icon(
                Icons.Rounded.Image, "Image",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        maxLines = 1,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun IconsList(
    iconState: IconsState,
    onIconClick: (IconItem) -> Unit
) {
    val icons = iconState.icons
    Column(
        modifier = Modifier.fillMaxHeight(0.4f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (iconState.loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
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
        var vector = Icons.Rounded.Image
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

