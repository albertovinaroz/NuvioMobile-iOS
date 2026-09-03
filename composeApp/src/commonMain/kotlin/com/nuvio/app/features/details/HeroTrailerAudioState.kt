package com.nuvio.app.features.details

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Whichever hero (Home's carousel or a Details screen's hero) currently has a trailer
 * ready-and-playing marks itself [visible] here, and clears it again the moment its own trailer
 * stops being shown. Home and a Details screen are never on-screen together, so this always
 * reflects the one hero mute control that could plausibly be visible right now, never two
 * competing ones — see [HeroTrailerMuteController] for how the native (iOS) mute button consumes
 * this instead of Compose's own.
 */
object HeroTrailerAudioState {
    private val _muted = MutableStateFlow(true)
    val muted: StateFlow<Boolean> = _muted.asStateFlow()

    private val _visible = MutableStateFlow(false)
    val visible: StateFlow<Boolean> = _visible.asStateFlow()

    fun toggleMuted() {
        _muted.value = !_muted.value
    }

    fun setVisible(visible: Boolean) {
        _visible.value = visible
    }
}

/**
 * Bridges [HeroTrailerAudioState] to a native SwiftUI mute button (see HeroTrailerMuteButton in
 * ContentView.swift) — mirrors [NativeProfileSwitcherController][com.nuvio.app.core.ui.NativeProfileSwitcherController]'s
 * observe/stop lifecycle so Swift can start collecting on `.onAppear` and cancel on
 * `.onDisappear` without leaking a coroutine per navigation. A fresh instance per SwiftUI view is
 * fine — every instance just observes the same underlying [HeroTrailerAudioState] singleton.
 */
class HeroTrailerMuteController {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var observationJob: Job? = null

    fun observeState(callback: (visible: Boolean, muted: Boolean) -> Unit) {
        observationJob?.cancel()
        observationJob = scope.launch {
            combine(HeroTrailerAudioState.visible, HeroTrailerAudioState.muted) { visible, muted ->
                visible to muted
            }.collect { (visible, muted) -> callback(visible, muted) }
        }
    }

    fun stopObserving() {
        observationJob?.cancel()
        observationJob = null
    }

    fun toggleMuted() {
        HeroTrailerAudioState.toggleMuted()
    }
}
