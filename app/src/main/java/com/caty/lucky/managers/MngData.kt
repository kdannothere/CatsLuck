package com.caty.lucky.managers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object MngData {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data")

    const val scoreDefault = 10000
    const val theBetDefault = 50

    val emailKey = stringPreferencesKey("email")
    val privacyKey = booleanPreferencesKey("privacy")
    val musicSettingKey = booleanPreferencesKey("music")
    val lvlKey = intPreferencesKey("lvl")
    val winNumberKey = intPreferencesKey("winNumber")
    val vibrationSettingKey = booleanPreferencesKey("vibration")
    val scoreKey = intPreferencesKey("score")
    val betKey = intPreferencesKey("bet")

    suspend fun loadScores(context: Context, currentValue: Int): Int {
        return context.dataStore.data
            .map { preferences ->
                preferences[scoreKey]
                    ?: currentValue
            }
            .first()
    }

    suspend fun loadLastBet(context: Context, currentValue: Int): Int {
        return context.dataStore.data
            .map { preferences ->
                preferences[betKey] ?: currentValue
            }
            .first()
    }

    suspend fun loadLvl(context: Context, currentValue: Int): Int {
        return context.dataStore.data
            .map { preferences ->
                preferences[lvlKey] ?: currentValue
            }
            .first()
    }

    suspend fun loadWinNumber(context: Context, currentValue: Int): Int {
        return context.dataStore.data
            .map { preferences ->
                preferences[winNumberKey] ?: currentValue
            }
            .first()
    }

    suspend fun loadEmail(context: Context): String {
        return context.dataStore.data
            .map { preferences ->
                preferences[emailKey] ?: ""
            }
            .first()
    }

    suspend fun loadPrivacy(context: Context): Boolean {
        return context.dataStore.data
            .map { preferences ->
                preferences[privacyKey] ?: false
            }
            .first()
    }

    suspend fun loadMusicSetting(context: Context): Boolean {
        return context.dataStore.data
            .map { preferences ->
                preferences[musicSettingKey] ?: true
            }
            .first()
    }

    suspend fun loadVibrationSetting(context: Context): Boolean {
        return context.dataStore.data
            .map { preferences ->
                preferences[vibrationSettingKey] ?: true
            }
            .first()
    }

    suspend fun savePrivacy(context: Context, privacy: Boolean) {
        context.dataStore.edit { data ->
            data[privacyKey] = privacy
        }
    }

    suspend fun saveMusicSetting(context: Context, isMusicOn: Boolean) {
        context.dataStore.edit { data ->
            data[musicSettingKey] = isMusicOn
        }
    }

    suspend fun saveVibrationSetting(context: Context, isVibrationOn: Boolean) {
        context.dataStore.edit { data ->
            data[vibrationSettingKey] = isVibrationOn
        }
    }

    suspend fun saveLvl(context: Context, lvl: Int) {
        context.dataStore.edit { data ->
            data[lvlKey] = lvl
        }
    }

    suspend fun saveWinNumber(context: Context, winNumber: Int) {
        context.dataStore.edit { data ->
            data[winNumberKey] = winNumber
        }
    }

    suspend fun saveEmail(context: Context, login: String) {
        context.dataStore.edit { data ->
            data[emailKey] = login
        }
    }

    suspend fun saveScore(context: Context, balance: Int) {
        context.dataStore.edit { data ->
            data[scoreKey] = balance
        }
    }

    suspend fun saveCurrentBet(context: Context, bet: Int) {
        context.dataStore.edit { data ->
            data[betKey] = bet
        }
    }
}
