package com.nuvio.app.core.haptics

// Android already gets a real vibration from the standard Compose haptic API at the call site —
// this native path only exists to cover iOS, so it's a no-op here.
internal actual object NativeImpactHapticFeedback {
    actual fun perform() = Unit
}
