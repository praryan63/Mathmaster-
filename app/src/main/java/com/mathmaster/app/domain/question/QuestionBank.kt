package com.mathmaster.app.domain.question

import com.mathmaster.app.domain.model.Difficulty
import com.mathmaster.app.domain.model.Question
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

class QuestionBank(private val random: Random = Random.Default) {

    fun generate(
        topicId: String,
        difficulty: Difficulty,
        count: Int,
        avoid: Set<String> = emptySet(),
        weakTables: Set<Int> = emptySet()
    ): List<Question> {
        if (count <= 0) return emptyList()
        val used = avoid.toMutableSet()
        val questions = mutableListOf<Question>()
        var guard = 0
        val limit = count * 60 + 300
        while (questions.size < count && guard < limit) {
            guard++
            val question = try {
                generateOne(topicId, difficulty, weakTables)
            } catch (exception: Exception) {
                continue
            }
            if (question.signature in used) continue
            used.add(question.signature)
            questions.add(question)
        }
        if (questions.isEmpty()) {
            questions.add(retag(addition(Difficulty.EASY), TopicCatalog.find(topicId)))
        }
        return questions.take(count)
    }

    private fun generateOne(topicId: String, difficulty: Difficulty, weakTables: Set<Int>): Question {
        val topic = TopicCatalog.find(topicId)
        return when (topicId) {
            "addition" -> addition(difficulty)
            "subtraction" -> subtraction(difficulty)
            "multiplication" -> multiplication(difficulty)
            "division" -> division(difficulty)
            "bodmas" -> bodmas(difficulty)
            "simplification" -> simplification(difficulty)
            "fractions" -> fractions(difficulty)
            "decimals" -> decimals(difficulty)
            "percentage" -> percentage(difficulty)
            "squares" -> squares(difficulty)
            "square_roots" -> squareRoots(difficulty)
            "cubes" -> cubes(difficulty)
            "cube_roots" -> cubeRoots(difficulty)
            "lcm_hcf" -> lcmHcf(difficulty)
            "ratio_proportion" -> ratioProportion(difficulty)
            "average" -> average(difficulty)
            "number_system" -> numberSystem(difficulty)
            "divisibility" -> divisibility(difficulty)
            "factors_multiples" -> factorsMultiples(difficulty)
            "tables_mixed" -> table(rnd(1, 50), difficulty, "mixed")
            "tables_forward" -> table(rnd(1, 50), difficulty, "forward")
            "tables_backward" -> table(rnd(1, 50), difficulty, "backward")
            "tables_random" -> table(rnd(1, 50), difficulty, "random")
            "tables_weak" -> {
                val pool = if (weakTables.isEmpty()) (1..50).toList() else weakTables.toList()
                table(pool.random(random), difficulty, "weak")
            }
            "ibps_simplification" -> ibpsSimplification(difficulty)
            "ibps_approximation" -> ibpsApproximation(difficulty)
            "ibps_number_series" -> ibpsNumberSeries(difficulty)
            "ibps_quadratic" -> ibpsQuadratic(difficulty)
            "ibps_percentage" -> ibpsPercentage(difficulty)
            "ibps_profit_loss" -> ibpsProfitLoss(difficulty)
            "ibps_si_ci" -> ibpsInterest(difficulty)
            "ibps_ratio" -> ibpsRatio(difficulty)
            "ibps_average" -> ibpsAverage(difficulty)
            "ibps_partnership" -> ibpsPartnership(difficulty)
            "ibps_time_work" -> ibpsTimeWork(difficulty)
            "ibps_pipes" -> ibpsPipes(difficulty)
            "ibps_speed_distance" -> ibpsSpeedDistance(difficulty)
            "ibps_trains" -> ibpsTrains(difficulty)
            "ibps_boats" -> ibpsBoats(difficulty)
            "ibps_mixture" -> ibpsMixture(difficulty)
            "ibps_ages" -> ibpsAges(difficulty)
            "ibps_probability" -> ibpsProbability(difficulty)
            "ibps_permutation" -> ibpsPermutation(difficulty)
            "ibps_di" -> ibpsDataInterpretation(difficulty)
            "speed_add" -> retag(addition(Difficulty.EASY), topic)
            "speed_sub" -> retag(subtraction(Difficulty.EASY), topic)
            "speed_mul" -> retag(multiplication(Difficulty.EASY), topic)
            "speed_div" -> retag(division(Difficulty.EASY), topic)
            "speed_percentage" -> retag(percentage(Difficulty.EASY), topic)
            "speed_squares" -> retag(squares(Difficulty.EASY), topic)
            "speed_cubes" -> retag(cubes(Difficulty.EASY), topic)
            "speed_approximation" -> ibpsApproximation(Difficulty.EASY).let { retag(it, topic) }
            "speed_mental" -> mentalChallenge(difficulty)
            "mixed" -> mixedQuestion(difficulty, weakTables)
            else -> if (TopicCatalog.isTableTopic(topicId)) {
                table(TopicCatalog.tableNumber(topicId) ?: 2, difficulty, "individual")
            } else {
                addition(difficulty)
            }
        }
    }

    private fun mixedQuestion(difficulty: Difficulty, weakTables: Set<Int>): Question {
        val pool = listOf(
            "addition", "subtraction", "multiplication", "division", "percentage",
            "average", "lcm_hcf", "fractions", "squares", "ibps_number_series", "tables_mixed"
        )
        return generateOne(pool.random(random), difficulty, weakTables)
    }

    private fun retag(question: Question, topic: com.mathmaster.app.domain.model.Topic): Question =
        question.copy(
            signature = "${topic.id}|${question.prompt}",
            topicId = topic.id,
            topicName = topic.name,
            tags = question.tags + topic.id
        )

    private fun question(
        topicId: String,
        prompt: String,
        answer: String,
        explanation: String,
        difficulty: Difficulty,
        hint: String = "",
        tags: List<String> = emptyList()
    ): Question = Question(
        signature = "$topicId|$prompt",
        topicId = topicId,
        topicName = TopicCatalog.nameOf(topicId),
        prompt = prompt,
        answer = answer,
        explanation = explanation,
        difficulty = difficulty,
        hint = hint,
        tags = tags
    )

    private fun rnd(min: Int, max: Int): Int = random.nextInt(min, max + 1)
    private fun <T> List<T>.pick(): T = this[random.nextInt(size)]

    private fun digits(number: Int): Int = number.toString().sumOf { it - '0' }

    private fun isPrime(value: Int): Boolean {
        if (value < 2) return false
        var divisor = 2
        while (divisor * divisor <= value) {
            if (value % divisor == 0) return false
            divisor++
        }
        return true
    }

    private fun countFactors(value: Int): Int {
        var count = 0
        for (divisor in 1..value) if (value % divisor == 0) count++
        return count
    }

    private fun factorial(value: Int): Long {
        var result = 1L
        for (factor in 2..value) result *= factor.toLong()
        return result
    }

    private fun nCr(n: Int, r: Int): Long = factorial(n) / (factorial(r) * factorial(n - r))
    private fun nPr(n: Int, r: Int): Long = factorial(n) / factorial(n - r)

    private fun addExpression(a: Int, b: Int, c: Int, d: Int, operation: String): String =
        when (operation) {
            "a+b×c-d" -> "$a + $b × $c - $d"
            "(a+b)×c-d" -> "($a + $b) × $c - $d"
            "a×b+c÷d" -> "$a × $b + $c ÷ $d"
            else -> "$a + $b - $c × $d"
        }

    private fun evaluateDisplayExpression(expression: String): Long =
        ExpressionEvaluator.evaluate(expression.replace("×", "*").replace("÷", "/"))

    private fun scale(value: Long): String =
        BigDecimal.valueOf(value).divide(BigDecimal("100")).stripTrailingZeros().toPlainString()

    private fun decimalText(value: Long, divisor: Int): String =
        BigDecimal.valueOf(value)
            .divide(BigDecimal(divisor), 2, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()

    private fun addition(difficulty: Difficulty): Question {
        val range = when (difficulty) {
            Difficulty.EASY -> 1..50
            Difficulty.MEDIUM -> 10..500
            Difficulty.HARD -> 100..5000
        }
        val a = range.random(random)
        val b = range.random(random)
        return question(
            "addition", "$a + $b = ?", (a + b).toString(),
            "$a plus $b equals ${a + b}.", difficulty, "Add the units first, then the tens."
        )
    }

    private fun subtraction(difficulty: Difficulty): Question {
        val range = when (difficulty) {
            Difficulty.EASY -> 5..100
            Difficulty.MEDIUM -> 50..1000
            Difficulty.HARD -> 500..10000
        }
        val larger = range.random(random)
        val smaller = rnd(1, larger)
        return question(
            "subtraction", "$larger - $smaller = ?", (larger - smaller).toString(),
            "$larger minus $smaller equals ${larger - smaller}.", difficulty,
            "Borrow from the next place when needed."
        )
    }

    private fun multiplication(difficulty: Difficulty): Question {
        val first: Int
        val second: Int
        when (difficulty) {
            Difficulty.EASY -> {
                first = rnd(2, 12); second = rnd(2, 12)
            }
            Difficulty.MEDIUM -> {
                first = rnd(10, 99); second = rnd(2, 9)
            }
            else -> {
                first = rnd(100, 999); second = rnd(10, 99)
            }
        }
        return question(
            "multiplication", "$first × $second = ?", (first.toLong() * second.toLong()).toString(),
            "$first multiplied by $second equals ${first.toLong() * second.toLong()}.",
            difficulty, "Split the multiplier into tens and units."
        )
    }

    private fun division(difficulty: Difficulty): Question {
        val divisor = when (difficulty) {
            Difficulty.EASY -> rnd(2, 12)
            Difficulty.MEDIUM -> rnd(3, 30)
            else -> rnd(10, 99)
        }
        val quotient = when (difficulty) {
            Difficulty.EASY -> rnd(2, 12)
            Difficulty.MEDIUM -> rnd(5, 50)
            else -> rnd(20, 200)
        }
        val dividend = divisor * quotient
        return question(
            "division", "$dividend ÷ $divisor = ?", quotient.toString(),
            "$dividend divided by $divisor equals $quotient because $divisor × $quotient = $dividend.",
            difficulty, "Estimate the quotient before dividing."
        )
    }

    private fun bodmas(difficulty: Difficulty): Question {
        val operations = listOf("a+b×c-d", "(a+b)×c-d", "a×b+c÷d")
        val operation = operations.pick()
        val a = rnd(2, 20)
        val b = rnd(2, 12)
        val d = rnd(2, 9)
        val expression = when (operation) {
            "a+b×c-d" -> addExpression(a, b, rnd(2, 9), d, operation)
            "(a+b)×c-d" -> addExpression(a, b, rnd(2, 9), d, operation)
            else -> {
                val divisor = rnd(2, 9)
                "$a × $b + ${divisor * rnd(2, 20)} ÷ $divisor"
            }
        }
        val value = evaluateDisplayExpression(expression)
        return question(
            "bodmas", "Find the value: $expression", value.toString(),
            "Apply BODMAS: brackets, powers, division, multiplication, addition, then subtraction. Result = $value.",
            difficulty, "Multiplication and division come before addition and subtraction."
        )
    }

    private fun simplification(difficulty: Difficulty): Question {
        val a = rnd(5, 40)
        val b = rnd(3, 25)
        val divisor = rnd(2, 12)
        val quotient = rnd(2, 20)
        val c = divisor * quotient
        val expression = "$a × $b + $c ÷ $divisor"
        val value = evaluateDisplayExpression(expression)
        return question(
            "simplification", "Simplify: $expression", value.toString(),
            "First compute $c ÷ $divisor = $quotient, then $a × $b = ${a * b}. Total = $value.",
            difficulty, "Division and multiplication are done before addition."
        )
    }

    private fun fractions(difficulty: Difficulty): Question {
        val operation = listOf("+", "-", "×", "÷").pick()
        val first = Fraction.of(rnd(1, 12).toLong(), rnd(2, 12).toLong())
        var second = Fraction.of(rnd(1, 12).toLong(), rnd(2, 12).toLong())
        if (operation == "÷" && second.numerator == 0L) second = Fraction.of(3L, 4L)
        val result = when (operation) {
            "+" -> first + second
            "-" -> first - second
            "×" -> first * second
            else -> first / second
        }
        return question(
            "fractions", "Find the value: $first $operation $second", result.toString(),
            "$first $operation $second = $result. The result is reduced to its lowest terms.",
            difficulty, "Convert to a common denominator for addition and subtraction."
        )
    }

    private fun decimals(difficulty: Difficulty): Question {
        val scale = if (difficulty == Difficulty.EASY) 1 else 2
        var first = BigDecimal.valueOf(rnd(10, 9999).toLong()).movePointLeft(scale)
        var second = BigDecimal.valueOf(rnd(10, 9999).toLong()).movePointLeft(scale)
        val operation = listOf("+", "-").pick()
        if (operation == "-" && second > first) {
            val temporary = first
            first = second
            second = temporary
        }
        val result = if (operation == "+") first.add(second) else first.subtract(second)
        val answer = result.stripTrailingZeros().toPlainString()
        return question(
            "decimals", "Find the value: ${first.toPlainString()} $operation ${second.toPlainString()}",
            answer,
            "${first.toPlainString()} $operation ${second.toPlainString()} = $answer.",
            difficulty, "Align the decimal points before calculating."
        )
    }

    private fun percentage(difficulty: Difficulty): Question {
        return when (rnd(0, 2)) {
            0 -> {
                val percent = listOf(5, 10, 15, 20, 25, 30, 40, 50, 75).pick()
                val base = rnd(20, 600)
                val answer = scale(base.toLong() * percent.toLong())
                question(
                    "percentage", "What is $percent% of $base?", answer,
                    "$percent% of $base = $base × $percent ÷ 100 = $answer.", difficulty,
                    "Percent means per hundred."
                )
            }
            1 -> {
                val percent = listOf(10, 20, 25, 50, 75).pick()
                val base = rnd(1, 12) * 100
                val part = base * percent / 100
                question(
                    "percentage", "$part is what percent of $base?", percent.toString(),
                    "$part ÷ $base × 100 = $percent%.", difficulty,
                    "Divide the part by the whole and multiply by 100."
                )
            }
            else -> {
                val percent = listOf(10, 20, 25, 50).pick()
                val base = rnd(2, 20) * 100
                val increase = base * percent / 100
                question(
                    "percentage", "Increase $base by $percent%. What is the new value?",
                    (base + increase).toString(),
                    "Increase = $base × $percent ÷ 100 = $increase. New value = $base + $increase = ${base + increase}.",
                    difficulty, "Find the increase first, then add it to the original."
                )
            }
        }
    }

    private fun squares(difficulty: Difficulty): Question {
        val limit = when (difficulty) {
            Difficulty.EASY -> 15
            Difficulty.MEDIUM -> 35
            else -> 75
        }
        val number = rnd(2, limit)
        return question(
            "squares", "What is the square of $number?", (number.toLong() * number.toLong()).toString(),
            "$number² = $number × $number = ${number.toLong() * number.toLong()}.", difficulty,
            "Use (a + b)² to split difficult squares."
        )
    }

    private fun squareRoots(difficulty: Difficulty): Question {
        val limit = when (difficulty) {
            Difficulty.EASY -> 15
            Difficulty.MEDIUM -> 35
            else -> 60
        }
        val root = rnd(2, limit)
        val square = root.toLong() * root.toLong()
        return question(
            "square_roots", "What is the square root of $square?", root.toString(),
            "√$square = $root because $root × $root = $square.", difficulty,
            "Look for a number that multiplies by itself to give the radicand."
        )
    }

    private fun cubes(difficulty: Difficulty): Question {
        val limit = if (difficulty == Difficulty.EASY) 10 else 25
        val number = rnd(2, limit)
        val cube = number.toLong() * number.toLong() * number.toLong()
        return question(
            "cubes", "What is the cube of $number?", cube.toString(),
            "$number³ = $number × $number × $number = $cube.", difficulty,
            "Cube means multiplying the number by itself three times."
        )
    }

    private fun cubeRoots(difficulty: Difficulty): Question {
        val limit = if (difficulty == Difficulty.EASY) 10 else 25
        val root = rnd(2, limit)
        val cube = root.toLong() * root.toLong() * root.toLong()
        return question(
            "cube_roots", "What is the cube root of $cube?", root.toString(),
            "∛$cube = $root because $root³ = $cube.", difficulty,
            "Recall the cubes of small numbers first."
        )
    }

    private fun lcmHcf(difficulty: Difficulty): Question {
        val limit = when (difficulty) {
            Difficulty.EASY -> 20
            Difficulty.MEDIUM -> 60
            else -> 150
        }
        val first = rnd(2, limit).toLong()
        val second = rnd(2, limit).toLong()
        val hcf = Fraction.gcd(first, second)
        val lcm = Fraction.lcm(first, second)
        return if (random.nextBoolean()) {
            question(
                "lcm_hcf", "Find the HCF of $first and $second", hcf.toString(),
                "The highest common factor of $first and $second is $hcf.", difficulty,
                "List factors of both numbers."
            )
        } else {
            question(
                "lcm_hcf", "Find the LCM of $first and $second", lcm.toString(),
                "LCM = ($first × $second) ÷ HCF = $lcm.", difficulty,
                "LCM × HCF = product of the two numbers."
            )
        }
    }

    private fun ratioProportion(difficulty: Difficulty): Question {
        val a = rnd(1, 9)
        val multiplier = rnd(2, 9)
        val b = a * multiplier
        val c = rnd(2, 12)
        val answer = multiplier * c
        return question(
            "ratio_proportion", "$a : $b = $c : ?", answer.toString(),
            "Since $b = $a × $multiplier, the fourth term is $c × $multiplier = $answer.",
            difficulty, "Use cross multiplication: a × d = b × c."
        )
    }

    private fun average(difficulty: Difficulty): Question {
        val count = when (difficulty) {
            Difficulty.EASY -> rnd(3, 5)
            Difficulty.MEDIUM -> rnd(5, 8)
            else -> rnd(8, 12)
        }
        val mean = rnd(5, 100)
        val values = mutableListOf<Int>()
        repeat(count - 1) { values.add(mean + rnd(-5, 5)) }
        var last = mean * count - values.sum()
        if (last < 0) {
            values.clear()
            repeat(count - 1) { values.add(mean) }
            last = mean
        }
        values.add(last)
        return question(
            "average", "Find the average of ${values.joinToString(", ")}", mean.toString(),
            "Sum = ${values.sum()} and there are $count values. Average = ${values.sum()} ÷ $count = $mean.",
            difficulty, "Average equals total sum divided by the number of values."
        )
    }

    private fun numberSystem(difficulty: Difficulty): Question {
        return when (rnd(0, 3)) {
            0 -> {
                val number = rnd(100, 9999)
                val divisor = rnd(2, 20)
                question(
                    "number_system", "Find the remainder when $number is divided by $divisor",
                    (number % divisor).toString(),
                    "$number = $divisor × ${number / divisor} + ${number % divisor}, so the remainder is ${number % divisor}.",
                    difficulty, "Remainder is always smaller than the divisor."
                )
            }
            1 -> {
                val number = rnd(100, 99999)
                question(
                    "number_system", "Find the sum of the digits of $number", digits(number).toString(),
                    "Digits: ${number.toString().toList().joinToString(" + ")} = ${digits(number)}.",
                    difficulty, "Add each digit separately."
                )
            }
            2 -> {
                val start = rnd(2, 50)
                val end = start + rnd(10, 40)
                var count = 0
                for (value in start..end) if (isPrime(value)) count++
                question(
                    "number_system", "How many prime numbers are there from $start to $end?",
                    count.toString(), "Counting primes between $start and $end gives $count.",
                    difficulty, "A prime number has exactly two factors."
                )
            }
            else -> {
                val divisor = rnd(3, 25)
                val smallest = ((100 + divisor - 1) / divisor) * divisor
                question(
                    "number_system", "What is the smallest three-digit number divisible by $divisor?",
                    smallest.toString(), "The first multiple of $divisor at or above 100 is $smallest.",
                    difficulty, "Divide 100 by the divisor and round up."
                )
            }
        }
    }

    private fun divisibleBy(value: Int, divisor: Int): Boolean = when (divisor) {
        2 -> value % 2 == 0
        3 -> digits(value) % 3 == 0
        4 -> value % 100 % 4 == 0
        5 -> value % 10 == 0 || value % 10 == 5
        6 -> value % 2 == 0 && digits(value) % 3 == 0
        8 -> value % 1000 % 8 == 0
        9 -> digits(value) % 9 == 0
        10 -> value % 10 == 0
        11 -> {
            val text = value.toString()
            var alternating = 0
            for (index in text.indices) {
                val digit = text[index] - '0'
                alternating += if (index % 2 == 0) digit else -digit
            }
            alternating % 11 == 0
        }
        else -> value % divisor == 0
    }

    private fun divisibility(difficulty: Difficulty): Question {
        val divisor = listOf(2, 3, 4, 5, 6, 8, 9, 10, 11).pick()
        val number = rnd(100, 99999)
        val answer = if (divisibleBy(number, divisor)) "yes" else "no"
        return question(
            "divisibility", "Is $number divisible by $divisor? Answer yes or no.", answer,
            "Using the divisibility rule for $divisor, $number is ${if (answer == "yes") "divisible" else "not divisible"} by $divisor.",
            difficulty, "Apply the specific rule for $divisor instead of full division."
        )
    }

    private fun factorsMultiples(difficulty: Difficulty): Question {
        return if (random.nextBoolean()) {
            val number = rnd(12, if (difficulty == Difficulty.EASY) 60 else 400)
            val count = countFactors(number)
            question(
                "factors_multiples", "How many factors does $number have?", count.toString(),
                "$number has $count positive factors.", difficulty,
                "Check every divisor from 1 up to the square root in pairs."
            )
        } else {
            val base = rnd(3, 25)
            val index = rnd(4, 20)
            question(
                "factors_multiples", "What is the $index-th multiple of $base?", (base * index).toString(),
                "$base × $index = ${base * index}.", difficulty,
                "The nth multiple of a number is the number multiplied by n."
            )
        }
    }

    private fun table(number: Int, difficulty: Difficulty, variant: String): Question {
        val multiplier = when (variant) {
            "forward" -> rnd(1, 20)
            "backward" -> rnd(1, 20)
            else -> rnd(1, 20)
        }
        val answer = number.toLong() * multiplier.toLong()
        return question(
            "table_$number", "$number × $multiplier = ?", answer.toString(),
            "$number multiplied by $multiplier equals $answer.", difficulty,
            "Build the answer from the base table of ${if (number > 10) number / 10 else 1}.",
            listOf("table_$number", variant)
        )
    }

    private fun mentalChallenge(difficulty: Difficulty): Question {
        val source = when (rnd(0, 5)) {
            0 -> addition(Difficulty.EASY)
            1 -> subtraction(Difficulty.EASY)
            2 -> multiplication(Difficulty.EASY)
            3 -> division(Difficulty.EASY)
            4 -> squares(Difficulty.EASY)
            else -> percentage(Difficulty.EASY)
        }
        return retag(source, TopicCatalog.find("speed_mental"))
    }

    private fun ibpsSimplification(difficulty: Difficulty): Question {
        val percent = listOf(10, 20, 25, 50).pick()
        val base = rnd(2, 20) * 10
        val part = base * percent / 100
        val a = rnd(5, 30)
        val b = rnd(3, 15)
        val c = rnd(10, 60)
        val expression = "$percent% of $base + $a × $b - $c"
        val value = part + a * b - c
        return question(
            "ibps_simplification", "Simplify: $expression", value.toString(),
            "$percent% of $base = $part, $a × $b = ${a * b}. So $part + ${a * b} - $c = $value.",
            difficulty, "Handle the percentage first, then apply BODMAS."
        )
    }

    private fun roundToTen(value: Int): Int = ((value + 5) / 10) * 10

    private fun ibpsApproximation(difficulty: Difficulty): Question {
        val operation = listOf("+", "-", "×").pick()
        val first = rnd(31, 199)
        val second = rnd(21, 189)
        val roundedFirst = roundToTen(first)
        val roundedSecond = roundToTen(second)
        val answer = when (operation) {
            "+" -> roundedFirst + roundedSecond
            "-" -> roundedFirst - roundedSecond
            else -> roundedFirst.toLong() * roundedSecond.toLong()
        }.toLong()
        return question(
            "ibps_approximation",
            "Approximate $first $operation $second by rounding each number to the nearest ten.",
            answer.toString(),
            "$first → $roundedFirst and $second → $roundedSecond. $roundedFirst $operation $roundedSecond = $answer.",
            difficulty, "Round first, then calculate."
        )
    }

    private fun ibpsNumberSeries(difficulty: Difficulty): Question {
        val type = rnd(0, 3)
        val terms: List<Long>
        val next: Long
        when (type) {
            0 -> {
                val start = rnd(2, 25).toLong()
                val difference = rnd(2, 15).toLong()
                terms = (0 until 5).map { start + difference * it }
                next = start + difference * 5
            }
            1 -> {
                val start = rnd(1, 20).toLong()
                terms = (0 until 5).map { start * (1L shl it) }
                next = start * 32L
            }
            2 -> {
                val start = rnd(1, 20).toLong()
                val step = rnd(2, 6).toLong()
                val generated = mutableListOf(start)
                var current = start
                for (index in 1 until 5) {
                    current += step + index.toLong()
                    generated.add(current)
                }
                terms = generated
                next = current + step + 5L
            }
            else -> {
                val start = rnd(2, 8)
                terms = (0 until 5).map { (start + it).toLong() * (start + it).toLong() }
                next = (start + 5).toLong() * (start + 5).toLong()
            }
        }
        return question(
            "ibps_number_series", "Find the next term: ${terms.joinToString(", ")}, ?", next.toString(),
            "The next term in this series is $next.", difficulty,
            "Check differences between consecutive terms first."
        )
    }

    private fun ibpsQuadratic(difficulty: Difficulty): Question {
        val firstRoot = rnd(-9, 12)
        var secondRoot = rnd(-9, 12)
        if (firstRoot == secondRoot) secondRoot += 2
        val sum = firstRoot + secondRoot
        val product = firstRoot * secondRoot
        val sumPart = if (sum >= 0) "- $sum" else "+ ${-sum}"
        val productPart = if (product >= 0) "+ $product" else "- ${-product}"
        val equation = "x² $sumPart x $productPart = 0"
        return when (rnd(0, 2)) {
            0 -> question(
                "ibps_quadratic", "For $equation what is the larger root?",
                maxOf(firstRoot, secondRoot).toString(),
                "The roots are $firstRoot and $secondRoot, so the larger root is ${maxOf(firstRoot, secondRoot)}.",
                difficulty, "Factor the equation into (x - r1)(x - r2)."
            )
            1 -> question(
                "ibps_quadratic", "For $equation find the sum of the roots.", sum.toString(),
                "For ax² + bx + c, the sum of roots is -b/a = $sum.", difficulty,
                "Use the coefficient relationship instead of solving fully."
            )
            else -> question(
                "ibps_quadratic", "For $equation find the product of the roots.", product.toString(),
                "For ax² + bx + c, the product of roots is c/a = $product.", difficulty,
                "Product of roots equals the constant term when a = 1."
            )
        }
    }

    private fun ibpsPercentage(difficulty: Difficulty): Question {
        return when (rnd(0, 2)) {
            0 -> {
                val percent = listOf(12, 15, 18, 24, 35, 45).pick()
                val base = rnd(2, 40) * 100
                val answer = base.toLong() * percent.toLong() / 100L
                question(
                    "ibps_percentage", "Find $percent% of $base.", answer.toString(),
                    "$base × $percent ÷ 100 = $answer.", difficulty,
                    "Break the percent into 10% and smaller parts."
                )
            }
            1 -> {
                val salary = rnd(10, 80) * 1000
                val percent = listOf(10, 20, 25).pick()
                val increased = salary + salary * percent / 100
                question(
                    "ibps_percentage", "A salary of $salary is increased by $percent%. Find the new salary.",
                    increased.toString(), "Increase = $salary × $percent ÷ 100 = ${salary * percent / 100}. New salary = $increased.",
                    difficulty, "Find the increment, then add it."
                )
            }
            else -> {
                val original = rnd(2, 20) * 100
                val percent = listOf(10, 20, 25).pick()
                val decreased = original - original * percent / 100
                question(
                    "ibps_percentage", "A number $original is decreased by $percent%. Find the result.",
                    decreased.toString(), "Decrease = $original × $percent ÷ 100 = ${original * percent / 100}. Result = $decreased.",
                    difficulty, "Percent decrease is subtracted from the original."
                )
            }
        }
    }

    private fun ibpsProfitLoss(difficulty: Difficulty): Question {
        val cost = rnd(2, 40) * 100
        val percent = listOf(5, 10, 15, 20, 25, 30).pick()
        val amount = cost * percent / 100
        return when (rnd(0, 2)) {
            0 -> {
                val selling = cost + amount
                question(
                    "ibps_profit_loss", "An article costs $cost and is sold at a $percent% profit. Find the selling price.",
                    selling.toString(), "Profit = $cost × $percent ÷ 100 = $amount. Selling price = $cost + $amount = $selling.",
                    difficulty, "Selling price = cost price plus profit."
                )
            }
            1 -> {
                val selling = cost - amount
                question(
                    "ibps_profit_loss", "An article costs $cost and is sold at a $percent% loss. Find the selling price.",
                    selling.toString(), "Loss = $cost × $percent ÷ 100 = $amount. Selling price = $cost - $amount = $selling.",
                    difficulty, "Selling price = cost price minus loss."
                )
            }
            else -> {
                val selling = cost + amount
                question(
                    "ibps_profit_loss", "An article bought for $cost is sold for $selling. Find the profit percent.",
                    percent.toString(), "Profit = $selling - $cost = $amount. Profit percent = $amount ÷ $cost × 100 = $percent%.",
                    difficulty, "Profit percent is profit divided by cost price times 100."
                )
            }
        }
    }

    private fun ibpsInterest(difficulty: Difficulty): Question {
        val principal = rnd(5, 50) * 100
        return when (rnd(0, 1)) {
            0 -> {
                val rate = listOf(5, 10, 15, 20).pick()
                val time = rnd(1, 6)
                val interest = principal.toLong() * rate.toLong() * time.toLong() / 100L
                question(
                    "ibps_si_ci", "Find the simple interest on $principal at $rate% per year for $time years.",
                    interest.toString(), "SI = P × R × T ÷ 100 = $principal × $rate × $time ÷ 100 = $interest.",
                    difficulty, "Use SI = PRT/100."
                )
            }
            else -> {
                val rate = listOf(10, 20).pick()
                val growth = if (rate == 10) 121L to 100L else 36L to 25L
                val amount = principal.toLong() / growth.second * growth.first
                val interest = amount - principal
                question(
                    "ibps_si_ci", "Find the compound interest on $principal at $rate% per year for 2 years, compounded annually.",
                    interest.toString(),
                    "Amount = $principal × (1 + $rate/100)² = $amount. Compound interest = $amount - $principal = $interest.",
                    difficulty, "CI for two years uses the square of the growth factor."
                )
            }
        }
    }

    private fun ibpsRatio(difficulty: Difficulty): Question {
        val first = rnd(1, 9)
        val second = rnd(1, 9)
        val multiplier = rnd(5, 40)
        val total = (first + second) * multiplier
        return question(
            "ibps_ratio", "Divide $total in the ratio $first : $second. Find the first part.",
            (first * multiplier).toString(),
            "Sum of ratio terms = ${first + second}. One part = $total ÷ ${first + second} = $multiplier. First part = $first × $multiplier = ${first * multiplier}.",
            difficulty, "Divide the total by the sum of ratio terms."
        )
    }

    private fun ibpsAverage(difficulty: Difficulty): Question {
        return when (rnd(0, 1)) {
            0 -> {
                val count = rnd(4, 10)
                val mean = rnd(10, 90)
                val values = mutableListOf<Int>()
                repeat(count - 1) { values.add(mean + rnd(-6, 6)) }
                val last = mean * count - values.sum()
                values.add(last)
                question(
                    "ibps_average", "Find the average of ${values.joinToString(", ")}", mean.toString(),
                    "Sum = ${values.sum()} ÷ $count values = $mean.", difficulty,
                    "Add everything before dividing by the count."
                )
            }
            else -> {
                val firstCount = rnd(2, 8)
                val secondCount = rnd(2, 8)
                val firstAvg = rnd(10, 60)
                val secondAvg = rnd(10, 60)
                val total = firstCount * firstAvg + secondCount * secondAvg
                val combined = total / (firstCount + secondCount)
                val answer = decimalText(total.toLong(), firstCount + secondCount)
                question(
                    "ibps_average",
                    "$firstCount numbers have average $firstAvg and $secondCount numbers have average $secondAvg. Find the combined average.",
                    answer,
                    "Weighted sum = ${firstCount * firstAvg} + ${secondCount * secondAvg} = $total. Combined average = $total ÷ ${firstCount + secondCount} = $answer.",
                    difficulty, "Use a weighted average, not the average of averages."
                )
            }
        }
    }

    private fun ibpsPartnership(difficulty: Difficulty): Question {
        val first = rnd(1, 7)
        val second = rnd(1, 7)
        val multiplier = rnd(5, 30)
        val profit = (first + second) * multiplier
        return question(
            "ibps_partnership",
            "A and B invest in the ratio $first : $second. If the total profit is $profit, find A's share.",
            (first * multiplier).toString(),
            "Total ratio = ${first + second}. Share per unit = $profit ÷ ${first + second} = $multiplier. A's share = $first × $multiplier = ${first * multiplier}.",
            difficulty, "Profit is shared in the investment ratio when time is equal."
        )
    }

    private fun ibpsTimeWork(difficulty: Difficulty): Question {
        val pairs = listOf(6 to 12, 4 to 12, 10 to 15, 12 to 24, 8 to 24, 15 to 30, 20 to 30)
        val (first, second) = pairs.pick()
        val days = first * second / (first + second)
        return question(
            "ibps_time_work", "A can finish a work in $first days and B in $second days. In how many days can they finish it together?",
            days.toString(),
            "Combined rate = 1/$first + 1/$second = ${first + second}/$first$second. Time = $first$second/${first + second} = $days days.",
            difficulty, "Add work rates, not the days."
        )
    }

    private fun ibpsPipes(difficulty: Difficulty): Question {
        val pairs = listOf(10 to 15, 12 to 24, 6 to 12, 20 to 30, 8 to 12)
        val (fill, empty) = pairs.pick()
        val time = fill * empty / (empty - fill)
        return question(
            "ibps_pipes", "A pipe fills a tank in $fill minutes and another empties it in $empty minutes. If both are open, how long to fill the tank?",
            time.toString(),
            "Net rate = 1/$fill - 1/$empty = ${empty - fill}/$fill$empty. Time = $fill$empty/${empty - fill} = $time minutes.",
            difficulty, "Subtract the emptying rate from the filling rate."
        )
    }

    private fun ibpsSpeedDistance(difficulty: Difficulty): Question {
        val speed = rnd(20, 100)
        val hours = rnd(1, 12)
        val distance = speed * hours
        return when (rnd(0, 2)) {
            0 -> question(
                "ibps_speed_distance", "A vehicle travels at $speed km/h for $hours hours. Find the distance covered in km.",
                distance.toString(), "Distance = speed × time = $speed × $hours = $distance km.", difficulty,
                "Keep the units consistent."
            )
            1 -> question(
                "ibps_speed_distance", "A vehicle covers $distance km in $hours hours. Find its speed in km/h.",
                speed.toString(), "Speed = distance ÷ time = $distance ÷ $hours = $speed km/h.", difficulty,
                "Speed is distance divided by time."
            )
            else -> question(
                "ibps_speed_distance", "A vehicle covers $distance km at $speed km/h. Find the time taken in hours.",
                hours.toString(), "Time = distance ÷ speed = $distance ÷ $speed = $hours hours.", difficulty,
                "Time is distance divided by speed."
            )
        }
    }

    private fun ibpsTrains(difficulty: Difficulty): Question {
        val speedKmph = listOf(18, 36, 54, 72).pick()
        val speedMs = speedKmph * 5 / 18
        val length = speedMs * rnd(5, 20)
        return if (random.nextBoolean()) {
            question(
                "ibps_trains", "A train of length $length m runs at $speedKmph km/h. How many seconds does it take to cross a pole?",
                (length / speedMs).toString(),
                "Speed = $speedKmph × 5/18 = $speedMs m/s. Time = $length ÷ $speedMs = ${length / speedMs} seconds.",
                difficulty, "Convert km/h to m/s before calculating."
            )
        } else {
            val platform = speedMs * rnd(5, 20)
            question(
                "ibps_trains", "A train of length $length m runs at $speedKmph km/h. How long does it take to cross a ${platform} m platform?",
                ((length + platform) / speedMs).toString(),
                "Speed = $speedMs m/s. Total distance = $length + $platform = ${length + platform} m. Time = ${(length + platform) / speedMs} seconds.",
                difficulty, "Add the train length and platform length."
            )
        }
    }

    private fun ibpsBoats(difficulty: Difficulty): Question {
        val still = rnd(5, 30)
        val stream = rnd(1, still - 1)
        val downstream = still + stream
        val upstream = still - stream
        return if (random.nextBoolean()) {
            question(
                "ibps_boats", "A boat travels downstream at $downstream km/h and upstream at $upstream km/h. Find the speed in still water.",
                still.toString(),
                "Still water speed = (downstream + upstream) ÷ 2 = ($downstream + $upstream) ÷ 2 = $still km/h.",
                difficulty, "Average the downstream and upstream speeds."
            )
        } else {
            question(
                "ibps_boats", "A boat travels downstream at $downstream km/h and upstream at $upstream km/h. Find the speed of the stream.",
                stream.toString(),
                "Stream speed = (downstream - upstream) ÷ 2 = ($downstream - $upstream) ÷ 2 = $stream km/h.",
                difficulty, "Half the difference gives the stream speed."
            )
        }
    }

    private fun ibpsMixture(difficulty: Difficulty): Question {
        val firstQuantity = rnd(2, 12) * 10
        val secondQuantity = rnd(2, 12) * 10
        val firstPrice = rnd(10, 60)
        val secondPrice = rnd(10, 60)
        val numerator = firstQuantity.toLong() * firstPrice.toLong() + secondQuantity.toLong() * secondPrice.toLong()
        val denominator = (firstQuantity + secondQuantity).toLong()
        val answer = Fraction.of(numerator, denominator).toString()
        return question(
            "ibps_mixture",
            "$firstQuantity kg of a variety costing $firstPrice per kg is mixed with $secondQuantity kg costing $secondPrice per kg. Find the average cost per kg.",
            answer,
            "Average cost = ($firstQuantity × $firstPrice + $secondQuantity × $secondPrice) ÷ ${firstQuantity + secondQuantity} = $answer.",
            difficulty, "Use the weighted average of the two prices."
        )
    }

    private fun ibpsAges(difficulty: Difficulty): Question {
        val younger = rnd(8, 45)
        val difference = rnd(2, 20)
        val older = younger + difference
        val sum = younger + older
        return question(
            "ibps_ages", "A is $difference years older than B. The sum of their ages is $sum. Find A's age.",
            older.toString(),
            "Let B = x. Then A = x + $difference and 2x + $difference = $sum, so x = $younger and A = $older years.",
            difficulty, "Form an equation using the age difference."
        )
    }

    private fun ibpsProbability(difficulty: Difficulty): Question {
        val red = rnd(2, 12)
        val blue = rnd(2, 12)
        val probability = Fraction.of(red.toLong(), (red + blue).toLong())
        return question(
            "ibps_probability", "A bag has $red red and $blue blue balls. One ball is drawn at random. What is the probability that it is red?",
            probability.toString(),
            "Probability = favourable ÷ total = $red ÷ ${red + blue} = $probability.",
            difficulty, "Express the probability in lowest terms."
        )
    }

    private fun ibpsPermutation(difficulty: Difficulty): Question {
        val n = rnd(5, 9)
        val r = rnd(2, 4).coerceAtMost(n)
        return if (random.nextBoolean()) {
            val answer = nCr(n, r)
            question(
                "ibps_permutation", "Find the value of ${n}C${r}.", answer.toString(),
                "${n}C${r} = $n! / ($r! × ${n - r}!) = $answer.", difficulty,
                "Combinations ignore order."
            )
        } else {
            val answer = nPr(n, r)
            question(
                "ibps_permutation", "Find the value of ${n}P${r}.", answer.toString(),
                "${n}P${r} = $n! / ${n - r}! = $answer.", difficulty,
                "Permutations count ordered arrangements."
            )
        }
    }

    private fun ibpsDataInterpretation(difficulty: Difficulty): Question {
        fun value(): Int = rnd(2, 20) * 3
        val a = listOf(value(), value(), value())
        val b = listOf(value(), value(), value())
        val c = listOf(value(), value(), value())
        val table = buildString {
            append("Sales in units:\n")
            append("Company A: ${a.joinToString(", ")}\n")
            append("Company B: ${b.joinToString(", ")}\n")
            append("Company C: ${c.joinToString(", ")}\n")
            append("Columns are Year 1, Year 2, Year 3.")
        }
        return when (rnd(0, 3)) {
            0 -> {
                val company = listOf("A" to a, "B" to b, "C" to c).pick()
                val total = company.second.sum()
                question(
                    "ibps_di", "$table\nWhat is the total sales of Company ${company.first} over all three years?",
                    total.toString(), "Add the three yearly values of Company ${company.first}: $total units.", difficulty,
                    "Read the correct company row before adding."
                )
            }
            1 -> {
                val index = rnd(0, 2)
                val total = a[index] + b[index] + c[index]
                question(
                    "ibps_di", "$table\nWhat is the total sales of all companies in Year ${index + 1}?",
                    total.toString(), "Add the Year ${index + 1} column: $total units.", difficulty,
                    "Read the correct year column."
                )
            }
            2 -> {
                val first = listOf("A" to a, "B" to b, "C" to c).pick()
                val second = listOf("A" to a, "B" to b, "C" to c).filter { it.first != first.first }.pick()
                val difference = kotlin.math.abs(first.second.sum() - second.second.sum())
                question(
                    "ibps_di", "$table\nWhat is the difference between the total sales of Company ${first.first} and Company ${second.first}?",
                    difference.toString(), "Totals are ${first.second.sum()} and ${second.second.sum()}. Difference = $difference units.",
                    difficulty, "Compute both totals before subtracting."
                )
            }
            else -> {
                val company = listOf("A" to a, "B" to b, "C" to c).pick()
                val answer = company.second.sum() / 3
                question(
                    "ibps_di", "$table\nWhat is the average yearly sales of Company ${company.first}?",
                    answer.toString(), "Total = ${company.second.sum()}, average = ${company.second.sum()} ÷ 3 = $answer units.",
                    difficulty, "Add the row, then divide by three."
                )
            }
        }
    }
}
