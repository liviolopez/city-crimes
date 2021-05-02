package com.liviolopez.citycrimes.ui.home

import androidx.lifecycle.*
import com.liviolopez.citycrimes.data.local.model.Availability
import com.liviolopez.citycrimes.data.local.model.Category
import com.liviolopez.citycrimes.data.local.model.CrimeInfo
import com.liviolopez.citycrimes.data.local.model.Force
import com.liviolopez.citycrimes.repository.Repository
import com.liviolopez.citycrimes.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    enum class LocationFilter { All, CURRENT }

    init {
        viewModelScope.launch {
            repository.initializeCache()
            filterCrimes()
        }
    }

    val availabilities: Flow<List<Availability>> get() = repository.getAvailabilities()
    val categories: Flow<List<Category>> get() = repository.getCategories()
    val forces: Flow<List<Force>> get() = repository.getForces()

    private val _crimesFiltered = MutableStateFlow<Resource<List<CrimeInfo>>>(Resource.success(emptyList()))
    val crimesFiltered = _crimesFiltered.asStateFlow()


    private val _filterDate = savedStateHandle.getLiveData<String>("filter_date")
    val filterDate = MutableStateFlow(_filterDate.value)

    private val _filterCategory = savedStateHandle.getLiveData<String>("filter_category")
    val filterCategory = MutableStateFlow(_filterCategory.value)

    private val _filterForce = savedStateHandle.getLiveData<String>("filter_force")
    val filterForce = MutableStateFlow(_filterForce.value)

    private val _filterLocation = savedStateHandle.getLiveData<LocationFilter>("filter_location")
    val filterLocation = MutableStateFlow(_filterLocation.value)


    private val isValidFilter: Flow<Boolean> = combine(filterDate, filterCategory, filterForce, filterLocation) { date, category, force, location ->
        _filterDate.value = date
        _filterCategory.value = category
        _filterForce.value = force
        _filterLocation.value = location

        return@combine date !== null && category !== null && force !== null
    }

    private suspend fun filterCrimes() {
        val filterScope = CoroutineScope(Dispatchers.IO)

        isValidFilter.collect { isValid ->
            filterScope.coroutineContext.cancelChildren()

            if (isValid){
                _crimesFiltered.value = Resource.loading()

                val crimesFlow = if(_filterLocation.value == LocationFilter.All) {
                    repository.fetchCrimes( _filterDate.value!!, _filterCategory.value!!, _filterForce.value!!)
                } else {
                    repository.fetchCrimesCloseToMe( _filterDate.value!!, _filterCategory.value!!, _filterForce.value!!, 52.629729, -1.131592)
                }

                filterScope.launch {
                    crimesFlow.catch { e -> _crimesFiltered.value = Resource.error(e, emptyList()) }
                        .debounce(500)
                        .collectLatest {  _crimesFiltered.value = it }
                }

            } else {
                _crimesFiltered.value = Resource.success(emptyList())
            }
        }
    }
}
