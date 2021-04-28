package com.liviolopez.citycrimes.ui.home

import androidx.lifecycle.*
import com.liviolopez.citycrimes.data.local.model.Availability
import com.liviolopez.citycrimes.data.local.model.Category
import com.liviolopez.citycrimes.data.local.model.CrimeInfo
import com.liviolopez.citycrimes.data.local.model.Force
import com.liviolopez.citycrimes.repository.Repository
import com.liviolopez.citycrimes.utils.Resource
import com.liviolopez.citycrimes.utils._log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    enum class LocationFilter { All, CURRENT }

    init {
        viewModelScope.launch { repository.initializeCache() }
    }

    private val _filterDate = savedStateHandle.getLiveData<String>("filter_date")
    var filterDate
        get() = _filterDate.value
        set(value) { _filterDate.value = value }

    private val _filterCategory = savedStateHandle.getLiveData<String>("filter_category")
    var filterCategory
        get() = _filterCategory.value
        set(value) { _filterCategory.value = value }

    private val _filterForce = savedStateHandle.getLiveData<String>("filter_force")
    var filterForce
        get() = _filterForce.value
        set(value) { _filterForce.value = value }

    private val _filterLocation = savedStateHandle.getLiveData<LocationFilter>("filter_location")
    var filterLocation
        get() = _filterLocation.value
        set(value) { _filterLocation.value = value }

    val availabilities: Flow<List<Availability>> get() = repository.getAvailabilities()
    val categories: Flow<List<Category>> get() = repository.getCategories()
    val forces: Flow<List<Force>> get() = repository.getForces()

    private val _crimesFiltered = MutableStateFlow<Resource<List<CrimeInfo>>>(Resource.success(emptyList()))
    val crimesFiltered = _crimesFiltered.asStateFlow()

    fun filterCrimes() {
        _filterLocation.value?._log()

        if(!_filterDate.value.isNullOrEmpty() && !_filterCategory.value.isNullOrEmpty() && !_filterForce.value.isNullOrEmpty()){
            viewModelScope.launch {
                _crimesFiltered.value = Resource.loading()

                val crimesFlow = if(_filterLocation.value == LocationFilter.All) {
                    repository.fetchCrimes( _filterDate.value!!, _filterCategory.value!!, _filterForce.value!!)
                } else {
                    repository.fetchCrimesCloseToMe( _filterDate.value!!, _filterCategory.value!!, _filterForce.value!!, 52.629729, -1.131592)
                }

                crimesFlow.catch { e -> _crimesFiltered.value = Resource.error(e, emptyList()) }
                          .debounce { 500 }
                          .collectLatest {  _crimesFiltered.value = it }
            }
        }
    }

}