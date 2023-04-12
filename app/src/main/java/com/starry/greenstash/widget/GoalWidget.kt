package com.starry.greenstash.widget


import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.RemoteViews
import com.starry.greenstash.R
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.utils.GoalTextUtils
import com.starry.greenstash.utils.PreferenceUtils
import com.starry.greenstash.utils.Utils
import dagger.hilt.EntryPoints


const val TYPE_MANUAL_REFRESH = "widget_manual_refresh"

class GoalWidget : AppWidgetProvider() {
    private lateinit var viewModel: WidgetViewModel
    private var isManualRefresh = false

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        initialiseVm(context)
    }

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        initialiseVm(context)
        for (appWidgetId in appWidgetIds) {
            viewModel.getGoalFromWidgetId(appWidgetId) { goalItem ->
                updateWidgetContents(context, appWidgetId, goalItem)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        isManualRefresh = if (intent?.type != null) {
            intent.type.equals(TYPE_MANUAL_REFRESH)
        } else {
            false
        }
        if (intent?.action.equals(Intent.ACTION_SCREEN_ON)) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(
                ComponentName(
                    context, GoalWidget::class.java
                )
            )
            if (ids.isNotEmpty()) {
                onUpdate(context, appWidgetManager, ids)
            }
        }
    }

    fun updateWidgetContents(
        context: Context, appWidgetId: Int, goalItem: GoalWithTransactions
    ) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val views = RemoteViews(context.packageName, R.layout.goal_widget)

        // Check if widget was manually refreshed.
        if (isManualRefresh) {
            views.setViewVisibility(R.id.widgetUpdateButton, View.INVISIBLE)
            views.setViewVisibility(R.id.widgetUpdateProgress, View.VISIBLE)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        // Set widget title.
        views.setCharSequence(R.id.widgetTitle, "setText", goalItem.goal.title)

        // Set Widget description.
        val defCurrency = PreferenceUtils.getString(PreferenceUtils.DEFAULT_CURRENCY, "")
        val widgetDesc = context.getString(R.string.goal_widget_desc)
            .format("$defCurrency${goalItem.getCurrentlySavedAmount()} | $defCurrency${goalItem.goal.targetAmount}")
        views.setCharSequence(R.id.widgetDesc, "setText", widgetDesc)

        // Calculate how much need to save per day, week & month.
        val remainingAmount = (goalItem.goal.targetAmount - goalItem.getCurrentlySavedAmount())
        if (remainingAmount > 0f) {
            if (goalItem.goal.deadline.isNotEmpty() && goalItem.goal.deadline.isNotBlank()) {
                val calculatedDays = GoalTextUtils.calcRemainingDays(goalItem.goal)
                if (calculatedDays.remainingDays > 2) {
                    val amountDays = "$defCurrency${
                        Utils.formatCurrency(
                            Utils.roundDecimal(
                                remainingAmount / calculatedDays.remainingDays
                            )
                        )
                    }/${context.getString(R.string.goal_approx_saving_day)}"
                    views.setCharSequence(R.id.widgetAmountDay, "setText", amountDays)
                    views.setViewVisibility(R.id.widgetAmountDay, View.VISIBLE)
                }
                if (calculatedDays.remainingDays > 7) {
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
                    views.setCharSequence(R.id.widgetAmountWeek, "setText", amountWeeks)
                    views.setViewVisibility(R.id.widgetAmountWeek, View.VISIBLE)
                }
            } else {
                views.setViewVisibility(R.id.widgetNoDeadlineSet, View.VISIBLE)
            }
        } else {
            views.setViewVisibility(R.id.amountDurationGroup, View.GONE)
            views.setViewVisibility(R.id.widgetNoDeadlineSet, View.GONE)
            views.setViewVisibility(R.id.widgetGoalAchieved, View.VISIBLE)
        }

        // Calculate current progress percentage.
        val progressPercent =
            ((goalItem.getCurrentlySavedAmount() / goalItem.goal.targetAmount) * 100).toInt()
        views.setProgressBar(R.id.widgetGoalProgress, 100, progressPercent, false)

        // Set refresh button click action.
        val intent = Intent(context, GoalWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intent.type = TYPE_MANUAL_REFRESH
        val ids = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, GoalWidget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widgetUpdateButton, pendingIntent)

        // update view contents.
        if (isManualRefresh) {
            Handler(Looper.getMainLooper()).postDelayed({
                views.setViewVisibility(R.id.widgetUpdateButton, View.VISIBLE)
                views.setViewVisibility(R.id.widgetUpdateProgress, View.GONE)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }, 750)
        } else {
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun initialiseVm(context: Context) {
        if (!this::viewModel.isInitialized) {
            viewModel = EntryPoints.get(context.applicationContext, WidgetEntryPoint::class.java)
                .getViewModel()
            PreferenceUtils.initialize(context)
        }
    }

}