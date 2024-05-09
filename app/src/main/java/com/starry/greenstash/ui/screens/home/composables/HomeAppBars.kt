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

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.starry.greenstash.R
import com.starry.greenstash.ui.screens.home.SearchBarState
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.utils.weakHapticFeedback


@Composable
fun HomeAppBar(
    searchBarState: SearchBarState,
    searchTextState: String,
    consumeBackPress: MutableState<Boolean>,
    onMenuClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    onSearchClicked: () -> Unit,
    onSearchTextChange: (String) -> Unit,
    onSearchCloseClicked: () -> Unit,
    onSearchImeAction: (String) -> Unit,
) {
    Crossfade(
        targetState = searchBarState,
        animationSpec = tween(durationMillis = 300),
        label = "searchbar cross-fade"
    ) {
        when (it) {
            SearchBarState.CLOSED -> {
                DefaultAppBar(
                    onMenuClicked = onMenuClicked,
                    onFilterClicked = onFilterClicked,
                    onSearchClicked = onSearchClicked
                )
                consumeBackPress.value = false
            }

            SearchBarState.OPENED -> {
                SearchAppBar(
                    text = searchTextState,
                    onTextChange = onSearchTextChange,
                    onCloseClicked = onSearchCloseClicked,
                    onSearchClicked = onSearchImeAction
                )
                // Consume the system back button press when the search bar is open
                // So we can close the search bar instead of navigating back.
                consumeBackPress.value = true
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultAppBar(
    onMenuClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    onSearchClicked: () -> Unit,
) {
    val view = LocalView.current
    TopAppBar(
        title = {
            Text(
                stringResource(id = R.string.home_screen_header),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = greenstashFont
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                view.weakHapticFeedback()
                onMenuClicked()
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(id = R.string.menu_button_desc)
                )
            }
        },
        actions = {
            IconButton(onClick = {
                view.weakHapticFeedback()
                onFilterClicked()
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_menu_filter),
                    contentDescription = stringResource(id = R.string.filter_button_desc),
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(onClick = {
                view.weakHapticFeedback()
                onSearchClicked()
            }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(id = R.string.search_button_desc)
                )
            }
        },
    )
}


@Composable
private fun SearchAppBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(insets = WindowInsets.statusBars)
            .focusRequester(focusRequester),
        color = MaterialTheme.colorScheme.surface
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            value = text,
            onValueChange = { onTextChange(it) },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.home_search_label),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = greenstashFont
                )
            },
            singleLine = true,
            leadingIcon = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = {
                    if (text.isNotEmpty()) {
                        onTextChange("")
                    } else {
                        onCloseClicked()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = {
                onSearchClicked(text)
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                cursorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            shape = RoundedCornerShape(24.dp)
        )

        // Request focus on the search bar when it is opened
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }
}