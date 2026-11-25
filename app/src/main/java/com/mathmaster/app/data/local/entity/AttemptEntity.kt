package com.mathmaster.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attempts",
    indices = [Index("topicId"), Index("day"), Index("sessionId"), Index("timestamp")]
)
data class AttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val sessionId: String,
    val topicId: String,
    val topicName: String,
    val prompt: String,
    val questionSignature: String,
    val correctAnswer: String,
    val explanation: String,
    val userAnswer: String,
    val isCorrect: Boolean,
    val durationMs: Long,
    val difficulty: String,
    val mode: String,
    val day: String,
    val timestamp: Long
)
