package com.starry.greenstash.widget


import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle
import android.widget.RemoteViews
import com.starry.greenstash.R
import com.starry.greenstash.utils.toToast
import dagger.hilt.EntryPoints


class WidgetProvider : AppWidgetProvider() {

    private lateinit var viewModel: WidgetViewModel

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        if (!this::viewModel.isInitialized) {
            val viewModel = EntryPoints
                .get(context.applicationContext, WidgetEntryPoint::class.java)
                .getViewModel()
        }

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.goal_widget)
            setWidgetContents(views)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        //val views = RemoteViews(context.packageName, R.layout.goal_widget)
        val minWidth: Int = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        val maxWidth: Int = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
        val minHeight: Int = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
        val maxHeight: Int = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)

        "$minWidth, $maxWidth, $minHeight, $maxHeight".toToast(context)

        //appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun setWidgetContents(views: RemoteViews) {
        views.setCharSequence(R.id.widgetTitle, "setText", "Genshin Character")
        views.setCharSequence(R.id.widgetDesc, "setText", "Saved 2,500/3,600.00")
        views.setCharSequence(R.id.widgetAmountDay, "setText", "$153.45/day")
        views.setCharSequence(R.id.widgetAmountWeek, "setText", "$253.45/day")
        views.setCharSequence(R.id.widgetAmountMonth, "setText", "$1530.45/day")
        views.setProgressBar(R.id.widgetGoalProgress, 100, 69, false)

    }


}