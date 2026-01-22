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
        ).build()
    }

    @Provides
    fun providePetDao(
        db: TagPawDatabase
    ): PetDao = db.petDao()
}