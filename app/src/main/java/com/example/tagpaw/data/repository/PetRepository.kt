package com.example.tagpaw.data.repository

import com.example.tagpaw.data.roomdb.PetDao
import com.example.tagpaw.domain.entities.PetEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface PetRepository {
    fun getAllPets(): Flow<List<PetEntity>>
    fun getPetById(id: Long): Flow<PetEntity?>
    suspend fun getPetByTagUid(tagUid: String): PetEntity?
    suspend fun upsert(pet: PetEntity): Long
    suspend fun delete(pet: PetEntity)
}

class PetRepositoryImpl @Inject constructor(
    private val petDao: PetDao
) : PetRepository {

    override fun getAllPets(): Flow<List<PetEntity>> = petDao.getAllPets()

    override fun getPetById(id: Long): Flow<PetEntity?> = petDao.getPetById(id)

    override suspend fun getPetByTagUid(tagUid: String): PetEntity? =
        petDao.getPetByTagUid(tagUid)

    override suspend fun upsert(pet: PetEntity): Long = petDao.upsert(pet)

    override suspend fun delete(pet: PetEntity) = petDao.delete(pet)
}