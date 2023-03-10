package com.starry.greenstash.ui.screens.home.composables

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.starry.greenstash.R

@Composable
fun GoalItem(
    title: String,
    primaryText: String,
    secondaryText: String,
    goalProgress: Float,
    goalImage: Bitmap?,
    onDepositClicked: () -> Unit,
    onWithdrawClicked: () -> Unit,
    onInfoClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
) {
    val progress by animateFloatAsState(targetValue = goalProgress)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                3.dp
            )
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column {
            AsyncImage(
                modifier = Modifier
                    .height(210.dp)
                    .fillMaxWidth(),
                model = goalImage ?: R.drawable.default_goal_image,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            LinearProgressIndicator(
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(40.dp)),
                progress = progress
            )

            /** Title, Primary & Secondary text */
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = title,
                    modifier = Modifier.padding(start = 8.dp),
                    fontWeight = FontWeight.Medium,
                    lineHeight = 1.2f.em,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = primaryText,
                    modifier = Modifier.padding(start = 8.dp, top = 6.dp),
                    lineHeight = 1.2f.em,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = secondaryText,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                    lineHeight = 1.1f.em,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            /** Goal Buttons */
            Row(modifier = Modifier.padding(3.dp)) {

                TextButton(
                    onClick = { onDepositClicked() },
                    modifier = Modifier.padding(end = 2.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.deposit_btn).uppercase(),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                TextButton(
                    onClick = { onWithdrawClicked() },
                ) {
                    Text(
                        text = stringResource(id = R.string.withdraw_btn).uppercase(),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(
                    modifier = Modifier
                        .height(1.dp)
                        .weight(1f)
                )

                IconButton(onClick = { onInfoClicked() }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_goal_info),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = { onEditClicked() }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_goal_edit),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = { onDeleteClicked() }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_goal_delete),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}


@Composable
@Preview
fun GoalItemPV() {
    GoalItem(
        title = "New Genshin Character",
        primaryText = "You're off to a great start!\nCurrently  saved $0.00 out of $1,000.00.",
        secondaryText = "You have until 26/05/2023 (85) days left.\nYou need to save around $58.83/day, $416.67/week, $2,500.00/month.",
        goalProgress = 0.6f,
        goalImage = null,
        onDepositClicked = { /*TODO*/ },
        onWithdrawClicked = { /*TODO*/ },
        onInfoClicked = { /*TODO*/ },
        onEditClicked = { /*TODO*/ }) {

    }
}