package com.liviolopez.citycrimes.repository

import android.app.Application
import android.content.res.AssetManager.ACCESS_STREAMING
import androidx.room.withTransaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liviolopez.citycrimes.data.local.AppDataBase
import com.liviolopez.citycrimes.data.local.AppDataStore
import com.liviolopez.citycrimes.data.local.model.*
import com.liviolopez.citycrimes.data.remote.RemoteDataSource
import com.liviolopez.citycrimes.data.remote.response.DetailsDto
import com.liviolopez.citycrimes.data.remote.response.toLocalModel
import com.liviolopez.citycrimes.utils.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class RepositoryImpl(
    private val remoteData: RemoteDataSource,
    private val localData: AppDataBase,
    private val appDataStore: AppDataStore,
    private val application: Application
) : Repository {
    private val crimeDao = localData.crimeDao()
    private val crimeOutcomeDao = localData.crimeOutcomeDao()

    private val availabilityDao = localData.availabilityDao()
    private val categoryDao = localData.categoryDao()
    private val forceDao = localData.forceDao()
    private val outcomeStatusDao = localData.outcomeStatusDao()

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
                crimeOutcomeDao.deleteAllCrimeOutcomes()
                crimeDao.deleteAllCrimes()

                crimeDao.insertCrimes(crimes.map { it.toLocalModel(forceId, location != null) })

                val outcomeStatusIds = mutableMapOf<String, String>()
                outcomeStatusDao.getOutcomesStatus().map { outcomeStatusIds[it.name] = it.id }

                val crimeOutcomes = crimes
                        .filter { it.outcomeStatus != null }
                        .map {
                            CrimeOutcome(
                                crimeId = it.id,
                                outcomeStatusId = outcomeStatusIds[it.outcomeStatus!!.category]!!,
                                month = it.outcomeStatus.date,
                                isLatest = true
                            )
                        }

                crimeOutcomeDao.insertCrimeOutcomes(crimeOutcomes)
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
        // API Resources -> DB
        syncNetworkAvailabilities()
        syncNetworkCategories()
        syncNetworkForces()

        // Assets Resources -> DB
        syncAssetsOutcomesStatus()

        // Register Sync on Local DataStore
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

    /**
     * The methods related to the DB table "OutcomesStatus" are a clear example of Overplanning.
     *
     * However these methods were developed as examples of:
     * - Room Entity/Table with multiple foreignKeys (CrimeOutcome entity)
     * - Room @DatabaseView and one-to-many @Relation with @Entity
     * - Fetch data from a locally stored JSON file
     */
    private suspend fun syncAssetsOutcomesStatus() = syncNetworkResource(
        shouldFetch = {
            coroutineScope {
                outcomeStatusDao.getCountOutcomesStatus() == 0
            }
        },
        runFetchCall = {
            val outcomeStatusJson = application.assets.open("outcome_status.json", ACCESS_STREAMING).bufferedReader().readText()

            val outcomeStatus: List<OutcomeStatus> = try {
                Gson().fromJson(outcomeStatusJson, object: TypeToken<List<OutcomeStatus>>() {}.type)
            } catch (e: Throwable) {
                emptyList()
            }

            outcomeStatus
        },
        saveFetchResult = { outcomesStatus ->
            localData.withTransaction {
                outcomeStatusDao.deleteAllOutcomesStatus()
                outcomeStatusDao.insertOutcomeStatus(outcomesStatus)
            }
        }
    )
}