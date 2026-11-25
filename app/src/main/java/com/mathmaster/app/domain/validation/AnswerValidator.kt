package com.mathmaster.app.domain.validation

import com.mathmaster.app.domain.model.Question
import java.util.Locale
import kotlin.math.abs

object AnswerValidator {

    fun normalize(input: String): String = input.trim()
        .lowercase(Locale.US)
        .replace("−", "-")
        .replace("–", "-")
        .replace("—", "-")
        .replace(",", "")
        .replace(" ", "")
        .replace("₹", "")
        .replace("rs.", "")
        .replace("rs", "")
        .replace("kmph", "")
        .replace("km/h", "")
        .replace("m/s", "")
        .replace("km", "")
        .replace("metres", "")
        .replace("meters", "")
        .replace("seconds", "")
        .replace("second", "")
        .replace("hours", "")
        .replace("hour", "")
        .replace("units", "")
        .replace("unit", "")
        .replace("kg", "")

    fun isCorrect(question: Question, input: String): Boolean {
        val expected = normalize(question.answer)
        var actual = normalize(input)
        if (actual.isEmpty()) return false

        if (expected == "yes" || expected == "no") {
            actual = when (actual) {
                "y", "true", "1" -> "yes"
                "n", "false", "0" -> "no"
                else -> actual
            }
            return expected == actual
        }

        if (expected == actual) return true

        val expectedValue = toDecimal(expected) ?: return false
        val actualValue = toDecimal(actual) ?: return false
        val tolerance = if (expected.contains('.') || actual.contains('.')) 0.011 else 0.0001
        return abs(expectedValue - actualValue) <= tolerance
    }

    private fun toDecimal(value: String): Double? {
        return if (value.contains('/')) {
            val parts = value.split('/')
            if (parts.size != 2) return null
            val numerator = parts[0].toDoubleOrNull() ?: return null
            val denominator = parts[1].toDoubleOrNull() ?: return null
            if (denominator == 0.0) null else numerator / denominator
        } else {
            value.toDoubleOrNull()
        }
    }
}
