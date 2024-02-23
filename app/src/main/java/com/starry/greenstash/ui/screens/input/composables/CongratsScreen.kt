package com.starry.greenstash.ui.screens.input.composables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.starry.greenstash.R
import com.starry.greenstash.ui.navigation.DrawerScreens

@Composable
fun CongratsScreen(navController: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        BackHandler {
            navController.popBackStack(DrawerScreens.Home.route, true)
            navController.navigate(DrawerScreens.Home.route)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val compositionResult: LottieCompositionResult = rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(R.raw.congrats_lottie)
            )
            val progressAnimation by animateLottieCompositionAsState(
                compositionResult.value,
                isPlaying = true,
                iterations = LottieConstants.IterateForever,
                speed = 1f
            )

            LottieAnimation(
                composition = compositionResult.value,
                progress = progressAnimation,
                modifier = Modifier.size(320.dp),
                enableMergePaths = true
            )

            Text(
                text = stringResource(id = R.string.goal_achieved_heading),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(id = R.string.goal_achieved_subtext),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    navController.popBackStack(DrawerScreens.Home.route, true)
                    navController.navigate(DrawerScreens.Home.route)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.width(100.dp)
            ) {
                Text(text = stringResource(id = R.string.goal_achieved_button))
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Preview
@Composable
private fun PV() {
    CongratsScreen(rememberNavController())
}