package com.mathmaster.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mathmaster.app.data.local.entity.MistakeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MistakeDao {

    @Upsert
    suspend fun upsert(mistake: MistakeEntity)

    @Query("SELECT * FROM mistakes ORDER BY lastPracticedAt DESC")
    fun observeAll(): Flow<List<MistakeEntity>>

    @Query("SELECT * FROM mistakes WHERE topicId = :topicId ORDER BY lastPracticedAt DESC")
    fun observeTopic(topicId: String): Flow<List<MistakeEntity>>

    @Query("SELECT * FROM mistakes ORDER BY lastPracticedAt DESC")
    suspend fun allOnce(): List<MistakeEntity>

    @Query("SELECT * FROM mistakes WHERE signature = :signature LIMIT 1")
    suspend fun find(signature: String): MistakeEntity?

    @Query("SELECT COUNT(*) FROM mistakes")
    fun count(): Flow<Int>

    @Query("SELECT COUNT(*) FROM mistakes WHERE improved = 1")
    fun improvedCount(): Flow<Int>

    @Query("DELETE FROM mistakes WHERE signature = :signature")
    suspend fun delete(signature: String)

    @Query("DELETE FROM mistakes")
    suspend fun clear()
}
