package com.example.tagpaw.di

import com.example.tagpaw.data.repository.PetRepository
import com.example.tagpaw.data.repository.PetRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPetRepository(
        impl: PetRepositoryImpl
    ): PetRepository
}