package com.mathmaster.app.domain.model

enum class Difficulty { EASY, MEDIUM, HARD }

enum class PracticeMode { PRACTICE, QUIZ, SPEED, MISTAKE }

enum class TopicCategory { BASIC, TABLES, IBPS, SPEED }

data class Topic(
    val id: String,
    val name: String,
    val category: TopicCategory,
    val description: String
)

data class Question(
    val signature: String,
    val topicId: String,
    val topicName: String,
    val prompt: String,
    val answer: String,
    val explanation: String,
    val difficulty: Difficulty,
    val hint: String = "",
    val tags: List<String> = emptyList()
)
