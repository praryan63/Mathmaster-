package com.mathmaster.app.domain.analytics

import com.mathmaster.app.util.DateFormatters

data class DayPoint(val label: String, val attempted: Int, val correct: Int)
data class PeriodPoint(val label: String, val attempted: Int, val correct: Int)

object AnalyticsEngine {

    fun accuracy(correct: Int, total: Int): Int =
        if (total == 0) 0 else ((correct.toDouble() / total.toDouble()) * 100.0).toInt()

    fun averageSeconds(totalDurationMs: Long, totalAttempts: Int): Double =
        if (totalAttempts == 0) 0.0 else totalDurationMs / totalAttempts.toDouble() / 1000.0

    fun streak(activeDays: Set<String>): Int {
        if (activeDays.isEmpty()) return 0
        var cursor = 0
        if (!activeDays.contains(DateFormatters.dayKey(0))) {
            cursor = 1
            if (!activeDays.contains(DateFormatters.dayKey(1))) return 0
        }
        var count = 0
        while (activeDays.contains(DateFormatters.dayKey(cursor))) {
            count++
            cursor++
        }
        return count
    }

    fun lastNDays(dayCounts: Map<String, Pair<Int, Int>>, days: Int): List<DayPoint> {
        val result = mutableListOf<DayPoint>()
        for (index in (days - 1) downTo 0) {
            val key = DateFormatters.dayKey(index)
            val value = dayCounts[key] ?: Pair(0, 0)
            result.add(DayPoint(DateFormatters.shortLabel(key), value.first, value.second))
        }
        return result
    }

    fun lastNWeeks(dayCounts: Map<String, Pair<Int, Int>>, weeks: Int): List<PeriodPoint> {
        val result = mutableListOf<PeriodPoint>()
        for (index in (weeks - 1) downTo 0) {
            var attempted = 0
            var correct = 0
            for (day in 0 until 7) {
                val key = DateFormatters.dayKey(index * 7 + day)
                val value = dayCounts[key] ?: Pair(0, 0)
                attempted += value.first
                correct += value.second
            }
            result.add(PeriodPoint("W${weeks - index}", attempted, correct))
        }
        return result
    }

    fun lastNMonths(dayCounts: Map<String, Pair<Int, Int>>, months: Int): List<PeriodPoint> {
        val monthFormat = java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.US)
        val labelFormat = java.text.SimpleDateFormat("MMM", java.util.Locale.US)
        val result = mutableListOf<PeriodPoint>()
        for (index in (months - 1) downTo 0) {
            val calendar = java.util.Calendar.getInstance()
            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
            calendar.add(java.util.Calendar.MONTH, -index)
            val prefix = monthFormat.format(calendar.time)
            var attempted = 0
            var correct = 0
            for ((key, value) in dayCounts) {
                if (key.startsWith(prefix)) {
                    attempted += value.first
                    correct += value.second
                }
            }
            result.add(PeriodPoint(labelFormat.format(calendar.time), attempted, correct))
        }
        return result
    }
}
