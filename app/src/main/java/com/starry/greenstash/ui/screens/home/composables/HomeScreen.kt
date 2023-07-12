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

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.starry.greenstash.R
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.ui.navigation.DrawerScreens
import com.starry.greenstash.ui.navigation.Screens
import com.starry.greenstash.ui.screens.home.viewmodels.BottomSheetType
import com.starry.greenstash.ui.screens.home.viewmodels.FilterField
import com.starry.greenstash.ui.screens.home.viewmodels.FilterSortType
import com.starry.greenstash.ui.screens.home.viewmodels.HomeViewModel
import com.starry.greenstash.ui.screens.home.viewmodels.SearchWidgetState
import com.starry.greenstash.utils.isScrollingUp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = hiltViewModel()

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val currentBottomSheet = remember { mutableStateOf(BottomSheetType.FILTER_MENU) }

    ModalBottomSheetLayout(sheetState = modalBottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetElevation = 24.dp,
        sheetBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        sheetContent = {
            SheetLayout(
                bottomSheetType = currentBottomSheet.value,
                viewModel = viewModel
            )
        },
        content = {
            HomeScreenContent(
                context = context,
                viewModel = viewModel,
                navController = navController,
                bottomSheetState = modalBottomSheetState,
                bottomSheetType = currentBottomSheet
            )
        })

}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun HomeScreenContent(
    context: Context,
    viewModel: HomeViewModel,
    navController: NavController,
    bottomSheetState: ModalBottomSheetState,
    bottomSheetType: MutableState<BottomSheetType>
) {
    val allGoals = viewModel.goalsList.observeAsState(emptyList()).value
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val items = listOf(DrawerScreens.Home, DrawerScreens.Backups, DrawerScreens.Settings)
    val selectedItem = remember { mutableStateOf(items[0]) }

    val searchWidgetState by viewModel.searchWidgetState
    val searchTextState by viewModel.searchTextState

    val lazyListState = rememberLazyListState()
    val isFabVisible = lazyListState.isScrollingUp()

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet(drawerShape = RoundedCornerShape(4.dp)) {
                Spacer(Modifier.height(14.dp))

                Text(
                    text = stringResource(id = R.string.app_name),
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                )

                Divider(
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = item.iconResId),
                                contentDescription = null
                            )
                        },
                        label = { Text(stringResource(id = item.nameResId)) },
                        selected = item == selectedItem.value,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            selectedItem.value = item
                            navController.navigate(item.route)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }) {
        Scaffold(modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackBarHostState) },
            topBar = {
                MainAppBar(
                    onMenuClicked = { coroutineScope.launch { drawerState.open() } },
                    onFilterClicked = {
                        bottomSheetType.value = BottomSheetType.FILTER_MENU
                        coroutineScope.launch {
                            bottomSheetState.show()
                        }
                    },
                    onSearchClicked = { viewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED) },
                    searchWidgetState = searchWidgetState,
                    searchTextState = searchTextState,
                    onSearchTextChange = { viewModel.updateSearchTextState(newValue = it) },
                    onSearchCloseClicked = { viewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED) },
                    onSearchImeAction = { println("Meow >~< | $it") },
                )
            },

            floatingActionButton = {
                val density = LocalDensity.current
                AnimatedVisibility(
                    visible = isFabVisible,
                    enter = slideInVertically {
                        with(density) { 40.dp.roundToPx() }
                    } + fadeIn(),
                    exit = fadeOut(
                        animationSpec = keyframes {
                            this.durationMillis = 120
                        }
                    )
                ) {
                    ExtendedFloatingActionButton(
                        modifier = Modifier.padding(end = 10.dp, bottom = 12.dp),
                        onClick = { navController.navigate(Screens.InputScreen.route) },
                        elevation = FloatingActionButtonDefaults.elevation(8.dp)
                    ) {
                        Row {
                            Icon(
                                imageVector = Icons.Filled.Add, contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(id = R.string.new_goal_fab),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (allGoals.isEmpty()) {
                    var showNoGoalsAnimation by remember { mutableStateOf(false) }

                    LaunchedEffect(key1 = true, block = {
                        delay(200)
                        showNoGoalsAnimation = true
                    })

                    if (showNoGoalsAnimation) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val compositionResult: LottieCompositionResult =
                                rememberLottieComposition(
                                    spec = LottieCompositionSpec.RawRes(R.raw.no_goal_set_lottie)
                                )
                            val progressAnimation by animateLottieCompositionAsState(
                                compositionResult.value,
                                isPlaying = true,
                                iterations = LottieConstants.IterateForever,
                                speed = 1f
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            LottieAnimation(
                                composition = compositionResult.value,
                                progress = progressAnimation,
                                modifier = Modifier.size(335.dp),
                                enableMergePaths = true
                            )

                            Text(
                                text = stringResource(id = R.string.no_goal_set),
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(start = 12.dp, end = 12.dp)
                            )

                            Spacer(modifier = Modifier.weight(2f))
                        }
                    }

                } else {

                    if (searchTextState.isNotEmpty() && searchTextState.isNotBlank()) {
                        val filteredList: ArrayList<GoalWithTransactions> = ArrayList()
                        for (goalItem in allGoals) {
                            if (goalItem.goal.title.lowercase(Locale.getDefault())
                                    .contains(searchTextState.lowercase(Locale.getDefault()))
                            ) {
                                filteredList.add(goalItem)
                            }
                        }
                        if (allGoals.isNotEmpty() && filteredList.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val compositionResult: LottieCompositionResult =
                                    rememberLottieComposition(
                                        spec = LottieCompositionSpec.RawRes(R.raw.goal_not_found_lottie)
                                    )
                                val progressAnimation by animateLottieCompositionAsState(
                                    compositionResult.value,
                                    isPlaying = true,
                                    iterations = LottieConstants.IterateForever,
                                    speed = 1f
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                LottieAnimation(
                                    composition = compositionResult.value,
                                    progress = progressAnimation,
                                    modifier = Modifier.size(320.dp),
                                    enableMergePaths = true
                                )

                                Text(
                                    text = stringResource(id = R.string.search_goal_not_found),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(start = 12.dp, end = 12.dp)
                                )

                                Spacer(modifier = Modifier.weight(2f))
                            }

                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                items(
                                    filteredList.size,
                                    key = { k -> k },
                                    contentType = { 0 }) { idx ->
                                    val item = filteredList[idx]
                                    GoalLazyColumnItem(
                                        context = context,
                                        viewModel = viewModel,
                                        item = item,
                                        snackBarHostState = snackBarHostState,
                                        bottomSheetState = bottomSheetState,
                                        bottomSheetType = bottomSheetType,
                                        navController = navController
                                    )
                                }
                            }
                        }

                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                            state = lazyListState
                        ) {
                            items(allGoals.size, key = { k -> k }, contentType = { 0 }) { idx ->
                                val item = allGoals[idx]
                                GoalLazyColumnItem(
                                    context = context,
                                    viewModel = viewModel,
                                    item = item,
                                    snackBarHostState = snackBarHostState,
                                    bottomSheetState = bottomSheetState,
                                    bottomSheetType = bottomSheetType,
                                    navController = navController
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun SheetLayout(bottomSheetType: BottomSheetType, viewModel: HomeViewModel) {
    when (bottomSheetType) {
        BottomSheetType.GOAL_ACHIEVED -> GoalAchievedSheet()
        BottomSheetType.FILTER_MENU -> FilterMenuSheet(viewModel)
    }

}

@Composable
fun GoalAchievedSheet() {
    Column(
        modifier = Modifier
            .height(425.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val compositionResult: LottieCompositionResult =
            rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(R.raw.congrats_lottie)
            )
        val progressAnimation by animateLottieCompositionAsState(
            compositionResult.value,
            isPlaying = true,
            iterations = LottieConstants.IterateForever,
            speed = 1f
        )

        Divider(
            modifier = Modifier
                .width(40.dp)
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(65.dp)),
            thickness = 6.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.69f)
        )

        LottieAnimation(
            composition = compositionResult.value,
            progress = progressAnimation,
            modifier = Modifier.size(300.dp),
            enableMergePaths = true
        )

        Text(
            text = stringResource(id = R.string.goal_achieved_card_title),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.goal_achieved_card_desc),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

    }
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun FilterMenuSheet(viewModel: HomeViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text = stringResource(id = R.string.filter_menu_title),
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                FilterField.values().forEach {
                    FilterButton(
                        text = it.name,
                        isSelected = it == viewModel.filterFlowData.value.filterField,
                        onClick = { viewModel.updateFilterField(it) })
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                FilterSortType.values().forEach {
                    FilterButton(
                        text = it.name,
                        isSelected = viewModel.filterFlowData.value.sortType.name == it.name,
                        onClick = { viewModel.updateFilterSort(it) })
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val buttonColor: Color
    val textColor: Color
    if (isSelected) {
        buttonColor = MaterialTheme.colorScheme.primary
        textColor = MaterialTheme.colorScheme.onPrimary
    } else {
        buttonColor = MaterialTheme.colorScheme.secondaryContainer
        textColor = MaterialTheme.colorScheme.onSecondaryContainer
    }

    Card(
        modifier = Modifier
            .height(60.dp)
            .padding(6.dp),
        colors = CardDefaults.cardColors(containerColor = buttonColor),
        shape = RoundedCornerShape(14.dp),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier.padding(2.dp),
                text = text,
                fontSize = 16.sp,
                fontStyle = MaterialTheme.typography.headlineMedium.fontStyle,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textColor,
            )
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
@Preview
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}