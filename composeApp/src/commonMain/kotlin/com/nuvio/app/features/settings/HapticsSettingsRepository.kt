package com.nuvio.app.features.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Two independent toggles under Settings > Advanced: one for the tab bar's tap feedback, one for
 * every other interface haptic added this cycle (profile selection tap/exit, the profile-tab
 * landing bounce, season-complete). Deliberately not wired into every haptic in the app — action
 * buttons, long-presses, etc. stay as-is; these two only gate the ambient polish haptics.
 */
internal object HapticsSettingsRepository {
    private val _tabBarEnabled = MutableStateFlow(true)
    val tabBarEnabled: StateFlow<Boolean> = _tabBarEnabled.asStateFlow()

    private val _interfaceEnabled = MutableStateFlow(true)
    val interfaceEnabled: StateFlow<Boolean> = _interfaceEnabled.asStateFlow()

    private var hasLoaded = false

    fun ensureLoaded() {
        if (hasLoaded) return
        hasLoaded = true
        _tabBarEnabled.value = HapticsSettingsStorage.loadTabBarEnabled() ?: true
        _interfaceEnabled.value = HapticsSettingsStorage.loadInterfaceEnabled() ?: true
    }

    fun setTabBarEnabled(enabled: Boolean) {
        ensureLoaded()
        if (_tabBarEnabled.value == enabled) return
        _tabBarEnabled.value = enabled
        HapticsSettingsStorage.saveTabBarEnabled(enabled)
    }

    fun setInterfaceEnabled(enabled: Boolean) {
        ensureLoaded()
        if (_interfaceEnabled.value == enabled) return
        _interfaceEnabled.value = enabled
        HapticsSettingsStorage.saveInterfaceEnabled(enabled)
    }
}
