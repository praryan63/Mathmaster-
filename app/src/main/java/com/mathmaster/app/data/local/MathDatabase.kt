package com.mathmaster.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mathmaster.app.data.local.dao.AttemptDao
import com.mathmaster.app.data.local.dao.MasteryDao
import com.mathmaster.app.data.local.dao.MistakeDao
import com.mathmaster.app.data.local.entity.AttemptEntity
import com.mathmaster.app.data.local.entity.MistakeEntity
import com.mathmaster.app.data.local.entity.TableMasteryEntity
import com.mathmaster.app.data.local.entity.TopicMasteryEntity

@Database(
    entities = [AttemptEntity::class, MistakeEntity::class, TopicMasteryEntity::class, TableMasteryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MathDatabase : RoomDatabase() {

    abstract fun attemptDao(): AttemptDao
    abstract fun mistakeDao(): MistakeDao
    abstract fun masteryDao(): MasteryDao

    companion object {
        @Volatile
        private var instance: MathDatabase? = null

        fun get(context: Context): MathDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                MathDatabase::class.java,
                "math_master.db"
            ).fallbackToDestructiveMigration().build().also { instance = it }
        }
    }
}
