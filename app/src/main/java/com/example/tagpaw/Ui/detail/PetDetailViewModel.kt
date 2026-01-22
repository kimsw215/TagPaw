package com.example.tagpaw.Ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagpaw.data.repository.PetRepository
import com.example.tagpaw.domain.entities.PetEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetDetailViewModel @Inject constructor(
    private val petRepository: PetRepository
) : ViewModel() {

    private val _pet = MutableStateFlow<PetEntity?>(null)
    val pet: StateFlow<PetEntity?> = _pet

    fun loadPet(id: Long) {
        viewModelScope.launch {
            petRepository.getPetById(id).collect {
                _pet.value = it
            }
        }
    }

    fun deletePet(pet: PetEntity, onDeleted: () -> Unit) {
        viewModelScope.launch {
            petRepository.delete(pet)
            onDeleted()
        }
    }
}
