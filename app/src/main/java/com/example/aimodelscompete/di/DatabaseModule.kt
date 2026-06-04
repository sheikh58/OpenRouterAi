package com.example.aimodelscompete.di

import android.app.Application
import androidx.room.Room
import com.example.aimodelscompete.data.local.AiModelsDao
import com.example.aimodelscompete.data.local.AiModelsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AiModelsDatabase {
        return Room.databaseBuilder(
            app,
            AiModelsDatabase::class.java,
            "aimodels.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideDao(db: AiModelsDatabase): AiModelsDao {
        return db.dao
    }
}
