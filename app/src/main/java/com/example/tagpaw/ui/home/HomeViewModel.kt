package com.example.tagpaw.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagpaw.data.repository.PetRepository
import com.example.tagpaw.domain.entities.PetEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val petRepository: PetRepository
) : ViewModel() {

    val pets: StateFlow<List<PetEntity>> =
        petRepository.getAllPets()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = emptyList()
            )

    private val _navigateToPetDetail = MutableSharedFlow<Long>()
    val navigateToPetDetail: SharedFlow<Long> = _navigateToPetDetail

    fun onTagScanned(uid: String) {
        viewModelScope.launch {
            val pet = petRepository.getPetByTagUid(uid)
            pet?.let { _navigateToPetDetail.emit(it.id) }
        }
    }
}