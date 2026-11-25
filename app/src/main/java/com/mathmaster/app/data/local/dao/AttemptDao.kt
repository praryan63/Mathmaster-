package com.mathmaster.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mathmaster.app.data.local.entity.AttemptEntity
import kotlinx.coroutines.flow.Flow

data class TopicStat(
    val topicId: String,
    val topicName: String,
    val attempts: Int,
    val correct: Int,
    val totalTimeMs: Long
)

data class DayStat(
    val day: String,
    val attempts: Int,
    val correct: Int
)

@Dao
interface AttemptDao {

    @Insert
    suspend fun insert(attempt: AttemptEntity)

    @Query("SELECT * FROM attempts ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<AttemptEntity>>

    @Query("SELECT * FROM attempts WHERE sessionId = :sessionId ORDER BY id ASC")
    fun observeSession(sessionId: String): Flow<List<AttemptEntity>>

    @Query("SELECT * FROM attempts ORDER BY timestamp DESC LIMIT :limit")
    suspend fun recent(limit: Int): List<AttemptEntity>

    @Query("SELECT questionSignature FROM attempts ORDER BY id DESC LIMIT :limit")
    suspend fun recentSignatures(limit: Int): List<String>

    @Query("SELECT COUNT(*) FROM attempts")
    fun count(): Flow<Int>

    @Query("SELECT COUNT(*) FROM attempts WHERE isCorrect = 1")
    fun correctCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(durationMs), 0) FROM attempts")
    fun totalDuration(): Flow<Long>

    @Query("SELECT day, COUNT(*) AS attempts, SUM(CASE WHEN isCorrect = 1 THEN 1 ELSE 0 END) AS correct FROM attempts GROUP BY day")
    fun dailyStats(): Flow<List<DayStat>>

    @Query("SELECT topicId, MAX(topicName) AS topicName, COUNT(*) AS attempts, SUM(CASE WHEN isCorrect = 1 THEN 1 ELSE 0 END) AS correct, COALESCE(SUM(durationMs), 0) AS totalTimeMs FROM attempts GROUP BY topicId ORDER BY attempts DESC")
    fun topicStats(): Flow<List<TopicStat>>

    @Query("DELETE FROM attempts")
    suspend fun clear()
}
