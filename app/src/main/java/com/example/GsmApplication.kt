package com.example

import android.app.Application
import com.example.data.database.AppDatabase
import com.example.notification.NotificationHelper
import com.example.preferences.UserPreferences
import com.example.repository.GsmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class GsmApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { GsmRepository(database, this) }
    val userPreferences by lazy { UserPreferences(this) }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
    }
}
