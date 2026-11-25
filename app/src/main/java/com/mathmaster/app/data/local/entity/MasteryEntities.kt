package com.mathmaster.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topic_mastery")
data class TopicMasteryEntity(
    @PrimaryKey val topicId: String,
    val topicName: String,
    val attempts: Int,
    val correct: Int,
    val totalTimeMs: Long,
    val lastPracticedAt: Long
)

@Entity(tableName = "table_mastery")
data class TableMasteryEntity(
    @PrimaryKey val tableNumber: Int,
    val attempts: Int,
    val correct: Int,
    val lastPracticedAt: Long
)
