package com.starry.greenstash.ui.screens.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.starry.greenstash.R
import com.starry.greenstash.ui.navigation.DrawerScreens
import com.starry.greenstash.ui.navigation.Screens
import com.starry.greenstash.ui.screens.home.viewmodels.HomeViewModel
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = hiltViewModel()
    val allGoals = viewModel.allGoals.observeAsState(listOf()).value

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val items = listOf(DrawerScreens.Home, DrawerScreens.Backups, DrawerScreens.Settings)
    val selectedItem = remember { mutableStateOf(items[0]) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = item.icon),
                                contentDescription = null
                            )
                        },
                        label = { Text(item.name) },
                        selected = item == selectedItem.value,
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedItem.value = item
                            navController.navigate(item.route)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(id = R.string.home_screen_header),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* doSomething() */ }) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null
                            )
                        }
                    }
                )
            },

            floatingActionButton = {
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(end = 10.dp, bottom = 12.dp),
                    onClick = { navController.navigate(Screens.InputScreen.route) },
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.new_goal_fab),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                if (allGoals.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val compositionResult: LottieCompositionResult = rememberLottieComposition(
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
                            modifier = Modifier.size(320.dp)
                        )

                        Text(
                            text = stringResource(id = R.string.no_goal_set),
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
                        items(allGoals.size) { idx ->
                            val item = allGoals[idx]
                            GoalItem(
                                title = item.goal.title,
                                primaryText = "Meow primary",
                                secondaryText = "meow secondary",
                                goalProgress = 5f,
                                goalImage = item.goal.goalImage,
                                onDepositClicked = { /*TODO*/ },
                                onWithdrawClicked = { /*TODO*/ },
                                onInfoClicked = { /*TODO*/ },
                                onEditClicked = { /*TODO*/ }) {
                            }
                        }
                    }
                }
            }
        }
    }
}


@ExperimentalMaterial3Api
@Composable
@Preview
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}