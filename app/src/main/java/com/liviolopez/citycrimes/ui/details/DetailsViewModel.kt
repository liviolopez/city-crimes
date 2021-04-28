package com.liviolopez.citycrimes.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.liviolopez.citycrimes.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    fun crime(persistentId: String) = repository.getCrime(persistentId)

    suspend fun crimeDetails(persistentId: String) = repository.fetchCrimeDetail(persistentId)
}