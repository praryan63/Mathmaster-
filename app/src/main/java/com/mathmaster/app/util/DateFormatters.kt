package com.mathmaster.app.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateFormatters {

    private val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayFormat = SimpleDateFormat("d MMM", Locale.US)
    private val fullFormat = SimpleDateFormat("d MMM yyyy", Locale.US)

    fun dayKey(date: Date = Date()): String = dayFormat.format(date)

    fun dayKey(daysAgo: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return dayFormat.format(calendar.time)
    }

    fun shortLabel(dayKey: String): String = runCatching { displayFormat.format(dayFormat.parse(dayKey)!!) }
        .getOrDefault(dayKey.takeLast(5))

    fun fullLabel(timestamp: Long): String = fullFormat.format(Date(timestamp))

    fun timeLabel(timestamp: Long): String {
        val format = SimpleDateFormat("h:mm a", Locale.US)
        return format.format(Date(timestamp))
    }

    fun durationLabel(millis: Long): String {
        val totalSeconds = (millis / 1000L).coerceAtLeast(0L)
        val minutes = totalSeconds / 60L
        val seconds = totalSeconds % 60L
        return if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"
    }

    fun clockLabel(millis: Long): String {
        val totalSeconds = (millis / 1000L).coerceAtLeast(0L)
        val minutes = totalSeconds / 60L
        val seconds = totalSeconds % 60L
        return "%02d:%02d".format(minutes, seconds)
    }
}
