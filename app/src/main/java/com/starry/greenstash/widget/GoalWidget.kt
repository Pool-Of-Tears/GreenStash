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
import com.starry.greenstash.utils.PreferenceUtil
import com.starry.greenstash.utils.Utils
import dagger.hilt.EntryPoints


private const val WIDGET_MANUAL_REFRESH = "widget_manual_refresh"

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
            intent.type.equals(WIDGET_MANUAL_REFRESH)
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

    fun updateWidgetContents(context: Context, appWidgetId: Int, goalItem: GoalWithTransactions) {
        val preferenceUtil = PreferenceUtil(context)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val views = RemoteViews(context.packageName, R.layout.goal_widget)

        if (isManualRefresh) {
            handleManualRefresh(views, appWidgetManager, appWidgetId)
        }

        // Set widget title.
        views.setCharSequence(R.id.widgetTitle, "setText", goalItem.goal.title)

        // Set Widget description.
        val defCurrency = preferenceUtil.getString(PreferenceUtil.DEFAULT_CURRENCY_STR, "")!!
        val datePattern = preferenceUtil.getString(PreferenceUtil.DATE_FORMAT_STR, "")!!

        val widgetDesc = context.getString(R.string.goal_widget_desc)
            .format(
                "${
                    Utils.formatCurrency(
                        goalItem.getCurrentlySavedAmount(),
                        defCurrency
                    )
                } / ${Utils.formatCurrency(goalItem.goal.targetAmount, defCurrency)}"
            )
        views.setCharSequence(R.id.widgetDesc, "setText", widgetDesc)

        // Calculate and display savings per day and week if applicable.
        handleSavingsPerDuration(context, views, goalItem, defCurrency, datePattern)

        // Display appropriate views when the goal is achieved.
        handleGoalAchieved(views, goalItem)

        // Calculate current progress percentage.
        handleProgress(views, goalItem)

        // Set refresh button click action.
        val intent = Intent(context, GoalWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            type = WIDGET_MANUAL_REFRESH
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, GoalWidget::class.java))
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widgetUpdateButton, pendingIntent)

        // Update widget contents.
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun handleManualRefresh(
        views: RemoteViews,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        views.setViewVisibility(R.id.widgetUpdateButton, View.INVISIBLE)
        views.setViewVisibility(R.id.widgetUpdateProgress, View.VISIBLE)
        appWidgetManager.updateAppWidget(appWidgetId, views)

        // Delayed update to show the button again.
        Handler(Looper.getMainLooper()).postDelayed({
            views.setViewVisibility(R.id.widgetUpdateButton, View.VISIBLE)
            views.setViewVisibility(R.id.widgetUpdateProgress, View.GONE)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }, 750)
    }

    private fun handleSavingsPerDuration(
        context: Context,
        views: RemoteViews,
        goalItem: GoalWithTransactions,
        defCurrency: String,
        datePattern: String
    ) {
        val remainingAmount = (goalItem.goal.targetAmount - goalItem.getCurrentlySavedAmount())

        if (remainingAmount > 0f && goalItem.goal.deadline.isNotEmpty()) {
            val calculatedDays = GoalTextUtils.calcRemainingDays(goalItem.goal, datePattern)

            if (calculatedDays.remainingDays > 2) {
                val amountDays = "${
                    Utils.formatCurrency(
                        amount = Utils.roundDecimal(remainingAmount / calculatedDays.remainingDays),
                        currencyCode = defCurrency
                    )
                }/${context.getString(R.string.goal_approx_saving_day)}"
                views.setCharSequence(R.id.widgetAmountDay, "setText", amountDays)
                views.setViewVisibility(R.id.widgetAmountDay, View.VISIBLE)
            }

            if (calculatedDays.remainingDays > 7) {
                val amountWeeks = "${
                    Utils.formatCurrency(
                        amount = Utils.roundDecimal(remainingAmount / (calculatedDays.remainingDays / 7)),
                        currencyCode = defCurrency
                    )
                }/${context.getString(R.string.goal_approx_saving_week)}"
                views.setCharSequence(R.id.widgetAmountWeek, "setText", amountWeeks)
                views.setViewVisibility(R.id.widgetAmountWeek, View.VISIBLE)
            }

            views.setViewVisibility(R.id.amountDurationGroup, View.VISIBLE)
            views.setViewVisibility(R.id.widgetGoalAchieved, View.GONE)
        }
    }

    private fun handleGoalAchieved(views: RemoteViews, goalItem: GoalWithTransactions) {
        if (goalItem.getCurrentlySavedAmount() >= goalItem.goal.targetAmount) {
            views.setViewVisibility(R.id.amountDurationGroup, View.GONE)
            views.setViewVisibility(R.id.widgetGoalAchieved, View.VISIBLE)
        }
    }

    private fun handleProgress(views: RemoteViews, goalItem: GoalWithTransactions) {
        val progressPercent =
            ((goalItem.getCurrentlySavedAmount() / goalItem.goal.targetAmount) * 100).toInt()
        views.setProgressBar(R.id.widgetGoalProgress, 100, progressPercent, false)
    }


    private fun initialiseVm(context: Context) {
        if (!this::viewModel.isInitialized) {
            println("viewmodel not initialised")
            viewModel = EntryPoints
                .get(context.applicationContext, WidgetEntryPoint::class.java).getViewModel()
        }
    }

}