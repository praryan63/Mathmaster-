package com.mathmaster.app.domain.question

import kotlin.math.abs

data class Fraction(val numerator: Long, val denominator: Long) {

    init {
        require(denominator != 0L) { "Denominator cannot be zero" }
    }

    operator fun plus(other: Fraction): Fraction =
        of(numerator * other.denominator + other.numerator * denominator, denominator * other.denominator)

    operator fun minus(other: Fraction): Fraction =
        of(numerator * other.denominator - other.numerator * denominator, denominator * other.denominator)

    operator fun times(other: Fraction): Fraction =
        of(numerator * other.numerator, denominator * other.denominator)

    operator fun div(other: Fraction): Fraction {
        require(other.numerator != 0L) { "Cannot divide by zero fraction" }
        return of(numerator * other.denominator, denominator * other.numerator)
    }

    fun decimal(): Double = numerator.toDouble() / denominator.toDouble()

    override fun toString(): String =
        if (denominator == 1L) numerator.toString() else "$numerator/$denominator"

    companion object {
        fun of(numerator: Long, denominator: Long): Fraction {
            require(denominator != 0L) { "Denominator cannot be zero" }
            val divisor = gcd(abs(numerator), abs(denominator))
            var n = numerator / divisor
            var d = denominator / divisor
            if (d < 0) {
                n = -n
                d = -d
            }
            return Fraction(n, d)
        }

        fun fromInt(value: Int): Fraction = of(value.toLong(), 1L)

        fun gcd(a: Long, b: Long): Long {
            var x = abs(a)
            var y = abs(b)
            while (y != 0L) {
                val remainder = x % y
                x = y
                y = remainder
            }
            return if (x == 0L) 1L else x
        }

        fun lcm(a: Long, b: Long): Long {
            if (a == 0L || b == 0L) return 0L
            return abs(a / gcd(a, b) * b)
        }
    }
}
