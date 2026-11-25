package com.mathmaster.app.di

import android.content.Context
import com.mathmaster.app.data.local.MathDatabase
import com.mathmaster.app.data.preferences.SettingsRepository
import com.mathmaster.app.data.repository.PracticeRepository
import com.mathmaster.app.domain.question.QuestionBank
import com.mathmaster.app.util.FeedbackManager

object ServiceLocator {

    lateinit var repository: PracticeRepository
        private set
    lateinit var settings: SettingsRepository
        private set
    lateinit var questionBank: QuestionBank
        private set
    lateinit var feedbackManager: FeedbackManager
        private set

    fun init(context: Context) {
        if (this::repository.isInitialized) return
        val applicationContext = context.applicationContext
        val database = MathDatabase.get(applicationContext)
        repository = PracticeRepository(database)
        settings = SettingsRepository(applicationContext)
        questionBank = QuestionBank()
        feedbackManager = FeedbackManager()
    }
}
