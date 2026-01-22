package com.example.tagpaw.data.roomdb

import androidx.room.*
import com.example.tagpaw.domain.entities.PetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {

    @Query("SELECT * FROM pets ORDER BY id DESC")
    fun getAllPets(): Flow<List<PetEntity>>

    @Query("SELECT * FROM pets WHERE id = :id LIMIT 1")
    fun getPetById(id: Long): Flow<PetEntity?>

    @Query("SELECT * FROM pets WHERE tagUid = :tagUid LIMIT 1")
    suspend fun getPetByTagUid(tagUid: String): PetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(pet: PetEntity): Long

    @Delete
    suspend fun delete(pet: PetEntity)
}
