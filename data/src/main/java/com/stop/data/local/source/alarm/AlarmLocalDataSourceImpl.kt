package com.stop.data.local.source.alarm

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.moshi.Moshi
import com.stop.data.local.model.Alarm
import com.stop.data.model.alarm.AlarmRepositoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AlarmLocalDataSourceImpl @Inject constructor(
    private val context: Context,
    moshi: Moshi
) : AlarmLocalDataSource {

    private val adapter = moshi.adapter(Alarm::class.java)

    override suspend fun saveAlarm(alarmRepositoryItem: AlarmRepositoryItem) {
        context.datastore.edit { preferences ->
            preferences[ALARM] = adapter.toJson(alarmRepositoryItem.toDataSourceModel())
        }
    }

    override suspend fun deleteAlarm() {
        context.datastore.edit { preferences ->
            preferences.remove(ALARM)
        }
    }

    override suspend fun getAlarm(): Flow<AlarmRepositoryItem?> {
        return context.datastore.data.map { preferences ->
            val jsonString = preferences[ALARM] ?: ""
            val elements = adapter.fromJson(jsonString)?.toRepositoryModel()
            elements
        }
    }

    companion object {
        private const val ALARM_KEY = "ALARM_KEY"
        private val ALARM = stringPreferencesKey(ALARM_KEY)
    }

}

const val DATASTORE_NAME = "ALARM"

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)