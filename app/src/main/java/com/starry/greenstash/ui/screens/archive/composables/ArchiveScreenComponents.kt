package com.starry.greenstash.ui.screens.archive.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face2
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.starry.greenstash.R
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.ui.theme.greenstashNumberFont
import com.starry.greenstash.utils.weakHapticFeedback

@Composable
fun ArchivedGoalItem(
    title: String,
    icon: ImageVector?,
    savedAmount: String,
    onRestoreClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    val view = LocalView.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon ?: Icons.Filled.Image,
                    contentDescription = title,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .clickable {
                            view.weakHapticFeedback()
                            onRestoreClicked()
                        }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(9.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .clickable {
                            view.weakHapticFeedback()
                            onDeleteClicked()
                        }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(9.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontFamily = greenstashFont,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = savedAmount,
                fontSize = 22.sp,
                fontFamily = greenstashNumberFont,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
            )
        }
    }
}

@Composable
fun ArchiveDialogs(
    showRestoreDialog: MutableState<Boolean>,
    showDeleteDialog: MutableState<Boolean>,
    onRestoreConfirmed: () -> Unit,
    onDeleteConfirmed: () -> Unit
) {
    if (showRestoreDialog.value) {
        AlertDialog(onDismissRequest = {
            showRestoreDialog.value = false
        }, title = {
            Text(
                text = stringResource(id = R.string.goal_restore_confirmation),
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = greenstashFont,
                fontSize = 18.sp
            )
        }, confirmButton = {
            FilledTonalButton(
                onClick = {
                    showRestoreDialog.value = false
                    onRestoreConfirmed()
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(stringResource(id = R.string.confirm), fontFamily = greenstashFont)
            }
        }, dismissButton = {
            TextButton(onClick = {
                showRestoreDialog.value = false
            }) {
                Text(stringResource(id = R.string.cancel), fontFamily = greenstashFont)
            }
        },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null
                )
            }
        )
    }

    if (showDeleteDialog.value) {
        AlertDialog(onDismissRequest = {
            showDeleteDialog.value = false
        }, title = {
            Text(
                text = stringResource(id = R.string.goal_delete_confirmation),
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = greenstashFont,
                fontSize = 18.sp
            )
        }, confirmButton = {
            FilledTonalButton(
                onClick = {
                    showDeleteDialog.value = false
                    onDeleteConfirmed()
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(stringResource(id = R.string.confirm), fontFamily = greenstashFont)
            }
        }, dismissButton = {
            TextButton(onClick = {
                showDeleteDialog.value = false
            }) {
                Text(stringResource(id = R.string.cancel), fontFamily = greenstashFont)
            }
        },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null
                )
            }
        )
    }
}


@Composable
fun NoArchivedGoals() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val compositionResult: LottieCompositionResult =
            rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(R.raw.no_goal_found_lottie)
            )
        val progressAnimation by animateLottieCompositionAsState(
            compositionResult.value,
            isPlaying = true,
            iterations = 1,
            speed = 1f
        )

        Spacer(modifier = Modifier.weight(1f))

        LottieAnimation(
            composition = compositionResult.value,
            progress = { progressAnimation },
            modifier = Modifier.size(320.dp),
            enableMergePaths = true
        )

        Text(
            text = stringResource(id = R.string.archive_empty),
            fontFamily = greenstashFont,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp)
                .offset(y = (-16).dp)
        )

        Spacer(modifier = Modifier.weight(2f))
    }
}


@Preview
@Composable
fun ArchiveItemPV() {
    ArchivedGoalItem(
        title = "Home Decorations",
        icon = Icons.Filled.Face2,
        savedAmount = "₹10,000",
        onRestoreClicked = {},
        onDeleteClicked = {}
    )
}