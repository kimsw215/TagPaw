package com.example.tagpaw.ui.tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagpaw.data.repository.PetRepository
import com.example.tagpaw.domain.entities.PetEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagRegisterViewModel @Inject constructor(
    private val petRepository: PetRepository
) : ViewModel() {

    private val _pet = MutableStateFlow<PetEntity?>(null)
    val pet: StateFlow<PetEntity?> = _pet.asStateFlow()

    fun loadPet(petId: Long) {
        viewModelScope.launch {
            petRepository.getPetById(petId).collect {
                _pet.value = it
            }
        }
    }

    fun saveTagToPet(
        petId: Long,
        uid: String,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            val currentPet = _pet.value ?: return@launch
            val updated = currentPet.copy(tagUid = uid)
            petRepository.upsert(updated)
            onSaved()
        }
    }
}
