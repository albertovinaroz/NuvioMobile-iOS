package com.nuvio.app.features.settings

import android.content.Context
import android.content.SharedPreferences

internal actual object HapticsSettingsStorage {
    private const val preferencesName = "nuvio_haptics_settings"
    private const val tabBarEnabledKey = "tab_bar_enabled"
    private const val interfaceEnabledKey = "interface_enabled"

    private var preferences: SharedPreferences? = null

    fun initialize(context: Context) {
        preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
    }

    actual fun loadTabBarEnabled(): Boolean? = loadBoolean(tabBarEnabledKey)

    actual fun saveTabBarEnabled(enabled: Boolean) {
        saveBoolean(tabBarEnabledKey, enabled)
    }

    actual fun loadInterfaceEnabled(): Boolean? = loadBoolean(interfaceEnabledKey)

    actual fun saveInterfaceEnabled(enabled: Boolean) {
        saveBoolean(interfaceEnabledKey, enabled)
    }

    private fun loadBoolean(key: String): Boolean? =
        preferences?.let { prefs -> if (prefs.contains(key)) prefs.getBoolean(key, true) else null }

    private fun saveBoolean(key: String, enabled: Boolean) {
        preferences?.edit()?.putBoolean(key, enabled)?.apply()
    }
}
