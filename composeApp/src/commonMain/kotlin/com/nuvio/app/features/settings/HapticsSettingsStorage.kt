package com.nuvio.app.features.settings

internal expect object HapticsSettingsStorage {
    fun loadTabBarEnabled(): Boolean?
    fun saveTabBarEnabled(enabled: Boolean)
    fun loadInterfaceEnabled(): Boolean?
    fun saveInterfaceEnabled(enabled: Boolean)
}
