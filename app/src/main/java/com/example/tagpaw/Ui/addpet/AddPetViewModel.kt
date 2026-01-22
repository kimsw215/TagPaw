package com.example.tagpaw.Ui.addpet

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
        sex: String,
        age: String,
        phone: String,
        note: String,
        pin: String,
        onSaved: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val id = petRepository.upsert(
                PetEntity(
                    name = name,
                    sex = sex,
                    age = age,
                    emergencyPhone = phone,
                    emergencyNote = note,
                    pin = pin
                )
            )
            onSaved(id)
        }
    }
}