package com.starry.greenstash.widget.configuration

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.compose.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.starry.greenstash.R
import com.starry.greenstash.ui.screens.settings.viewmodels.SettingsViewModel
import com.starry.greenstash.ui.screens.settings.viewmodels.ThemeMode
import com.starry.greenstash.ui.theme.GreenStashTheme
import com.starry.greenstash.utils.PreferenceUtils
import com.starry.greenstash.widget.GoalWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
@ExperimentalMaterial3Api
@ExperimentalAnimationApi
class WidgetConfigActivity : AppCompatActivity() {

    private val viewModel: WidgetConfigViewModel by viewModels()
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PreferenceUtils.initialize(this)
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        settingsViewModel.setUpAppTheme()

        setContent {
            GreenStashTheme(settingsViewModel = settingsViewModel) {
                val systemUiController = rememberSystemUiController()
                systemUiController.setNavigationBarColor(
                    color = MaterialTheme.colorScheme.background,
                    darkIcons = settingsViewModel.getCurrentTheme() == ThemeMode.Light
                )

                systemUiController.setStatusBarColor(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                    darkIcons = settingsViewModel.getCurrentTheme() == ThemeMode.Light
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
                    intent.extras?.let {
                        appWidgetId = it.getInt(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID
                        )
                    }

                    if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                        finish()
                    } else {
                        ConfigScreenContent(viewModel, appWidgetId)
                    }
                }
            }
        }
    }

    @Composable
    private fun ConfigScreenContent(viewModel: WidgetConfigViewModel, appWidgetId: Int) {
        Scaffold(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
            topBar = {
                TopAppBar(modifier = Modifier.fillMaxWidth(), title = {
                    Text(
                        text = stringResource(id = R.string.widget_config_screen_header),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }, navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack, contentDescription = null
                        )
                    }
                }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                )
                )
            }
        ) {
            Column(modifier = Modifier.padding(it)) {
                val allGoals = viewModel.allGoals.observeAsState(listOf()).value
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
                                    spec = LottieCompositionSpec.RawRes(R.raw.widget_config_no_goal_lottie)
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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 4.dp)
                    ) {
                        val defCurrency =
                            PreferenceUtils.getString(PreferenceUtils.DEFAULT_CURRENCY, "")

                        items(allGoals.size) { idx ->
                            val item = allGoals[idx]
                            val progressPercent =
                                ((item.getCurrentlySavedAmount() / item.goal.targetAmount) * 100).toInt()

                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                GoalItem(
                                    title = item.goal.title,
                                    description = stringResource(id = R.string.goal_widget_desc).format(
                                        "$defCurrency${item.getCurrentlySavedAmount()}/$defCurrency${item.goal.targetAmount}"
                                    ),
                                    progress = progressPercent.toFloat() / 100
                                ) {
                                    viewModel.setWidgetData(
                                        widgetId = appWidgetId,
                                        goalId = item.goal.goalId,
                                    ) { goalItem ->
                                        // update widget contents for the first time.
                                        GoalWidget().updateWidgetContents(
                                            context = this@WidgetConfigActivity,
                                            appWidgetId = appWidgetId,
                                            goalItem = goalItem
                                        )
                                        // set result and finish the activity.
                                        val resultValue = Intent()
                                        resultValue.putExtra(
                                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                                            appWidgetId
                                        )
                                        setResult(RESULT_OK, resultValue)
                                        finish()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun GoalItem(title: String, description: String, progress: Float, onClick: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(top = 4.dp, bottom = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    3.dp
                )
            ),
            shape = RoundedCornerShape(12.dp),
            onClick = onClick
        ) {
            Row(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(90.dp)
                        .width(90.dp)
                        .padding(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_widget_config_item),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(top = 4.dp, start = 2.dp)
                    )
                }

                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = title,
                        fontStyle = MaterialTheme.typography.headlineMedium.fontStyle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Text(
                        text = description,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        fontStyle = MaterialTheme.typography.bodySmall.fontStyle,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = progress,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(40.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

            }
        }
    }

}