package com.yugentech.sessions.notification.reminders

import android.content.Context
import org.json.JSONObject

/**
 * What the reminders need to remember, kept where the receivers and NudgeWorker can read it
 * without the app:
 * - The daily reminders that are on: a restart clears every alarm, and BootReceiver books them
 *   again from here.
 * - Today's nudge plan, so NudgeWorker sends at most one nudge a day.
 */
internal object ReminderStore {

    data class Daily(val id: Int, val hour: Int, val minute: Int, val title: String, val message: String)

    /**
     * Today's nudge: whether today gets a playful one (rolled once a day), the hour from which it
     * may be sent, and whether today's decision has been made.
     */
    data class DayPlan(val epochDay: Long, val sendFromHour: Int, val playful: Boolean, val done: Boolean)

    private const val DAILY_PREFS = "sessions_daily_reminders"
    private const val NUDGE_PREFS = "sessions_nudges"
    private const val PLAN_KEY = "day_plan"

    private fun prefs(context: Context, name: String) = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    fun saveDaily(context: Context, daily: Daily) {
        val json = JSONObject()
            .put("hour", daily.hour)
            .put("minute", daily.minute)
            .put("title", daily.title)
            .put("message", daily.message)
        prefs(context, DAILY_PREFS).edit().putString(daily.id.toString(), json.toString()).apply()
    }

    fun removeDaily(context: Context, id: Int) {
        prefs(context, DAILY_PREFS).edit().remove(id.toString()).apply()
    }

    fun allDaily(context: Context): List<Daily> = prefs(context, DAILY_PREFS).all.mapNotNull { (key, value) ->
        val id = key.toIntOrNull() ?: return@mapNotNull null
        runCatching {
            val json = JSONObject(value as String)
            Daily(id, json.getInt("hour"), json.getInt("minute"), json.getString("title"), json.getString("message"))
        }.getOrNull()
    }

    fun saveDayPlan(context: Context, plan: DayPlan) {
        val json = JSONObject()
            .put("epochDay", plan.epochDay)
            .put("sendFromHour", plan.sendFromHour)
            .put("playful", plan.playful)
            .put("done", plan.done)
        prefs(context, NUDGE_PREFS).edit().putString(PLAN_KEY, json.toString()).apply()
    }

    fun dayPlan(context: Context): DayPlan? = runCatching {
        val json = JSONObject(prefs(context, NUDGE_PREFS).getString(PLAN_KEY, null) ?: return null)
        DayPlan(json.getLong("epochDay"), json.getInt("sendFromHour"), json.getBoolean("playful"), json.getBoolean("done"))
    }.getOrNull()
}
