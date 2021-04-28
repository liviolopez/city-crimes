package com.liviolopez.citycrimes.repository

import androidx.room.withTransaction
import com.liviolopez.citycrimes.data.local.AppDataBase
import com.liviolopez.citycrimes.data.local.AppDataStore
import com.liviolopez.citycrimes.data.local.model.Availability
import com.liviolopez.citycrimes.data.local.model.Category
import com.liviolopez.citycrimes.data.local.model.CrimeInfo
import com.liviolopez.citycrimes.data.local.model.Force
import com.liviolopez.citycrimes.data.remote.RemoteDataSource
import com.liviolopez.citycrimes.data.remote.response.DetailsDto
import com.liviolopez.citycrimes.data.remote.response.toLocalModel
import com.liviolopez.citycrimes.utils.Resource
import com.liviolopez.citycrimes.utils.networkBoundResource
import com.liviolopez.citycrimes.utils.syncNetworkResource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class RepositoryImpl(
    private val remoteData: RemoteDataSource,
    private val localData: AppDataBase,
    private val appDataStore: AppDataStore
) : Repository {
    private val crimeDao = localData.crimeDao()

    private val availabilityDao = localData.availabilityDao()
    private val categoryDao = localData.categoryDao()
    private val forceDao = localData.forceDao()

    override fun fetchCrimes(date: String, categoryId: String, forceId: String) = networkBoundResource(
        loadFromDb = {
            if(categoryId == "all-crime"){
                crimeDao.getCrimesAllCategories(date, forceId)
            } else {
                crimeDao.getCrimes(date, categoryId, forceId)
            }
        },
        createCall = {
            remoteData.fetchCrimes(date, categoryId, forceId)
        },
        saveFetchResult = { crimes ->
            localData.withTransaction {
                // crimeDao.deleteAllCrimes()
                crimeDao.insertCrimes(crimes.map { it.toLocalModel(forceId, false) })
            }
        }
    )

    override fun fetchCrimesCloseToMe(date: String, categoryId: String, forceId: String, latitude: Double, longitude: Double) = networkBoundResource(
        loadFromDb = {
            if(categoryId == "all-crime"){
                crimeDao.getCrimesCloseToMeAllCategories(date, forceId, true)
            } else {
                crimeDao.getCrimesCloseToMe(date, categoryId, forceId, true)
            }
        },
        createCall = {
            remoteData.fetchCrimesCloseToMe(date, categoryId, forceId, latitude, longitude)
        },
        saveFetchResult = { crimes ->
            localData.withTransaction {
                // crimeDao.deleteAllCrimes()
                crimeDao.insertCrimes(crimes.map { it.toLocalModel(forceId, true) })
            }
        }
    )

    override suspend fun fetchCrimeDetail(persistentId: String): Flow<Resource<DetailsDto>> {
        return flowOf(Resource.success(remoteData.fetchCrimeDetails(persistentId)))
    }

    override fun getCrime(persistentId: String): Flow<List<CrimeInfo>> {
        return crimeDao.getCrime(persistentId)
    }

    override fun getAvailabilities(): Flow<List<Availability>> {
        return availabilityDao.getAvailabilities()
    }

    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.getCategories()
    }

    override fun getForces(): Flow<List<Force>> {
        return forceDao.getForces()
    }

    override suspend fun initializeCache() {
        syncNetworkAvailabilities()
        syncNetworkCategories()
        syncNetworkForces()
        appDataStore.registerDbUpdate()
    }

    private suspend fun syncNetworkAvailabilities() = syncNetworkResource(
        shouldFetch = {
            coroutineScope {
                appDataStore.isDbUpdateNeeded() || availabilityDao.getCountAvailabilities() == 0
            }
        },
        runFetchCall = {
            remoteData.fetchAvailabilities()
        },
        saveFetchResult = { availabilities ->
            localData.withTransaction {
                availabilityDao.deleteAllAvailabilities()
                availabilityDao.insertAvailabilities( availabilities.map { it.toLocalModel() } )
            }
        }
    )

    private suspend fun syncNetworkCategories() = syncNetworkResource(
        shouldFetch = {
            coroutineScope {
                appDataStore.isDbUpdateNeeded() || categoryDao.getCountCategories() == 0
            }
        },
        runFetchCall = {
            remoteData.fetchCategories()
        },
        saveFetchResult = { categories ->
            localData.withTransaction {
                categoryDao.deleteAllCategories()
                categoryDao.insertCategories( categories.map { it.toLocalModel() } )
            }
        }
    )

    private suspend fun syncNetworkForces() = syncNetworkResource(
        shouldFetch = {
            coroutineScope {
                appDataStore.isDbUpdateNeeded() || forceDao.getCountForces() == 0
            }
        },
        runFetchCall = {
            remoteData.fetchForces()
        },
        saveFetchResult = { forces ->
            localData.withTransaction {
                forceDao.deleteAllForces()
                forceDao.insertForces( forces.map { it.toLocalModel() } )
            }
        }
    )
}