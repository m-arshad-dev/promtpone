package com.example.campuscompanion

import android.app.Application
import com.example.campuscompanion.data.local.database.AppDatabase
import com.example.campuscompanion.data.remote.mock.MockCampusApiService
import com.example.campuscompanion.data.repository.CampusRepository
import com.example.campuscompanion.data.repository.UserPreferencesRepository
import com.example.campuscompanion.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CampusCompanionApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val database by lazy { AppDatabase.getDatabase(this) }
    val preferencesRepository by lazy { UserPreferencesRepository(this) }
    val apiService by lazy { MockCampusApiService() }
    val campusRepository by lazy {
        CampusRepository(
            context = this,
            dao = database.campusDao(),
            apiService = apiService,
            preferencesRepository = preferencesRepository
        )
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)

        // Seed initial data asynchronously if database is newly initialized
        applicationScope.launch(Dispatchers.IO) {
            campusRepository.refreshAllData()
        }
    }
}
