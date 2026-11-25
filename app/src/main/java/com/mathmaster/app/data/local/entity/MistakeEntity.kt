package com.mathmaster.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mistakes",
    indices = [Index("topicId"), Index("improved")]
)
data class MistakeEntity(
    @PrimaryKey val signature: String,
    val topicId: String,
    val topicName: String,
    val prompt: String,
    val correctAnswer: String,
    val explanation: String,
    val lastWrongAnswer: String,
    val wrongCount: Int,
    val correctCount: Int,
    val improved: Boolean,
    val createdAt: Long,
    val lastPracticedAt: Long
)
