package com.mathmaster.app.domain.question

object ExpressionEvaluator {

    private val tokenRegex = Regex("\\d+|\\+|\\-|\\*|/|\\(|\\)")

    fun evaluate(expression: String): Long {
        val compact = expression.replace(" ", "")
        require(compact.isNotEmpty()) { "Empty expression" }
        val tokens = tokenRegex.findAll(compact).map { it.value }.toList()
        require(tokens.joinToString("") == compact) { "Unsupported character in expression" }

        val output = mutableListOf<String>()
        val operators = ArrayDeque<String>()
        val precedence = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2)

        for (token in tokens) {
            when {
                token[0].isDigit() -> output.add(token)
                token == "(" -> operators.addLast(token)
                token == ")" -> {
                    while (operators.isNotEmpty() && operators.last() != "(") {
                        output.add(operators.removeLast())
                    }
                    require(operators.isNotEmpty()) { "Unbalanced brackets" }
                    operators.removeLast()
                }
                else -> {
                    while (operators.isNotEmpty() && operators.last() != "(" &&
                        (precedence[operators.last()] ?: 0) >= (precedence[token] ?: 0)
                    ) {
                        output.add(operators.removeLast())
                    }
                    operators.addLast(token)
                }
            }
        }
        while (operators.isNotEmpty()) {
            val op = operators.removeLast()
            require(op != "(") { "Unbalanced brackets" }
            output.add(op)
        }

        val stack = ArrayDeque<Long>()
        for (token in output) {
            if (token[0].isDigit()) {
                stack.addLast(token.toLong())
            } else {
                val right = stack.removeLast()
                val left = stack.removeLast()
                val result = when (token) {
                    "+" -> left + right
                    "-" -> left - right
                    "*" -> left * right
                    "/" -> {
                        require(right != 0L && left % right == 0L) { "Division must be exact" }
                        left / right
                    }
                    else -> throw IllegalArgumentException("Unknown operator")
                }
                stack.addLast(result)
            }
        }
        require(stack.size == 1) { "Invalid expression" }
        return stack.removeLast()
    }
}
