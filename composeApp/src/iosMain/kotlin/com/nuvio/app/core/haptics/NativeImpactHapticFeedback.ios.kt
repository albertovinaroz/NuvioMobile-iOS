package com.nuvio.app.core.haptics

import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

internal actual object NativeImpactHapticFeedback {
    private val generator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)

    actual fun perform() {
        generator.impactOccurred()
        generator.prepare()
    }
}
