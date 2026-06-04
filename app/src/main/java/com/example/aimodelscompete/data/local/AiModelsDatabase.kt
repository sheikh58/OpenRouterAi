package com.example.aimodelscompete.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.aimodelscompete.data.local.entity.ChatHistoryEntity
import com.example.aimodelscompete.data.local.entity.ModelEntity

@Database(
    entities = [ModelEntity::class, ChatHistoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AiModelsDatabase : RoomDatabase() {
    abstract val dao: AiModelsDao
}
