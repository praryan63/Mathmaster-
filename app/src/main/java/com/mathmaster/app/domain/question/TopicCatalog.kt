package com.mathmaster.app.domain.question

import com.mathmaster.app.domain.model.Topic
import com.mathmaster.app.domain.model.TopicCategory

object TopicCatalog {

    val basic: List<Topic> = listOf(
        Topic("addition", "Addition", TopicCategory.BASIC, "Add numbers with confidence"),
        Topic("subtraction", "Subtraction", TopicCategory.BASIC, "Find the difference accurately"),
        Topic("multiplication", "Multiplication", TopicCategory.BASIC, "Multiply faster and cleaner"),
        Topic("division", "Division", TopicCategory.BASIC, "Split numbers into equal parts"),
        Topic("bodmas", "BODMAS", TopicCategory.BASIC, "Order of operations"),
        Topic("simplification", "Simplification", TopicCategory.BASIC, "Simplify arithmetic expressions"),
        Topic("fractions", "Fractions", TopicCategory.BASIC, "Add, subtract, multiply and divide fractions"),
        Topic("decimals", "Decimals", TopicCategory.BASIC, "Work with decimal numbers"),
        Topic("percentage", "Percentage", TopicCategory.BASIC, "Percent of a quantity and changes"),
        Topic("squares", "Squares", TopicCategory.BASIC, "Find squares of numbers"),
        Topic("square_roots", "Square Roots", TopicCategory.BASIC, "Find perfect square roots"),
        Topic("cubes", "Cubes", TopicCategory.BASIC, "Find cubes of numbers"),
        Topic("cube_roots", "Cube Roots", TopicCategory.BASIC, "Find perfect cube roots"),
        Topic("lcm_hcf", "LCM and HCF", TopicCategory.BASIC, "Least multiple and highest common factor"),
        Topic("ratio_proportion", "Ratio and Proportion", TopicCategory.BASIC, "Compare and split quantities"),
        Topic("average", "Average", TopicCategory.BASIC, "Find the mean of a data set"),
        Topic("number_system", "Number System", TopicCategory.BASIC, "Digits, remainders and primes"),
        Topic("divisibility", "Divisibility Rules", TopicCategory.BASIC, "Test divisibility without long division"),
        Topic("factors_multiples", "Factors and Multiples", TopicCategory.BASIC, "Count factors and find multiples")
    )

    val ibps: List<Topic> = listOf(
        Topic("ibps_simplification", "Simplification", TopicCategory.IBPS, "Fast arithmetic for exams"),
        Topic("ibps_approximation", "Approximation", TopicCategory.IBPS, "Estimate answers quickly"),
        Topic("ibps_number_series", "Number Series", TopicCategory.IBPS, "Find the missing logic"),
        Topic("ibps_quadratic", "Quadratic Equations", TopicCategory.IBPS, "Roots and relationships"),
        Topic("ibps_percentage", "Percentage", TopicCategory.IBPS, "Exam-level percentage problems"),
        Topic("ibps_profit_loss", "Profit and Loss", TopicCategory.IBPS, "Cost, selling price and profit"),
        Topic("ibps_si_ci", "SI and CI", TopicCategory.IBPS, "Simple and compound interest"),
        Topic("ibps_ratio", "Ratio and Proportion", TopicCategory.IBPS, "Ratio based word problems"),
        Topic("ibps_average", "Average", TopicCategory.IBPS, "Group averages and weighted means"),
        Topic("ibps_partnership", "Partnership", TopicCategory.IBPS, "Share profit by ratio"),
        Topic("ibps_time_work", "Time and Work", TopicCategory.IBPS, "Combined work rates"),
        Topic("ibps_pipes", "Pipes and Cisterns", TopicCategory.IBPS, "Filling and draining rates"),
        Topic("ibps_speed_distance", "Speed, Time and Distance", TopicCategory.IBPS, "Core motion problems"),
        Topic("ibps_trains", "Trains", TopicCategory.IBPS, "Crossing poles and platforms"),
        Topic("ibps_boats", "Boats and Streams", TopicCategory.IBPS, "Upstream and downstream"),
        Topic("ibps_mixture", "Mixture and Alligation", TopicCategory.IBPS, "Blend and average price"),
        Topic("ibps_ages", "Ages", TopicCategory.IBPS, "Age relationships"),
        Topic("ibps_probability", "Probability", TopicCategory.IBPS, "Simple events as fractions"),
        Topic("ibps_permutation", "Permutation and Combination", TopicCategory.IBPS, "Counting arrangements"),
        Topic("ibps_di", "Data Interpretation", TopicCategory.IBPS, "Read tables and compute")
    )

    val speed: List<Topic> = listOf(
        Topic("speed_add", "Fast Addition", TopicCategory.SPEED, "Two-digit sums under pressure"),
        Topic("speed_sub", "Fast Subtraction", TopicCategory.SPEED, "Quick differences"),
        Topic("speed_mul", "Multiplication Shortcuts", TopicCategory.SPEED, "Multiply without writing"),
        Topic("speed_div", "Division Shortcuts", TopicCategory.SPEED, "Divide exactly and fast"),
        Topic("speed_percentage", "Percentage Calculations", TopicCategory.SPEED, "Common percent values"),
        Topic("speed_squares", "Squares", TopicCategory.SPEED, "Square numbers mentally"),
        Topic("speed_cubes", "Cubes", TopicCategory.SPEED, "Recall small cubes"),
        Topic("speed_approximation", "Approximation", TopicCategory.SPEED, "Round and compute"),
        Topic("speed_mental", "Mental Maths Challenge", TopicCategory.SPEED, "Mixed rapid-fire questions")
    )

    val tableTopics: List<Topic> = (1..50).map { n ->
        Topic("table_$n", "Table of $n", TopicCategory.TABLES, "Multiplication table from 1 to 20")
    }

    val tablePracticeModes: List<Topic> = listOf(
        Topic("tables_mixed", "Mixed Tables", TopicCategory.TABLES, "Tables 1 to 50 in random order"),
        Topic("tables_forward", "Forward Practice", TopicCategory.TABLES, "Multipliers from 1 to 20"),
        Topic("tables_backward", "Backward Practice", TopicCategory.TABLES, "Multipliers from 20 to 1"),
        Topic("tables_random", "Random Order", TopicCategory.TABLES, "Random table and multiplier"),
        Topic("tables_weak", "Weak Tables", TopicCategory.TABLES, "Practice only your weak tables")
    )

    val mixed: Topic = Topic("mixed", "Mixed Practice", TopicCategory.BASIC, "Questions from every topic")

    val all: List<Topic> = basic + tableTopics + tablePracticeModes + ibps + speed + listOf(mixed)

    private val byId: Map<String, Topic> = all.associateBy { it.id }

    fun find(id: String): Topic = byId[id] ?: mixed

    fun nameOf(id: String): String = find(id).name

    fun isTableTopic(id: String): Boolean = id.startsWith("table_")

    fun tableNumber(id: String): Int? =
        if (isTableTopic(id)) id.removePrefix("table_").toIntOrNull() else null
}
