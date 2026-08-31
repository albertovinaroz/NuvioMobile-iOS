package com.nuvio.app

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.nuvio.app.core.ui.nuvio
import com.nuvio.app.features.settings.AppBrandWordmark
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

// How long the intro is guaranteed to stay on screen from process start, regardless of how
// quickly auth/profile state actually resolves underneath it — see the hold logic in AppGate.kt.
// Long enough for the reveal below to fully settle (spring scale ~700-900ms) plus a beat to
// actually register as a brand moment, short enough to never read as an imposed delay.
internal const val AppIntroMinDurationMs = 1300L

/**
 * A brief branded reveal shown in place of AppGate's old generic spinner while the app resolves
 * auth/profile state on cold start. Deliberately simple — no arbitrary flourishes swiped from a
 * scrapped/rejected mp4 mockup, just the existing wordmark asset given a soft, confident entrance
 * with the same spring language `AppLoadingContent`'s profile-switch entrance already uses.
 */
@Composable
internal fun AppIntroContent(modifier: Modifier = Modifier) {
    val wordmarkAlpha = remember { Animatable(0f) }
    val wordmarkScale = remember { Animatable(0.55f) }
    val glowAlpha = remember { Animatable(0f) }

    // Slow, gentle drift so the glow reads as alive behind the logo instead of a static smudge —
    // one full loop every 7s, an ellipse rather than a circle so it doesn't feel mechanical.
    val driftAngle by rememberInfiniteTransition(label = "intro_glow_drift").animateFloat(
        initialValue = 0f,
        targetValue = (2 * kotlin.math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "intro_glow_drift_angle",
    )
    val density = LocalDensity.current
    val driftRadiusPx = with(density) { 36.dp.toPx() }

    LaunchedEffect(Unit) {
        launch {
            glowAlpha.animateTo(1f, tween(durationMillis = 600, easing = FastOutSlowInEasing))
        }
        launch {
            wordmarkAlpha.animateTo(1f, tween(durationMillis = 350, easing = FastOutSlowInEasing))
        }
        // Low stiffness on purpose: a snappier spring here resolves so fast against the small
        // scale delta that the motion barely registers before it's already settled — this one
        // takes ~700-900ms to visibly grow and settle with a bit of overshoot, so it actually
        // reads as an animation instead of the logo just appearing.
        wordmarkScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = 120f,
            ),
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.nuvio.colors.background),
        contentAlignment = Alignment.Center,
    ) {
        // A soft glow baked directly into the gradient's own color stops (fully transparent
        // well inside the box's bounds) rather than a plain circle pushed through Modifier.blur()
        // — blur's edge treatment clips to a rectangle on some platforms/backends no matter what,
        // which read as a visible box around the glow instead of a soft radial falloff.
        val accent = MaterialTheme.nuvio.colors.accent
        Box(
            modifier = Modifier
                .size(190.dp)
                .graphicsLayer {
                    alpha = glowAlpha.value
                    translationX = cos(driftAngle) * driftRadiusPx
                    translationY = sin(driftAngle) * driftRadiusPx * 0.6f
                }
                .background(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.0f to accent.copy(alpha = 0.28f),
                            0.35f to accent.copy(alpha = 0.14f),
                            0.7f to accent.copy(alpha = 0.03f),
                            1.0f to Color.Transparent,
                        ),
                    ),
                ),
        )
        AppBrandWordmark(
            modifier = Modifier
                .height(52.dp)
                .graphicsLayer {
                    alpha = wordmarkAlpha.value
                    scaleX = wordmarkScale.value
                    scaleY = wordmarkScale.value
                },
        )
    }
}
