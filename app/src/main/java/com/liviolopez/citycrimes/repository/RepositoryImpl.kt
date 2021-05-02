package com.liviolopez.citycrimes.repository

import androidx.room.withTransaction
import com.liviolopez.citycrimes.data.local.AppDataBase
import com.liviolopez.citycrimes.data.local.AppDataStore
import com.liviolopez.citycrimes.data.local.model.*
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

    override fun fetchCrimes(date: String, categoryId: String, forceId: String, location: Crime.Location?) = networkBoundResource(
        loadFromDb = {
            val query = crimeDao.createSimpleQuery(date, forceId, categoryId, location)
            crimeDao.getCrimesRaw(query)
        },
        createCall = {
            if(location == null){
                remoteData.fetchCrimes(date, categoryId, forceId)
            } else {
                remoteData.fetchCrimesNearPosition(date, categoryId, forceId, location.latitude, location.longitude)
            }
        },
        saveFetchResult = { crimes ->
            localData.withTransaction {
                crimeDao.deleteAllCrimes()
                crimeDao.insertCrimes(crimes.map { it.toLocalModel(forceId, location != null) })
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