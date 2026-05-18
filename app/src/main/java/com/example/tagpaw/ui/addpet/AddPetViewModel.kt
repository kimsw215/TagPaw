package com.example.tagpaw.ui.addpet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagpaw.data.repository.PetRepository
import com.example.tagpaw.domain.entities.PetEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPetViewModel @Inject constructor(
    private val petRepository: PetRepository
) : ViewModel() {

    fun savePet(
        name: String,
        age: String,
        phone: String,
        note: String,
        pin: String,
        onSaved: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val pet = PetEntity(
                name = name,
                age = age,           // "2" → NFC 저장 시 "화멍이(2살)" 로 조합
                emergencyPhone = phone,
                emergencyNote = note,
                pin = pin
            )
            val id = petRepository.upsert(pet)
            onSaved(id)
        }
    }
}