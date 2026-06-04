package com.example.aimodelscompete.data.local

import androidx.room.*
import com.example.aimodelscompete.data.local.entity.ChatHistoryEntity
import com.example.aimodelscompete.data.local.entity.ModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiModelsDao {
    @Query("SELECT * FROM models")
    fun getAllModels(): Flow<List<ModelEntity>>

    @Query("SELECT * FROM models")
    suspend fun getModelsOnce(): List<ModelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModels(models: List<ModelEntity>)

    @Query("DELETE FROM models")
    suspend fun deleteAllModels()

    @Update
    suspend fun updateModel(model: ModelEntity)

    @Query("SELECT * FROM chat_history WHERE modelId = :modelId ORDER BY timestamp ASC")
    fun getHistoryForModel(modelId: String): Flow<List<ChatHistoryEntity>>

    @Insert
    suspend fun insertChat(chat: ChatHistoryEntity)
    
    @Query("DELETE FROM chat_history WHERE modelId = :modelId")
    suspend fun clearHistoryForModel(modelId: String)

    @Query("SELECT * FROM chat_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<ChatHistoryEntity>>

    @Query("DELETE FROM chat_history")
    suspend fun clearAllHistory()
}
