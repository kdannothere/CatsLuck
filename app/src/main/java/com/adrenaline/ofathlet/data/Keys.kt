package com.adrenaline.ofathlet.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Keys {
    val balanceKey = intPreferencesKey("balance")
    val betKey = intPreferencesKey("bet")
    val loginKey = stringPreferencesKey("login")
    val musicSettingKey = booleanPreferencesKey("musicSetting")
    val soundSettingKey = booleanPreferencesKey("soundSetting")
    val vibrationSettingKey = booleanPreferencesKey("vibrationSetting")
}