package com.example.tagpaw.data.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tagpaw.domain.entities.PetEntity

@Database(
    entities = [PetEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TagPawDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
}