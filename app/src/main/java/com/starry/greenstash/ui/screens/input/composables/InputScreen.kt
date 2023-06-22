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

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarTimeline
import com.starry.greenstash.R
import com.starry.greenstash.database.goal.GoalPriority
import com.starry.greenstash.ui.common.SelectableChipGroup
import com.starry.greenstash.ui.navigation.DrawerScreens
import com.starry.greenstash.ui.screens.input.viewmodels.InputViewModel
import com.starry.greenstash.ui.screens.settings.viewmodels.DateStyle
import com.starry.greenstash.utils.PreferenceUtils
import com.starry.greenstash.utils.Utils
import com.starry.greenstash.utils.toToast
import com.starry.greenstash.utils.validateAmount
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun InputScreen(editGoalId: String?, navController: NavController) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val viewModel: InputViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()

    var imageData: Any? by remember { mutableStateOf(R.drawable.default_goal_image) }
    val calenderState = rememberSheetState()
    val showGoalAddedDialog = remember { mutableStateOf(false) }
    val showRemoveDeadlineDialog = remember { mutableStateOf(false) }

    val topBarText: String
    val buttonText: String

    if (editGoalId != null) {
        LaunchedEffect(key1 = true, block = {
            viewModel.setEditGoalData(goalId = editGoalId.toLong(), onEditDataSet = { goalImageBm ->
                if (goalImageBm != null) {
                    imageData = goalImageBm
                }
            })
        })
        topBarText = stringResource(id = R.string.input_edit_goal_header)
        buttonText = stringResource(id = R.string.input_edit_goal_button)
    } else {
        topBarText = stringResource(id = R.string.input_screen_header)
        buttonText = stringResource(id = R.string.input_add_goal_button)
    }


    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) {
        if (it != null) {
            imageData = it
            viewModel.state = viewModel.state.copy(goalImageUri = it)
        }
    }

    CalendarDialog(
        state = calenderState, selection = CalendarSelection.Date { date ->
            viewModel.state = viewModel.state.copy(
                deadline = date.format(
                    DateTimeFormatter.ofPattern(
                        PreferenceUtils.getString(
                            PreferenceUtils.DATE_FORMAT, DateStyle.DateMonthYear.pattern
                        )
                    )
                )
            )
        }, config = CalendarConfig(
            monthSelection = true, yearSelection = true, disabledTimeline = CalendarTimeline.PAST
        )
    )

    if (showRemoveDeadlineDialog.value) {
        AlertDialog(onDismissRequest = {
            showRemoveDeadlineDialog.value = false
        }, title = {
            Text(
                text = stringResource(id = R.string.goal_remove_deadline),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }, confirmButton = {
            TextButton(onClick = {
                showRemoveDeadlineDialog.value = false
                viewModel.removeDeadLine()
            }) {
                Text(stringResource(id = R.string.dialog_confirm_button))
            }
        }, dismissButton = {
            TextButton(onClick = {
                showRemoveDeadlineDialog.value = false
            }) {
                Text(stringResource(id = R.string.cancel))
            }
        })
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(modifier = Modifier.fillMaxWidth(), title = {
            Text(
                text = topBarText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack, contentDescription = null
                )
            }
        }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
        )
        )
    }) {
        if (showGoalAddedDialog.value) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val compositionResult: LottieCompositionResult = rememberLottieComposition(
                    spec = LottieCompositionSpec.RawRes(R.raw.goal_saved_lottie)
                )
                val progressAnimation by animateLottieCompositionAsState(
                    compositionResult.value,
                    isPlaying = true,
                    iterations = 1,
                    speed = 1.4f
                )

                Spacer(modifier = Modifier.weight(1f))

                LottieAnimation(
                    composition = compositionResult.value,
                    progress = progressAnimation,
                    modifier = Modifier.size(320.dp)
                )
                val textStr = if (editGoalId == null) {
                    stringResource(id = R.string.goal_saved_success)
                } else {
                    stringResource(id = R.string.goad_edit_success)
                }
                Text(
                    text = textStr,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.weight(1.4f))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(it)
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(12.dp))
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
                                .fillMaxWidth(0.8f)
                                .height(190.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context).data(imageData)
                                    .crossfade(enable = true).build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }


                    ExtendedFloatingActionButton(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 24.dp),
                        onClick = {
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
                                text = stringResource(id = R.string.input_pick_image),
                                modifier = Modifier.padding(top = 2.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 20.dp, start = 30.dp, end = 30.dp),
                    text = stringResource(id = R.string.input_page_quote),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ), shape = RoundedCornerShape(14.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp),
                                    text = stringResource(id = R.string.input_goal_priority),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                SelectableChipGroup(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    choices = listOf(
                                        GoalPriority.High.name,
                                        GoalPriority.Normal.name,
                                        GoalPriority.Low.name
                                    ),
                                    selected = viewModel.state.goalPriority,
                                    onSelected = { newValue ->
                                        viewModel.state =
                                            viewModel.state.copy(goalPriority = newValue)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    OutlinedTextField(
                        value = viewModel.state.goalTitleText,
                        onValueChange = { newText ->
                            viewModel.state = viewModel.state.copy(goalTitleText = newText)
                        },
                        modifier = Modifier.fillMaxWidth(0.8f),
                        label = {
                            Text(text = stringResource(id = R.string.input_text_title))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_title),
                                contentDescription = null
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
                        ),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    OutlinedTextField(
                        value = viewModel.state.targetAmount,
                        onValueChange = { newText ->
                            viewModel.state =
                                viewModel.state.copy(targetAmount = Utils.getValidatedNumber(newText))
                        },
                        modifier = Modifier.fillMaxWidth(0.8f),
                        label = {
                            Text(text = stringResource(id = R.string.input_text_amount))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_amount),
                                contentDescription = null
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
                        ),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    val interactionSource = remember { MutableInteractionSource() }

                    OutlinedTextField(
                        value = viewModel.state.deadline,
                        onValueChange = { newText ->
                            viewModel.state = viewModel.state.copy(deadline = newText)
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .combinedClickable(
                                onClick = { calenderState.show() },
                                onLongClick = {
                                    haptic.performHapticFeedback(
                                        HapticFeedbackType.LongPress
                                    )
                                    if (viewModel.state.deadline.isNotEmpty()) {
                                        showRemoveDeadlineDialog.value = true
                                    }
                                },
                                interactionSource = interactionSource,
                                indication = null
                            ),
                        label = {
                            Text(text = stringResource(id = R.string.input_deadline))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_deadline),
                                contentDescription = null
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.onBackground,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            //For Icons
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(14.dp),
                        enabled = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    OutlinedTextField(
                        value = viewModel.state.additionalNotes,
                        onValueChange = { newText ->
                            viewModel.state = viewModel.state.copy(additionalNotes = newText)
                        },
                        modifier = Modifier.fillMaxWidth(0.8f),
                        label = {
                            Text(text = stringResource(id = R.string.input_additional_notes))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_input_additional_notes),
                                contentDescription = null
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
                        ),
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

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
                                    showGoalAddedDialog.value = true
                                    delay(2000)
                                    navController.popBackStack(DrawerScreens.Home.route, true)
                                    navController.navigate(DrawerScreens.Home.route)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(55.dp),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text(
                            text = buttonText,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}


@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Preview
@Composable
fun InputScreenPV() {
    InputScreen(editGoalId = null, rememberNavController())
}