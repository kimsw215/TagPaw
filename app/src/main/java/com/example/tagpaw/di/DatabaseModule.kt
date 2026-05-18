package com.example.tagpaw.di

import android.content.Context
import androidx.room.Room
import com.example.tagpaw.data.roomdb.PetDao
import com.example.tagpaw.data.roomdb.TagPawDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): TagPawDatabase {
        return Room.databaseBuilder(
            context,
            TagPawDatabase::class.java,
            "tagpaw.db"
        )
            .fallbackToDestructiveMigration() // 스키마가 바뀌면 기존 데이터를 전부 지우고 새로 만들어줌
            .build()
    }

    @Provides
    fun providePetDao(
        db: TagPawDatabase
    ): PetDao = db.petDao()
}