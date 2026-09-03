package com.nuvio.app.core.haptics

// Compose's standard haptic API (LocalHapticFeedback) is a no-op on iOS in this Compose
// Multiplatform version — real device feedback there needs a native UIFeedbackGenerator, same
// reasoning as ProfileHoverHapticFeedback. This one is for a firmer "bump/landing" feel
// (UIImpactFeedbackGenerator) rather than that one's light selection tick.
internal expect object NativeImpactHapticFeedback {
    fun perform()
}
