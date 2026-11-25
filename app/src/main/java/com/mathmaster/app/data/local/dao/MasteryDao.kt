package com.mathmaster.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mathmaster.app.data.local.entity.TableMasteryEntity
import com.mathmaster.app.data.local.entity.TopicMasteryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MasteryDao {

    @Upsert
    suspend fun upsertTopic(mastery: TopicMasteryEntity)

    @Upsert
    suspend fun upsertTable(mastery: TableMasteryEntity)

    @Query("SELECT * FROM topic_mastery ORDER BY attempts DESC")
    fun observeTopics(): Flow<List<TopicMasteryEntity>>

    @Query("SELECT * FROM topic_mastery WHERE topicId = :topicId LIMIT 1")
    suspend fun topic(topicId: String): TopicMasteryEntity?

    @Query("SELECT * FROM table_mastery ORDER BY tableNumber ASC")
    fun observeTables(): Flow<List<TableMasteryEntity>>

    @Query("SELECT * FROM table_mastery WHERE tableNumber = :number LIMIT 1")
    suspend fun table(number: Int): TableMasteryEntity?

    @Query("DELETE FROM topic_mastery")
    suspend fun clearTopics()

    @Query("DELETE FROM table_mastery")
    suspend fun clearTables()
}
