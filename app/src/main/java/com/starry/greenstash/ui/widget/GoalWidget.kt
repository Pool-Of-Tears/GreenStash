package com.starry.greenstash.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.unit.ColorProvider
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dagger.hilt.EntryPoints

class GoalWidget : GlanceAppWidget() {
    @Composable
    override fun Content() {
        val context = LocalContext.current.applicationContext
        val viewModel = EntryPoints.get(context, WidgetEntryPoint::class.java).getViewModel()

        GoalWidgetContent(
            title = "Meow Meow Meow",
            primaryText = "Almost there, You've got this!",
            amountDay = "$153.45/day",
            amountWeek = "$598.00/week",
            progress = 0.8f
        )

    }

    @Composable
    fun GoalWidgetContent(
        title: String,
        primaryText: String,
        amountDay: String,
        amountWeek: String,
        progress: Float
    ) {
        Column(
            modifier = GlanceModifier.background(
                ColorProvider(
                    Color(0xFFFBFDF9),
                    Color(0xFF191C1A)
                )
            ).height(100.dp).width(185.dp).padding(4.dp)
        ) {
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column {
                    Text(
                        text = title,
                        modifier = GlanceModifier.padding(start = 8.dp, end = 8.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(
                                Color(0xFF191C1A),
                                Color(0xFFE1E3DF)
                            )
                        ),
                        maxLines = 1,
                    )
                    Text(
                        text = primaryText,
                        modifier = GlanceModifier.padding(start = 8.dp, end = 8.dp),
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorProvider(
                                Color(0xFF191C1A),
                                Color(0xFFE1E3DF)
                            )
                        ),
                    )
                    LinearProgressIndicator(
                        backgroundColor = ColorProvider(
                            Color(0xFF191C1A),
                            Color(0xFFE1E3DF)
                        ),
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .padding(start = 8.dp, end = 8.dp, top = 6.dp).cornerRadius(40.dp),
                        progress = progress,
                    )
                }
            }

            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                WidgetDuration(text = amountDay)
                Spacer(modifier = GlanceModifier.width(4.dp))
                WidgetDuration(text = amountWeek)
            }
        }
    }


    @Composable
    fun WidgetDuration(
        text: String,
    ) {
        Box(
            modifier = GlanceModifier.background(
                ColorProvider(
                    Color(0xFF006C4A),
                    Color(0xFF28E1A0)
                )
            ).cornerRadius(6.dp), contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 10.sp,
                    color = ColorProvider(
                        Color(0xFFFFFFFF),
                        Color(0xFF003824)
                    )
                ),
                modifier = GlanceModifier.padding(4.dp),
            )

        }
    }

}