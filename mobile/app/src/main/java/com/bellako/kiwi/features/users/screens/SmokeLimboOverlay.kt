package com.bellako.kiwi.features.users.screens

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.IntSize

// Ambient "smoke limbo" overlay for the signup screens. Applies the AGSL
// SMOKE_LIMBO_SHADER_SRC via a Compose graphicsLayer RenderEffect (API 33+).
// The caller (SignUpScreen) stacks this over the onboarding backdrop but under
// the UI, so the haze reads as atmosphere on the backdrop while text/forms stay
// fully legible.
//
// On API 25–32, RuntimeShader doesn't exist, so the composable emits nothing
// and the backdrop renders unmodified — a graceful no-op, the same fallback
// MapWaterOverlay uses.
//
// Animation gate: the per-frame time loop is skipped in Compose previews
// (LocalInspectionMode) and whenever LocalSignupVfxEnabled is false. Tests set
// that flag to false so the otherwise-infinite withFrameNanos loop can't keep
// the Compose runtime non-idle and hang waitForIdle (same precedent as
// LocalLoginBackgroundAnimated in LogInScreen). When gated off the shader still
// renders a single static frame (iTime = 0), so previews show a still wisp.

private const val NANOS_PER_SECOND = 1_000_000_000f

// Tests provide `false` to freeze the animation (see comment above). Defaults to
// true so the effect animates in the running app.
internal val LocalSignupVfxEnabled = staticCompositionLocalOf { true }

@Composable
fun SmokeLimboOverlay(modifier: Modifier = Modifier) {
    // Compose previews (Layoutlib) can't render a RuntimeShader — skip there so
    // the per-screen @Preview composables still draw the backdrop cleanly.
    if (LocalInspectionMode.current) return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        SmokeShaderOverlay(modifier = modifier)
    }
    // Pre-API-33: no overlay. The signup backdrop renders as before.
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun SmokeShaderOverlay(modifier: Modifier) {
    val animate = LocalSignupVfxEnabled.current

    val shader = remember { RuntimeShader(SMOKE_LIMBO_SHADER_SRC) }

    // Canvas size as Compose state. The graphicsLayer block reads it, so once
    // onSizeChanged fires after layout the block re-runs with the correct
    // iResolution before the RenderEffect is built. Without this the first frame
    // would divide fragCoord by zero → NaN coords → garbage.
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    // Time driver for the drift. withFrameNanos integrates a monotonic clock —
    // same pattern as MapWaterOverlay. Reading the resulting state inside the
    // graphicsLayer block invalidates the layer each frame so the iTime uniform
    // is pushed through. Skipped entirely when gated off (previews / tests),
    // leaving iTime at 0 → a static frame.
    var elapsedSeconds by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(animate) {
        if (!animate) return@LaunchedEffect
        var lastNanos = 0L
        while (true) {
            withFrameNanos { nanos ->
                if (lastNanos != 0L) {
                    elapsedSeconds += (nanos - lastNanos) / NANOS_PER_SECOND
                }
                lastNanos = nanos
            }
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .onSizeChanged { canvasSize = it }
                .graphicsLayer {
                    if (canvasSize.width > 0 && canvasSize.height > 0) {
                        shader.setFloatUniform(
                            "iResolution",
                            canvasSize.width.toFloat(),
                            canvasSize.height.toFloat(),
                        )
                        shader.setFloatUniform("iTime", elapsedSeconds)
                        renderEffect =
                            RenderEffect
                                .createRuntimeShaderEffect(shader, "content")
                                .asComposeRenderEffect()
                    } else {
                        renderEffect = null
                    }
                }
                .drawBehind {
                    // The renderEffect needs the layer to produce content to
                    // filter. The shader ignores this fill and paints its own
                    // premultiplied smoke (transparent where there is none), so
                    // the black never actually shows — it just gives the layer
                    // something to rasterize. Skip until the size is known.
                    if (canvasSize.width > 0 && canvasSize.height > 0) {
                        drawRect(Color.Black)
                    }
                },
    )
}
