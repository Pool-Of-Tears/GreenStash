package com.starry.greenstash.widget


import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle
import android.widget.RemoteViews
import com.starry.greenstash.R
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.utils.GoalTextUtils
import com.starry.greenstash.utils.PreferenceUtils
import com.starry.greenstash.utils.Utils
import com.starry.greenstash.utils.toToast
import dagger.hilt.EntryPoints


class GoalWidgetProvider : AppWidgetProvider() {

    private lateinit var viewModel: WidgetViewModel

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        initialiseVm(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        initialiseVm(context)
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.goal_widget)
            viewModel.getWidgetData(appWidgetId, context) { goalItem ->
                setWidgetContents(context, views, goalItem)
            }
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

    fun setWidgetContents(context: Context, views: RemoteViews, goalItem: GoalWithTransactions) {
        val defCurrency =
            PreferenceUtils.getString(PreferenceUtils.DEFAULT_CURRENCY, "")
        val widgetDesc = context.getString(R.string.goal_widget_desc)
            .format("$defCurrency${goalItem.getCurrentlySavedAmount()}/$defCurrency${goalItem.goal.targetAmount}")
        val remainingAmount = (goalItem.goal.targetAmount - goalItem.getCurrentlySavedAmount())

        // Calculate how much need to save per day, week & month.
        val calculatedDays = GoalTextUtils.calcRemainingDays(goalItem.goal)
        val amountDays = "$defCurrency${
            Utils.formatCurrency(
                Utils.roundDecimal(
                    remainingAmount / calculatedDays.remainingDays
                )
            )
        }/${context.getString(R.string.goal_approx_saving_day)}"
        val amountWeeks = "$defCurrency${
            Utils.formatCurrency(
                Utils.roundDecimal(
                    remainingAmount / (calculatedDays.remainingDays / 7)
                )
            )
        }/${
            context.getString(
                R.string.goal_approx_saving_week
            )
        }"
        // Calculate current progress percentage.
        val progressPercent =
            ((goalItem.getCurrentlySavedAmount() / goalItem.goal.targetAmount) * 100).toInt()

        // Set widget title.
        views.setCharSequence(R.id.widgetTitle, "setText", goalItem.goal.title)
        views.setCharSequence(R.id.widgetDesc, "setText", widgetDesc)
        // Set Widget description.
        views.setCharSequence(R.id.widgetAmountDay, "setText", amountDays)
        views.setCharSequence(R.id.widgetAmountWeek, "setText", amountWeeks)
        views.setProgressBar(R.id.widgetGoalProgress, 100, progressPercent, false)

    }

    private fun initialiseVm(context: Context) {
        if (!this::viewModel.isInitialized) {
            viewModel = EntryPoints
                .get(context.applicationContext, WidgetEntryPoint::class.java)
                .getViewModel()
            PreferenceUtils.initialize(context)
        }
    }

}