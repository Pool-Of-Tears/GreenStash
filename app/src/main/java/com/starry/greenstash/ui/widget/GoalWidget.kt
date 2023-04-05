package com.starry.greenstash.ui.widget

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.LocalGlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text

class GoalWidget : GlanceAppWidget() {
    @Composable
    override fun Content() {
        Column(
            modifier = GlanceModifier.background(MaterialTheme.colorScheme.background).fillMaxSize()
        ) {
            Text("Hello World! ${LocalGlanceId.current}")
        }
    }
}

class GoalWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = GoalWidget()
}