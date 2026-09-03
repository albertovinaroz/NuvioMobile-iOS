package com.nuvio.app.features.settings

import platform.Foundation.NSUserDefaults

internal actual object HapticsSettingsStorage {
    // Read directly by NuvioGlassTabBar.swift via UserDefaults.standard — same cross-language
    // shared-key convention as NuvioTabBarBehavior.storageKey, so the tab bar doesn't need a
    // Kotlin/Swift bridge call just to check this on every tap.
    private const val tabBarEnabledKey = "NuvioTabBarHapticsEnabled"
    private const val interfaceEnabledKey = "haptics_interface_enabled"

    actual fun loadTabBarEnabled(): Boolean? = loadBoolean(tabBarEnabledKey)

    actual fun saveTabBarEnabled(enabled: Boolean) {
        saveBoolean(tabBarEnabledKey, enabled)
    }

    actual fun loadInterfaceEnabled(): Boolean? = loadBoolean(interfaceEnabledKey)

    actual fun saveInterfaceEnabled(enabled: Boolean) {
        saveBoolean(interfaceEnabledKey, enabled)
    }

    private fun loadBoolean(key: String): Boolean? {
        val defaults = NSUserDefaults.standardUserDefaults
        return if (defaults.objectForKey(key) != null) defaults.boolForKey(key) else null
    }

    private fun saveBoolean(key: String, enabled: Boolean) {
        NSUserDefaults.standardUserDefaults.setBool(enabled, forKey = key)
    }
}
