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
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.starry.greenstash.R
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.utils.GoalTextUtils
import com.starry.greenstash.utils.NumberUtils
import com.starry.greenstash.utils.PreferenceUtil
import dagger.hilt.EntryPoints


private const val WIDGET_MANUAL_REFRESH = "widget_manual_refresh"
private const val MAX_AMOUNT_DIGITS = 1000
private const val FULL_WIDGET_MIN_HEIGHT = 110

class GoalWidget : AppWidgetProvider() {

    // Viewmodel for the widget.
    private lateinit var viewModel: WidgetViewModel

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

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        val minHeight = newOptions?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0) ?: 0
        initialiseVm(context)
        viewModel.getGoalFromWidgetId(appWidgetId) { goalItem ->
            updateWidgetContents(context, appWidgetId, goalItem, minHeight)
        }
    }

    fun updateWidgetContents(
        context: Context,
        appWidgetId: Int,
        goalItem: GoalWithTransactions,
        minHeight: Int? = null
    ) {
        val preferenceUtil = PreferenceUtil(context)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val views = RemoteViews(context.packageName, R.layout.goal_widget)

        // Set widget title.
        views.setCharSequence(R.id.widgetTitle, "setText", goalItem.goal.title)

        // Set Widget description.
        val defCurrency = preferenceUtil.getString(PreferenceUtil.DEFAULT_CURRENCY_STR, "")!!
        val datePattern = preferenceUtil.getString(PreferenceUtil.DATE_FORMAT_STR, "")!!

        val savedAmount = goalItem.getCurrentlySavedAmount().let {
            if (it > MAX_AMOUNT_DIGITS) {
                "${NumberUtils.getCurrencySymbol(defCurrency)}${NumberUtils.prettyCount(it)}"
            } else NumberUtils.formatCurrency(it, defCurrency)
        }
        val targetAmount = goalItem.goal.targetAmount.let {
            if (it > MAX_AMOUNT_DIGITS) {
                "${NumberUtils.getCurrencySymbol(defCurrency)}${NumberUtils.prettyCount(it)}"
            } else NumberUtils.formatCurrency(it, defCurrency)
        }
        val widgetDesc = context.getString(R.string.goal_widget_desc)
            .format("$savedAmount / $targetAmount")
        views.setCharSequence(R.id.widgetDesc, "setText", widgetDesc)

        // Calculate and display savings per day and week if applicable.
        handleSavingsPerDuration(context, views, goalItem, defCurrency, datePattern, minHeight)

        // Display appropriate views when the goal is achieved.
        handleGoalAchieved(views, goalItem, minHeight)

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
        views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent)

        // Update widget contents.
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun handleSavingsPerDuration(
        context: Context,
        views: RemoteViews,
        goalItem: GoalWithTransactions,
        defCurrency: String,
        datePattern: String,
        minHeight: Int? = null
    ) {
        val remainingAmount = (goalItem.goal.targetAmount - goalItem.getCurrentlySavedAmount())
        // Check if system locale is english to drop full stop in remaining days or weeks.
        val localeEnglish = context.resources.configuration.locales[0].language == "en"

        if (remainingAmount > 0f && goalItem.goal.deadline.isNotBlank()) {
            val calculatedDays = GoalTextUtils.calcRemainingDays(goalItem.goal, datePattern)

            if (calculatedDays.remainingDays > 2) {
                // Calculate amount needed to save per day.
                val calcPerDayAmount =
                    NumberUtils.roundDecimal(remainingAmount / calculatedDays.remainingDays)
                // Build amount per day text by checking if the amount is greater than MAX_AMOUNT_DIGITS,
                // if yes, then use prettyCount to format the amount.
                val amountPerDayText = calcPerDayAmount.let {
                    if (it > MAX_AMOUNT_DIGITS) {
                        "${NumberUtils.getCurrencySymbol(defCurrency)}${NumberUtils.prettyCount(it)}"
                    } else NumberUtils.formatCurrency(it, defCurrency)
                } + "/${context.getString(R.string.goal_approx_saving_day)}".let {
                    if (localeEnglish) it.dropLast(1) else it
                }

                views.setCharSequence(R.id.widgetAmountDay, "setText", amountPerDayText)
                views.setViewVisibility(R.id.widgetAmountDay, View.VISIBLE)
            }

            if (calculatedDays.remainingDays > 7) {
                // Calculate amount needed to save per week.
                val calcPerWeekAmount =
                    NumberUtils.roundDecimal(remainingAmount / (calculatedDays.remainingDays / 7))
                // Build amount per week text by checking if the amount is greater than MAX_AMOUNT_DIGITS,
                // if yes, then use prettyCount to format the amount.
                val amountPerWeekText = calcPerWeekAmount.let {
                    if (it > MAX_AMOUNT_DIGITS) {
                        "${NumberUtils.getCurrencySymbol(defCurrency)}${NumberUtils.prettyCount(it)}"
                    } else NumberUtils.formatCurrency(it, defCurrency)
                } + "/${context.getString(R.string.goal_approx_saving_week)}".let {
                    if (localeEnglish) it.dropLast(1) else it
                }
                views.setCharSequence(R.id.widgetAmountWeek, "setText", amountPerWeekText)
                views.setViewVisibility(R.id.widgetAmountWeek, View.VISIBLE)
            }

            // Hide views if the widget is too small.
            if (minHeight != null && minHeight < FULL_WIDGET_MIN_HEIGHT) {
                views.setViewVisibility(R.id.amountDurationGroup, View.GONE)
            } else {
                views.setViewVisibility(R.id.amountDurationGroup, View.VISIBLE)
            }
            // Always hide goal achieved view since the goal is not achieved.
            views.setViewVisibility(R.id.widgetGoalAchieved, View.GONE)
        }
    }

    private fun handleGoalAchieved(
        views: RemoteViews,
        goalItem: GoalWithTransactions,
        minHeight: Int? = null
    ) {
        if (goalItem.getCurrentlySavedAmount() >= goalItem.goal.targetAmount) {
            // Hide goal achieved view if the widget is too small.
            if (minHeight != null && minHeight < FULL_WIDGET_MIN_HEIGHT) {
                views.setViewVisibility(R.id.widgetGoalAchieved, View.GONE)
            } else {
                views.setViewVisibility(R.id.widgetGoalAchieved, View.VISIBLE)
            }
            // Always hide amount per day and week views since the goal is achieved.
            views.setViewVisibility(R.id.amountDurationGroup, View.GONE)
        }
    }

    private fun handleProgress(views: RemoteViews, goalItem: GoalWithTransactions) {
        val progressPercent =
            ((goalItem.getCurrentlySavedAmount() / goalItem.goal.targetAmount) * 100).toInt()
        views.setProgressBar(R.id.widgetGoalProgress, 100, progressPercent, false)
    }


    private fun initialiseVm(context: Context) {
        if (!this::viewModel.isInitialized) {
            Log.d("GoalWidget", "Initialising viewmodel")
            viewModel = EntryPoints
                .get(context.applicationContext, WidgetEntryPoint::class.java).getViewModel()
        }
    }

}