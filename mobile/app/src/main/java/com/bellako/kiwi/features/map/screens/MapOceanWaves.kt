package com.bellako.kiwi.features.map.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.core.content.ContextCompat
import com.bellako.kiwi.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import kotlin.random.Random

// The three wave sprites (vector drawables). One is picked at random per wave.
// They are rasterised to bitmaps once (see rasterizeWave) so each wave can be
// drawn at its own arbitrary size without the VectorPainter size-cache that
// caused mis-sized / cropped sprites when one painter was reused at many sizes.
private val WAVE_DRAWABLES =
    listOf(
        R.drawable.mindveil_wave_01,
        R.drawable.mindveil_wave_02,
        R.drawable.mindveil_wave_03,
    )

// Resolution the wave vectors are rasterised at (width in px; height follows the
// vector's aspect). Waves are drawn smaller than this on screen, so it only
// needs to be big enough to stay crisp at high zoom.
private const val WAVE_RASTER_WIDTH_PX = 512

// Number of waves alive at once. Each recycles independently when its life ends.
private const val WAVE_COUNT = 120

// A wave's lifetime, randomised per wave so they don't pulse in lockstep.
private const val WAVE_LIFE_MIN_S = 2.5f
private const val WAVE_LIFE_MAX_S = 5.0f

// Fraction of the life spent fading in, and the same fading out (so the middle
// holds at full). 0.35 + 0.35 leaves a 0.30 hold.
private const val WAVE_FADE_FRACTION = 0.35f

// Wave width as a fraction of the map width; height follows the sprite's aspect
// ratio. Randomised per wave for size variety.
private const val WAVE_WIDTH_MIN_FRAC = 0.02f
private const val WAVE_WIDTH_MAX_FRAC = 0.045f

// Peak opacity of a wave (the white sprite at full strength). Lower for subtler
// foam, raise toward 1 for crisper waves.
private const val WAVE_MAX_ALPHA = 0.65f

// Total drift over a wave's whole life, as a fraction of the map size. "Slight"
// movement in one shared direction — like a gentle current. Tweak the sign of
// each to change direction.
private const val WAVE_DRIFT_X_FRAC = 0.012f
private const val WAVE_DRIFT_Y_FRAC = -0.004f

// Mask sampling. inSampleSize shrinks the 4000 x 3796 mask on decode; we then
// scan every Nth pixel for "white" (water) and keep those as spawn candidates.
private const val MASK_DECODE_SAMPLE_SIZE = 16
private const val MASK_PIXEL_STRIDE = 2
private const val MASK_WHITE_THRESHOLD = 200

private const val NANOS_PER_SECOND = 1_000_000_000f
private const val WAVE_SEED = 0x0CEA_17L

private data class Wave(
    val variant: Int,
    val xFrac: Float,
    val yFrac: Float,
    val widthFrac: Float,
    val birthSeconds: Float,
    val lifeSeconds: Float,
)

private fun spawnWave(
    rng: Random,
    oceanPoints: List<Offset>,
    now: Float,
    staggered: Boolean,
): Wave {
    val point = oceanPoints[rng.nextInt(oceanPoints.size)]
    val life = WAVE_LIFE_MIN_S + rng.nextFloat() * (WAVE_LIFE_MAX_S - WAVE_LIFE_MIN_S)
    // When seeding the initial pool, back-date the birth by a random slice of
    // the life so the waves start mid-cycle instead of all fading in together.
    val birth = if (staggered) now - rng.nextFloat() * life else now
    return Wave(
        variant = rng.nextInt(WAVE_DRAWABLES.size),
        xFrac = point.x,
        yFrac = point.y,
        widthFrac = WAVE_WIDTH_MIN_FRAC + rng.nextFloat() * (WAVE_WIDTH_MAX_FRAC - WAVE_WIDTH_MIN_FRAC),
        birthSeconds = birth,
        lifeSeconds = life,
    )
}

// Triangle-ish opacity envelope over the wave's normalised life [0, 1].
private fun waveAlpha(lifeT: Float): Float =
    when {
        lifeT < WAVE_FADE_FRACTION -> lifeT / WAVE_FADE_FRACTION
        lifeT > 1f - WAVE_FADE_FRACTION -> (1f - lifeT) / WAVE_FADE_FRACTION
        else -> 1f
    }

// Rasterise a (vector) drawable to an ImageBitmap once. A bitmap scales to any
// draw size via drawImage with no per-size cache, unlike a shared VectorPainter.
private fun rasterizeWave(
    context: Context,
    resId: Int,
): ImageBitmap {
    val drawable = ContextCompat.getDrawable(context, resId)
    val intrinsicW = (drawable?.intrinsicWidth ?: 1).coerceAtLeast(1)
    val intrinsicH = (drawable?.intrinsicHeight ?: 1).coerceAtLeast(1)
    val width = WAVE_RASTER_WIDTH_PX
    val height = (width.toFloat() * intrinsicH / intrinsicW).roundToInt().coerceAtLeast(1)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    if (drawable != null) {
        val canvas = android.graphics.Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
    }
    return bitmap.asImageBitmap()
}

// Decode the water mask small and collect normalised positions of white (water)
// pixels — the candidate spots where a wave may spawn.
private suspend fun loadOceanPoints(
    context: Context,
    maskResId: Int,
): List<Offset> =
    withContext(Dispatchers.Default) {
        val options = BitmapFactory.Options().apply { inSampleSize = MASK_DECODE_SAMPLE_SIZE }
        val bitmap =
            BitmapFactory.decodeResource(context.resources, maskResId, options)
                ?: return@withContext emptyList()
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        bitmap.recycle()

        val points = ArrayList<Offset>()
        var y = 0
        while (y < height) {
            var x = 0
            while (x < width) {
                val px = pixels[y * width + x]
                val r = (px ushr 16) and 0xFF
                val g = (px ushr 8) and 0xFF
                val b = px and 0xFF
                if (r >= MASK_WHITE_THRESHOLD && g >= MASK_WHITE_THRESHOLD && b >= MASK_WHITE_THRESHOLD) {
                    points.add(Offset(x / (width - 1f), y / (height - 1f)))
                }
                x += MASK_PIXEL_STRIDE
            }
            y += MASK_PIXEL_STRIDE
        }
        points
    }

/**
 * Ocean-wave overlay for the map. Reads [maskResourceId] (white = water) to find
 * where the ocean is, then keeps a pool of [WAVE_COUNT] wave sprites that spawn
 * at random water positions, fade in, drift slightly in one shared direction,
 * and fade out — recycling forever. Pauses while [LocalMapVfxEnabled] is false.
 *
 * Expected to be placed inside the map's transformed Box (so it pans/zooms with
 * the map) and below the node connections (so nodes stay visible on top).
 */
@Composable
fun MapOceanWaves(
    maskResourceId: Int,
    modifier: Modifier = Modifier,
) {
    if (!LocalMapVfxEnabled.current) return

    val context = LocalContext.current
    val oceanPoints by produceState(initialValue = emptyList<Offset>(), maskResourceId) {
        value = loadOceanPoints(context, maskResourceId)
    }
    if (oceanPoints.isEmpty()) return

    val waveBitmaps =
        remember(context) {
            listOf(
                rasterizeWave(context, R.drawable.mindveil_wave_01),
                rasterizeWave(context, R.drawable.mindveil_wave_02),
                rasterizeWave(context, R.drawable.mindveil_wave_03),
            )
        }
    val rng = remember { Random(WAVE_SEED) }
    val waves = remember(oceanPoints) {
        MutableList(WAVE_COUNT) { spawnWave(rng, oceanPoints, now = 0f, staggered = true) }
    }

    var elapsedSeconds by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(oceanPoints) {
        var lastNanos = 0L
        while (true) {
            withFrameNanos { nanos ->
                if (lastNanos != 0L) {
                    elapsedSeconds += (nanos - lastNanos) / NANOS_PER_SECOND
                }
                lastNanos = nanos
                // Recycle any wave whose life has run out.
                for (i in waves.indices) {
                    val w = waves[i]
                    if (elapsedSeconds - w.birthSeconds >= w.lifeSeconds) {
                        waves[i] = spawnWave(rng, oceanPoints, now = elapsedSeconds, staggered = false)
                    }
                }
            }
        }
    }

    Canvas(modifier = modifier) {
        val viewportW = size.width
        val viewportH = size.height
        if (viewportW <= 0f || viewportH <= 0f) return@Canvas

        waves.forEach { wave ->
            val lifeT = ((elapsedSeconds - wave.birthSeconds) / wave.lifeSeconds).coerceIn(0f, 1f)
            val alpha = waveAlpha(lifeT) * WAVE_MAX_ALPHA
            if (alpha <= 0f) return@forEach

            val bitmap = waveBitmaps[wave.variant]
            val width = wave.widthFrac * viewportW
            val height = width * bitmap.height / bitmap.width

            // Keep the whole sprite inside the canvas so it never reads as
            // clipped at an edge — ocean often sits right at the map border.
            val halfW = width / 2f
            val halfH = height / 2f
            val rawX = (wave.xFrac + WAVE_DRIFT_X_FRAC * lifeT) * viewportW
            val rawY = (wave.yFrac + WAVE_DRIFT_Y_FRAC * lifeT) * viewportH
            val centerX = rawX.coerceIn(halfW, (viewportW - halfW).coerceAtLeast(halfW))
            val centerY = rawY.coerceIn(halfH, (viewportH - halfH).coerceAtLeast(halfH))

            drawImage(
                image = bitmap,
                dstOffset =
                    IntOffset(
                        (centerX - halfW).roundToInt(),
                        (centerY - halfH).roundToInt(),
                    ),
                dstSize =
                    IntSize(
                        width.roundToInt().coerceAtLeast(1),
                        height.roundToInt().coerceAtLeast(1),
                    ),
                alpha = alpha,
            )
        }
    }
}
