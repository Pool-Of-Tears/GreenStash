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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.starry.greenstash.R
import com.starry.greenstash.ui.navigation.DrawerScreens
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.utils.Utils
import kotlinx.coroutines.launch

@Composable
fun HomeDrawer(drawerState: DrawerState, navController: NavController) {
    val items = listOf(DrawerScreens.Home, DrawerScreens.Backups, DrawerScreens.Settings)
    val selectedItem = remember { mutableStateOf(items[0]) }
    val coroutineScope = rememberCoroutineScope()
    ModalDrawerSheet(
        modifier = Modifier.width(280.dp),
        drawerShape = RoundedCornerShape(4.dp)
    ) {
        Spacer(Modifier.height(14.dp))

        Text(
            text = Utils.getGreeting(),
            modifier = Modifier.padding(start = 16.dp, top = 12.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = greenstashFont,
            color = MaterialTheme.colorScheme.onSurface
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )

        items.forEach { item ->
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = item.iconResId),
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = item.nameResId),
                        fontFamily = greenstashFont
                    )
                },
                selected = item == selectedItem.value,
                onClick = {
                    selectedItem.value = item
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate(item.route)
                    }
                },
                modifier = Modifier
                    .width(280.dp)
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(Modifier.weight(1f))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(id = R.string.drawer_footer_text),
                modifier = Modifier.padding(bottom = 18.dp),
                fontSize = 12.sp,
                fontFamily = greenstashFont,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }

    }
}