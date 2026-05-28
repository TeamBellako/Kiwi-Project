package com.bellako.kiwi.features.map.screens

import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.annotation.DrawableRes
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
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize

// Animated water VFX overlay for the map. Applies an AGSL RuntimeShader via a
// Compose graphicsLayer RenderEffect (API 33+). The caller is expected to size
// this to the map image and wrap it in the same transformed parent as the
// Kiwi_Image, so the shader inherits pan/zoom automatically and the foam stays
// anchored to water bodies on the map.
//
// On API 25–32, RuntimeShader doesn't exist. The composable emits nothing and
// the map renders as the unmodified JPEG — graceful no-op fallback (Phase 6).
//
// The water mask is a committed PNG drawable (one per map). Convention:
// white pixels = water (shader effect applies), black pixels = land (shader
// passes through). The mask is decoded with inScaled = false to bypass DPI
// scaling that would otherwise upscale a drawable/ resource into hundreds of
// megabytes on high-density devices.

private const val NANOS_PER_SECOND = 1_000_000_000f

@Composable
fun MapWaterOverlay(
    @DrawableRes maskResourceId: Int,
    modifier: Modifier = Modifier,
) {
    // Same gate as MapMist / MapClouds: when the map is fully covered
    // (full-screen conversation, combat) or VFX are disabled for tests, skip
    // the shader entirely so its per-frame loop and GPU work go away.
    if (!LocalMapVfxEnabled.current) return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        WaterShaderOverlay(maskResourceId = maskResourceId, modifier = modifier)
    }
    // Pre-API-33: no overlay. A static foam PNG could slot in here later.
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun WaterShaderOverlay(
    @DrawableRes maskResourceId: Int,
    modifier: Modifier,
) {
    val context = LocalContext.current

    val shader =
        remember(maskResourceId) {
            val opts =
                BitmapFactory.Options().apply {
                    inScaled = false
                    inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888
                }
            val maskBitmap =
                BitmapFactory.decodeResource(context.resources, maskResourceId, opts)
                    ?: error("Failed to decode water mask resource: $maskResourceId")
            RuntimeShader(WATER_SHADER_SRC).apply {
                setInputShader(
                    "mask",
                    BitmapShader(maskBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP),
                )
                setFloatUniform(
                    "maskResolution",
                    maskBitmap.width.toFloat(),
                    maskBitmap.height.toFloat(),
                )
            }
        }

    // Canvas size as Compose state. The graphicsLayer block reads it, so when
    // onSizeChanged fires after layout, the block re-runs with the correct
    // iResolution before the RenderEffect is recreated. Without this, the
    // first frame would compute the effect with iResolution = (0, 0) →
    // fragCoord divided by zero → NaN/Inf mask coords → garbage everywhere.
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    // Time driver for the foam animation. withFrameNanos integrates a
    // monotonic clock — same pattern as MapClouds.kt. Reading the resulting
    // state inside the graphicsLayer block invalidates the layer each frame
    // so the iTime uniform is pushed through and the RenderEffect re-records.
    var elapsedSeconds by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
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
                    // filter. Skip the fill until the size is known so we
                    // don't show a frame of solid black before the shader
                    // takes over.
                    if (canvasSize.width > 0 && canvasSize.height > 0) {
                        drawRect(Color.Black)
                    }
                },
    )
}
