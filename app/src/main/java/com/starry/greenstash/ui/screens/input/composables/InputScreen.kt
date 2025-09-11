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

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.psoffritti.taptargetcompose.TapTargetCoordinator
import com.psoffritti.taptargetcompose.TapTargetStyle
import com.psoffritti.taptargetcompose.TextDefinition
import com.starry.greenstash.BuildConfig
import com.starry.greenstash.MainActivity
import com.starry.greenstash.R
import com.starry.greenstash.database.goal.GoalPriority
import com.starry.greenstash.ui.common.TipCard
import com.starry.greenstash.ui.navigation.DrawerScreens
import com.starry.greenstash.ui.screens.input.InputViewModel
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.utils.ImageUtils
import com.starry.greenstash.utils.NumberUtils
import com.starry.greenstash.utils.displayName
import com.starry.greenstash.utils.getActivity
import com.starry.greenstash.utils.hasNotificationPermission
import com.starry.greenstash.utils.toToast
import com.starry.greenstash.utils.validateAmount
import com.starry.greenstash.utils.weakHapticFeedback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(editGoalId: String?, navController: NavController) {
    val view = LocalView.current
    val context = LocalContext.current

    val viewModel: InputViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    var goalImage: Any? by remember { mutableStateOf(R.drawable.default_goal_image) }
    var goalIcon by remember { mutableStateOf(Icons.Filled.Image) }

    val selectedDate = remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    val calenderState = rememberUseCaseState(visible = false, true)

    val showGoalAddedAnim = remember { mutableStateOf(false) }
    val showRemoveDeadlineDialog = remember { mutableStateOf(false) }

    val topBarText: String
    val buttonText: String

    if (editGoalId != null) {
        LaunchedEffect(key1 = true, block = {
            viewModel.setEditGoalData(
                goalId = editGoalId.toLong(),
                onEditDataSet = { goalImageBm, goalIconId ->
                    goalImageBm?.let { goalImage = it }
                    goalIconId?.let { id ->
                        goalIcon = ImageUtils.createIconVector(id) ?: Icons.Filled.Image
                    }
                })
        })
        topBarText = stringResource(id = R.string.input_edit_goal_header)
        buttonText = stringResource(id = R.string.input_edit_goal_button)
    } else {
        topBarText = stringResource(id = R.string.input_screen_header)
        buttonText = stringResource(id = R.string.input_add_goal_button)
    }

    // Goal Image Picker.
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) {
        if (it != null) {
            goalImage = it
            viewModel.state = viewModel.state.copy(goalImageUri = it)
        }
    }

    // Deadline Calendar Dialog.
    CalendarDialog(
        state = calenderState,
        config = CalendarConfig(
            yearSelection = true,
            monthSelection = true,
            style = CalendarStyle.MONTH,
            boundary = LocalDate.now()..LocalDate.now().plusYears(100)
        ),
        selection = CalendarSelection.Date(
            selectedDate = selectedDate.value
        ) { newDate ->
            selectedDate.value = newDate
            viewModel.state = viewModel.state.copy(
                deadline = selectedDate.value!!.format(DateTimeFormatter.ofPattern(viewModel.getDateStyleValue()))
            )
        },
    )

    // Icon Picker Dialog.
    val showIconPickerDialog = remember { mutableStateOf(false) }
    IconPickerDialog(
        viewModel = viewModel,
        showDialog = showIconPickerDialog,
        onIconSelected = { icon ->
            icon?.let {
                goalIcon = it.image ?: Icons.Filled.Image
                viewModel.updateSelectedIcon(it)
            }
        })

    // Remove Deadline Dialog.
    if (showRemoveDeadlineDialog.value) {
        AlertDialog(onDismissRequest = {
            showRemoveDeadlineDialog.value = false
        }, title = {
            Text(
                text = stringResource(id = R.string.input_goal_remove_deadline),
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = greenstashFont,
                fontSize = 18.sp
            )
        }, confirmButton = {
            FilledTonalButton(
                onClick = {
                    showRemoveDeadlineDialog.value = false
                    viewModel.removeDeadLine()
                }, colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(stringResource(id = R.string.confirm), fontFamily = greenstashFont)
            }
        }, dismissButton = {
            TextButton(onClick = {
                showRemoveDeadlineDialog.value = false
            }) {
                Text(stringResource(id = R.string.cancel), fontFamily = greenstashFont)
            }
        })
    }

    // Input Screen UI. ================================================

    val showTapTargets = remember { mutableStateOf(false) }
    LaunchedEffect(key1 = viewModel.showOnboardingTapTargets.value) {
        delay(300) // Delay to prevent flickering
        showTapTargets.value = viewModel.showOnboardingTapTargets.value
    }

    TapTargetCoordinator(
        showTapTargets = showTapTargets.value,
        onComplete = { viewModel.onboardingTapTargetsShown() },
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            snackbarHost = { SnackbarHost(snackBarHostState) },
            topBar = {
                TopAppBar(modifier = Modifier.fillMaxWidth(), title = {
                    Text(
                        text = topBarText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = greenstashFont
                    )
                }, navigationIcon = {
                    IconButton(onClick = {
                        view.weakHapticFeedback()
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                })
            }) { paddingValues ->
            if (showGoalAddedAnim.value) {
                GoalAddedOREditedAnimation(editGoalId = editGoalId)
            } else {
                // Scroll to top when screen is loaded.
                val scrollState = rememberScrollState()
                LaunchedEffect(key1 = true) {
                    scrollState.scrollTo(scrollState.maxValue)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues)
                        .verticalScroll(scrollState, reverseScrolling = true),
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    GoalImagePicker(
                        goalImage = goalImage, photoPicker = photoPicker,
                        fabModifier = Modifier.tapTarget(
                            precedence = 0,
                            title = TextDefinition(
                                text = stringResource(id = R.string.input_pick_image_onboarding_title),
                                textStyle = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                fontFamily = greenstashFont,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            description = TextDefinition(
                                text = stringResource(id = R.string.input_pick_image_onboarding_desc),
                                textStyle = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontFamily = greenstashFont
                            ),
                            tapTargetStyle = TapTargetStyle(
                                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                tapTargetHighlightColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                backgroundAlpha = 1f,
                            ),
                        ),
                    )

                    InputQuoteText() // New Goal Quote Text.

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconPickerCard(
                            goalIcon = goalIcon,
                            onClick = { showIconPickerDialog.value = true },
                            modifier = Modifier.tapTarget(
                                precedence = 1,
                                title = TextDefinition(
                                    text = stringResource(id = R.string.input_pick_icon_onboarding_title),
                                    textStyle = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = greenstashFont,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                description = TextDefinition(
                                    text = stringResource(id = R.string.input_pick_icon_onboarding_desc),
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontFamily = greenstashFont
                                ),
                                tapTargetStyle = TapTargetStyle(
                                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                    tapTargetHighlightColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    backgroundAlpha = 1f,
                                ),
                            ),
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        GoalPriorityMenu(
                            selectedPriority = viewModel.state.priority,
                            onPriorityChanged = { newValue ->
                                viewModel.updatePriority(newValue)
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        GoalReminderMenu(
                            reminderState = viewModel.state.reminder,
                            onReminderChanged = { newValue ->
                                viewModel.updateReminder(newValue)
                            },
                            snackBarHostState = snackBarHostState,
                            coroutineScope = coroutineScope,
                            modifier = Modifier.tapTarget(
                                precedence = 2,
                                title = TextDefinition(
                                    text = stringResource(id = R.string.input_goal_reminders_onboarding_title),
                                    textStyle = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = greenstashFont,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                description = TextDefinition(
                                    text = stringResource(id = R.string.input_goal_reminders_onboarding_desc),
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontFamily = greenstashFont
                                ),
                                tapTargetStyle = TapTargetStyle(
                                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                    tapTargetHighlightColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    backgroundAlpha = 1f,
                                ),
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Show onboarding tip for removing deadline.
                        val showRemoveDeadlineTip = remember { mutableStateOf(false) }
                        LaunchedEffect(key1 = viewModel.state.deadline) {
                            if (editGoalId != null && viewModel.shouldShowRemoveDeadlineTip()) {
                                delay(600) // Don't show immediately.
                                showRemoveDeadlineTip.value = true
                            }
                        }

                        TipCard(
                            modifier = Modifier.fillMaxWidth(0.86f),
                            description = stringResource(id = R.string.input_remove_deadline_tip),
                            showTipCard = showRemoveDeadlineTip.value,
                            onDismissRequest = {
                                showRemoveDeadlineTip.value = false
                                viewModel.removeDeadlineTipShown()
                            })


                        InputTextFields(
                            goalTitle = viewModel.state.goalTitleText,
                            targetAmount = viewModel.state.targetAmount,
                            deadline = viewModel.state.deadline,
                            additionalNotes = viewModel.state.additionalNotes,
                            onTitleChange = { newText -> viewModel.updateTitle(newText) },
                            onAmountChange = { newText -> viewModel.updateTargetAmount(newText) },
                            onDeadlineChange = { newText -> viewModel.updateDeadline(newText) },
                            onNotesChange = { newText -> viewModel.updateAdditionalNotes(newText) },
                            calenderState = calenderState,
                            showRemoveDeadlineDialog = showRemoveDeadlineDialog
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                if (viewModel.state.goalTitleText.isEmpty() || viewModel.state.goalTitleText.isBlank()) {
                                    context.getString(R.string.title_empty_err).toToast(context)
                                } else if (!viewModel.state.targetAmount.validateAmount()) {
                                    context.getString(R.string.amount_empty_err).toToast(context)
                                } else {
                                    if (editGoalId != null) {
                                        viewModel.editSavingGoal(editGoalId.toLong(), context)
                                    } else {
                                        viewModel.addSavingGoal(context)
                                    }

                                    coroutineScope.launch {
                                        showGoalAddedAnim.value = true
                                        delay(1050)
                                        withContext(Dispatchers.Main) {
                                            navController.popBackStack(
                                                DrawerScreens.Home,
                                                true
                                            )
                                            navController.navigate(DrawerScreens.Home)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.86f)
                                .height(45.dp),
                            shape = RoundedCornerShape(14.dp),
                        ) {
                            Text(
                                text = buttonText,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontFamily = greenstashFont
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalImagePicker(
    goalImage: Any?, photoPicker: ActivityResultLauncher<PickVisualMediaRequest>,
    // To be used for onboarding tap target.
    @SuppressLint("ModifierParameter") fabModifier: Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .height(190.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(goalImage).crossfade(enable = true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        ExtendedFloatingActionButton(
            modifier = fabModifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp),
            onClick = {
                view.weakHapticFeedback()
                photoPicker.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
            elevation = FloatingActionButtonDefaults.elevation(4.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Row {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_image),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.input_pick_image_fab),
                    modifier = Modifier.padding(top = 2.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontFamily = greenstashFont
                )
            }
        }
    }
}

@Composable
private fun InputQuoteText() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 20.dp, start = 30.dp, end = 30.dp),
        text = stringResource(id = R.string.input_page_quote),
        textAlign = TextAlign.Center,
        fontSize = 13.5f.sp,
        fontFamily = greenstashFont
    )
}

@Composable
private fun IconPickerCard(
    goalIcon: ImageVector,
    onClick: () -> Unit,
    // To be used for onboarding tap target.
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    Card(
        onClick = {
            view.weakHapticFeedback()
            onClick()
        }, modifier = Modifier.fillMaxWidth(0.86f), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ), shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 12.dp, bottom = 12.dp, start = 22.dp, end = 20.dp
                )
        ) {
            Box(
                modifier = modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface, shape = CircleShape
                    )
                    .padding(8.dp), contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = goalIcon,
                    contentDescription = stringResource(id = R.string.input_pick_icon),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = R.string.input_pick_icon),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 16.sp,
                maxLines = 2,
                fontFamily = greenstashFont,
                overflow = TextOverflow.Ellipsis

            )
        }
    }
}


@Composable
private fun GoalPriorityMenu(selectedPriority: String, onPriorityChanged: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(0.86f), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ), shape = RoundedCornerShape(14.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier.padding(
                    top = 8.dp, bottom = 4.dp, start = 24.dp, end = 24.dp
                ),
                text = stringResource(id = R.string.input_goal_priority),
                fontFamily = greenstashFont,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 8.dp)
                ) {
                    SegmentedButton(
                        selected = selectedPriority == GoalPriority.High.name,
                        onClick = { onPriorityChanged(GoalPriority.High.name) },
                        shape = RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp),
                        label = {
                            Text(
                                text = GoalPriority.High.displayName(), fontFamily = greenstashFont
                            )
                        },
                        icon = {
                            if (selectedPriority == GoalPriority.High.name) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        colors = SegmentedButtonDefaults.colors(
                            activeContentColor = MaterialTheme.colorScheme.onPrimary,
                            activeContainerColor = MaterialTheme.colorScheme.primary,
                            inactiveContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            inactiveContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )

                    SegmentedButton(
                        selected = selectedPriority == GoalPriority.Normal.name,
                        onClick = { onPriorityChanged(GoalPriority.Normal.name) },
                        shape = RectangleShape,
                        label = {
                            Text(
                                text = GoalPriority.Normal.displayName(), fontFamily = greenstashFont
                            )
                        },
                        icon = {
                            if (selectedPriority == GoalPriority.Normal.name) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        colors = SegmentedButtonDefaults.colors(
                            activeContentColor = MaterialTheme.colorScheme.onPrimary,
                            activeContainerColor = MaterialTheme.colorScheme.primary,
                            inactiveContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            inactiveContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )

                    SegmentedButton(
                        selected = selectedPriority == GoalPriority.Low.name,
                        onClick = { onPriorityChanged(GoalPriority.Low.name) },
                        shape = RoundedCornerShape(topEnd = 14.dp, bottomEnd = 14.dp),
                        label = {
                            Text(
                                text = GoalPriority.Low.displayName(), fontFamily = greenstashFont
                            )
                        },
                        icon = {
                            if (selectedPriority == GoalPriority.Low.name) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        colors = SegmentedButtonDefaults.colors(
                            activeContentColor = MaterialTheme.colorScheme.onPrimary,
                            activeContainerColor = MaterialTheme.colorScheme.primary,
                            inactiveContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            inactiveContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }
    }
}


@Composable
private fun GoalReminderMenu(
    reminderState: Boolean,
    onReminderChanged: (Boolean) -> Unit,
    snackBarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    // To be used for onboarding tap target.
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    val context = LocalContext.current
    var hasNotificationPermission by remember { mutableStateOf(context.hasNotificationPermission()) }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                hasNotificationPermission = isGranted
                if (!isGranted) {
                    onReminderChanged(false)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.shouldShowRequestPermissionRationale(
                            context.getActivity() as MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    ) {
                        coroutineScope.launch {
                            val snackBarResult = snackBarHostState.showSnackbar(
                                message = context.getString(R.string.notification_permission_error),
                                actionLabel = "Open Settings"
                            )
                            if (snackBarResult == SnackbarResult.ActionPerformed) {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                                intent.data = uri
                                context.startActivity(intent, null)
                            }
                        }
                    }
                }
            })

    Card(
        modifier = Modifier.fillMaxWidth(0.86f), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ), shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.input_goal_reminder),
                fontSize = 16.sp,
                fontFamily = greenstashFont
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = reminderState, onCheckedChange = { newValue ->
                view.weakHapticFeedback()
                onReminderChanged(newValue)
                // Ask for notification permission if android ver > 13.
                if (newValue && !hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }, thumbContent = if (reminderState) {
                {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                }
            } else {
                null
            }, modifier = modifier)
        }
    }
}

@Composable
private fun InputTextFields(
    goalTitle: String,
    targetAmount: String,
    deadline: String,
    additionalNotes: String,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onDeadlineChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    calenderState: UseCaseState,
    showRemoveDeadlineDialog: MutableState<Boolean>
) {
    val haptic = LocalHapticFeedback.current
    val textFieldSpacing = 8.dp
    val containerColorAlpha = 0.25f

    OutlinedTextField(
        value = goalTitle,
        onValueChange = { newText -> onTitleChange(newText) },
        modifier = Modifier.fillMaxWidth(0.86f),
        label = {
            Text(
                text = stringResource(id = R.string.input_text_title), fontFamily = greenstashFont
            )
        },
        leadingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_title),
                contentDescription = null
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = containerColorAlpha),
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = containerColorAlpha),
        ),
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    )

    Spacer(modifier = Modifier.height(textFieldSpacing))

    OutlinedTextField(
        value = targetAmount,
        onValueChange = { newText -> onAmountChange(NumberUtils.getValidatedNumber(newText)) },
        modifier = Modifier.fillMaxWidth(0.86f),
        label = {
            Text(
                text = stringResource(id = R.string.input_text_amount), fontFamily = greenstashFont
            )
        },
        leadingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_amount),
                contentDescription = null
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = containerColorAlpha),
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = containerColorAlpha),
        ),
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )

    Spacer(modifier = Modifier.height(textFieldSpacing))

    val interactionSource = remember { MutableInteractionSource() }

    OutlinedTextField(
        value = deadline,
        onValueChange = { newText -> onDeadlineChange(newText) },
        modifier = Modifier
            .fillMaxWidth(0.86f)
            .combinedClickable(
                onClick = { calenderState.show() }, onLongClick = {
                    haptic.performHapticFeedback(
                        HapticFeedbackType.LongPress
                    )
                    if (deadline.isNotEmpty()) {
                        showRemoveDeadlineDialog.value = true
                    }
                }, interactionSource = interactionSource, indication = null
            ),
        label = {
            Text(
                text = stringResource(id = R.string.input_deadline), fontFamily = greenstashFont
            )
        },
        leadingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_deadline),
                contentDescription = null
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.onBackground,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = containerColorAlpha),
            //For Icons
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        shape = RoundedCornerShape(14.dp),
        enabled = false,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    )

    Spacer(modifier = Modifier.height(textFieldSpacing))

    OutlinedTextField(
        value = additionalNotes,
        onValueChange = { newText -> onNotesChange(newText) },
        modifier = Modifier.fillMaxWidth(0.86f),
        label = {
            Text(
                text = stringResource(id = R.string.input_additional_notes),
                fontFamily = greenstashFont
            )
        },
        leadingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_additional_notes),
                contentDescription = null
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = containerColorAlpha),
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = containerColorAlpha),
        ),
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    )
}

@Composable
private fun GoalAddedOREditedAnimation(editGoalId: String?) {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val compositionResult: LottieCompositionResult = rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(R.raw.goal_saved_lottie)
        )
        val progressAnimation by animateLottieCompositionAsState(
            compositionResult.value, isPlaying = true, iterations = 1, speed = 1.4f
        )

        Spacer(modifier = Modifier.weight(1f))

        LottieAnimation(
            composition = compositionResult.value,
            progress = { progressAnimation },
            modifier = Modifier.size(320.dp)
        )
        val textStr = if (editGoalId == null) {
            stringResource(id = R.string.input_goal_saved_success)
        } else {
            stringResource(id = R.string.input_goad_edit_success)
        }
        Text(
            text = textStr,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            fontFamily = greenstashFont
        )

        Spacer(modifier = Modifier.weight(1.4f))
    }
}


@Preview(showBackground = true)
@Composable
fun InputScreenPV() {
    InputScreen(editGoalId = "", navController = rememberNavController())
}