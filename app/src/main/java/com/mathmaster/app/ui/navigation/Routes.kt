package com.mathmaster.app.ui.navigation

import com.mathmaster.app.domain.model.PracticeMode

object Routes {
    const val HOME = "home"
    const val LEARN = "learn"
    const val TABLES = "tables"
    const val TABLE_DETAIL = "table_detail/{number}"
    const val IBPS = "ibps"
    const val MISTAKES = "mistakes"
    const val SPEED = "speed"
    const val QUIZ_SETUP = "quiz_setup?topic={topic}"
    const val PRACTICE = "practice/{topicId}/{mode}?count={count}&timer={timer}"
    const val ANALYTICS = "analytics"
    const val SETTINGS = "settings"
    const val RESULT = "result/{sessionId}"

    fun tableDetail(number: Int) = "table_detail/$number"

    fun quizSetup(topicId: String = "mixed") = "quiz_setup?topic=$topicId"

    fun practice(
        topicId: String,
        mode: PracticeMode,
        count: Int = 0,
        timer: Boolean = false
    ): String = "practice/$topicId/${mode.name}?count=$count&timer=$timer"

    fun result(sessionId: String) = "result/$sessionId"
}
