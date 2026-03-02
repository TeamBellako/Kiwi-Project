package com.bellako.kiwi.audio

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.mutableStateListOf
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

data class AudioLayer(
    val resId: Int,
    var baseVolume: Float = 1f,
    var isActive: Boolean,
)

class AudioLayerPlayer(
    var layer: AudioLayer,
    val player: ExoPlayer,
    var fade: Runnable?,
)

private const val DEFAULT_FADE_DURATION = 1000L
private const val DEFAULT_FADE_DURATION_FAST = 100L
private const val FADE_IN_INTERVAL = 50L

/**
 * For centralized audio management. Music, SFXs and volume.
 */
class AudioManagerBase {
    // Enabled
    private var isEnabled: Boolean = true

    // Volume
    private var globalVolume: Float = 1f // 0f..1f

    // Layers currently playing
    private val currentLayers: MutableMap<Int, AudioLayerPlayer> = mutableMapOf()

    // Event handlers
    private val handler = Handler(Looper.getMainLooper())

    // ---------------------------------------------------------------------------------------------

    /** Getter layers. */
    fun getLayers(): MutableMap<Int, AudioLayerPlayer> = currentLayers

    /** Disables the whole audio. Used for android tests. */
    fun setEnabled(isEnabled: Boolean) {
        this.isEnabled = isEnabled
    }

    /** Updates the volume of the whole manager. */
    fun updateGlobalVolume(newGlobalVolume: Float) {
        globalVolume = newGlobalVolume.coerceIn(0f, 1f)
        updateCurrentLayersVolume()
    }

    /**
     * Should be called when the app goes to background or screen turns off.
     * Pause all the layers if any.
     */
    fun onBackgroundEnter() {
        pauseAll()
    }

    /**
     * Should be called when the app comes to foreground.
     * Resume all the layers if any.
     */
    fun onBackgroundResume() {
        for ((_, player) in currentLayers) {
            playLayer(player, DEFAULT_FADE_DURATION_FAST)
        }
    }

    /** Updates a layer, active or not. Creates a player or fades (in or our) if already existing. */
    fun updateOrCreateLayer(
        context: Context,
        layer: AudioLayer,
        fadeDuration: Long,
        looping: Boolean,
    ) {
        if (!isEnabled) {
            return
        }

        val targetVolume = if (layer.isActive) globalVolume * layer.baseVolume else 0f

        val existingLayerPlayer = currentLayers[layer.resId]
        if (existingLayerPlayer != null) {
            existingLayerPlayer.layer.isActive = layer.isActive
            existingLayerPlayer.layer.baseVolume = layer.baseVolume
            fade(existingLayerPlayer, existingLayerPlayer.player.volume, targetVolume, fadeDuration)
            return
        }

        val player = ExoPlayer.Builder(context).build()
        val uri =
            Uri
                .Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(context.packageName)
                .appendPath(layer.resId.toString())
                .build()
        player.setMediaItem(MediaItem.fromUri(uri))
        player.repeatMode = if (looping) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
        player.prepare()
        val newLayerPlayer = AudioLayerPlayer(layer, player, null)

        if (!looping) {
            player.addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        if (state == Player.STATE_ENDED) {
                            stopLayer(newLayerPlayer, 0)
                        }
                    }
                },
            )
        }

        playLayer(newLayerPlayer, fadeDuration)
        currentLayers.put(layer.resId, newLayerPlayer)
    }

    /** Stops a layer and removes it. */
    fun removeLayer(
        layer: AudioLayer,
        fadeDuration: Long,
    ) {
        val layerPlayer = currentLayers[layer.resId]
        if (layerPlayer != null) {
            stopLayer(layerPlayer, fadeDuration)
        }
    }

    /** Pause all current layers. */
    fun pauseAll(fadeDuration: Long = DEFAULT_FADE_DURATION_FAST) {
        for ((_, player) in currentLayers) {
            pauseLayer(player, fadeDuration)
        }
    }

    // ---------------------------------------------------------------------------------------------

    private fun playLayer(
        layerPlayer: AudioLayerPlayer,
        fadeDuration: Long,
    ) {
        val targetVolume =
            if (layerPlayer.layer.isActive) {
                globalVolume * layerPlayer.layer.baseVolume
            } else {
                0f
            }
        val fadeDuration = if (layerPlayer.layer.isActive) fadeDuration else 0
        fade(layerPlayer, 0f, targetVolume, fadeDuration, null)
        layerPlayer.player.play()
    }

    private fun pauseLayer(
        layerPlayer: AudioLayerPlayer,
        fadeDuration: Long,
    ) {
        fade(layerPlayer, layerPlayer.player.volume, 0f, fadeDuration) {
            layerPlayer.player.pause()
        }
    }

    private fun stopLayer(
        layerPlayer: AudioLayerPlayer,
        fadeDuration: Long,
    ) {
        fade(layerPlayer, layerPlayer.player.volume, 0f, fadeDuration) {
            layerPlayer.player.stop()
            currentLayers.remove(layerPlayer.layer.resId)
        }
    }

    private fun fade(
        player: AudioLayerPlayer,
        fromVolume: Float,
        toVolume: Float,
        duration: Long,
        onComplete: (() -> Unit)? = null,
    ) {
        // Stop previous fade if any
        player.fade?.let { fade ->
            handler.removeCallbacks(fade)
            onComplete?.invoke()
        }
        // Check and clamp values
        val actualFromVolume = fromVolume.coerceIn(0f, 1f)
        val actualToVolume = toVolume.coerceIn(0f, 1f)
        if (duration <= 0) {
            player.player.volume = actualToVolume
            onComplete?.invoke()
            return
        }
        // Start fade
        player.player.volume = actualFromVolume
        val interval = FADE_IN_INTERVAL
        val steps = (duration / interval).toInt().coerceAtLeast(1)
        val stepDuration = duration / steps
        val volumeDelta = (actualToVolume - actualFromVolume) / steps
        var currentStep = 0
        val runnable =
            object : Runnable {
                override fun run() {
                    if (currentStep <= steps) {
                        player.player.volume = (actualFromVolume + volumeDelta * currentStep).coerceIn(0f, 1f)
                        currentStep++
                        handler.postDelayed(this, stepDuration)
                    } else {
                        onComplete?.invoke()
                    }
                }
            }
        player.fade = runnable
        handler.post(runnable)
    }

    private fun updateCurrentLayersVolume() {
        for ((_, player) in currentLayers) {
            if (player.layer.isActive) {
                val targetVolume = globalVolume * player.layer.baseVolume
                fade(player, player.player.volume, targetVolume, DEFAULT_FADE_DURATION_FAST)
            }
        }
    }
}

/**
 * For centralized audio management. Plays music, SFXs and manages the global volume of the app.
 */
object AudioManager {
    val musicManager = AudioManagerBase()
    val sfxManager = AudioManagerBase()

    /** Disables the whole audio. Used for android tests. */
    fun setEnabled(isEnabled: Boolean) {
        musicManager.setEnabled(isEnabled)
        sfxManager.setEnabled(isEnabled)
    }

    /** Updates the music volume of the whole app. */
    fun updateGlobalVolumeMusic(newGlobalVolumeMusic: Float) {
        musicManager.updateGlobalVolume(newGlobalVolumeMusic)
    }

    /** Updates the SFXs volume of the whole app. */
    fun updateGlobalVolumeSFX(newGlobalVolumeSFX: Float) {
        sfxManager.updateGlobalVolume(newGlobalVolumeSFX)
    }

    /**
     * Plays a new music looping.
     * There may be only one music playing simultaneously, so if there is any already,
     * it will fade out while the new one is fading in.
     * A music can have several layers, all playing at the same time, inactive ones with volume 0.
     * Use this function also to enable/disable layers.
     */
    fun playMusic(
        context: Context,
        layers: List<AudioLayer>,
        fadeDuration: Long = DEFAULT_FADE_DURATION,
    ) {
        val layersToRemove = mutableStateListOf<AudioLayer>()
        for (existingLayer in musicManager.getLayers()) {
            if (!(layers.map { audioLayer -> audioLayer.resId }.contains(existingLayer.key))) {
                layersToRemove.add(
                    AudioLayer(
                        existingLayer.value.layer.resId,
                        existingLayer.value.layer.baseVolume,
                        existingLayer.value.layer.isActive,
                    ),
                )
            }
        }
        for (layerToRemove in layersToRemove) {
            musicManager.removeLayer(layerToRemove, fadeDuration)
        }

        // Add or update
        for (newLayer in layers) {
            musicManager.updateOrCreateLayer(context, newLayer, fadeDuration, true)
        }
    }

    /** Plays a new SFX once */
    fun playSFX(
        context: Context,
        resId: Int,
        baseVolume: Float = 1f,
    ) {
        sfxManager.updateOrCreateLayer(context, AudioLayer(resId, baseVolume, true), 0, false)
    }

    /**
     * Should be called when the app goes to background or screen turns off.
     * Pause the music and SFXs if any.
     */
    fun onBackgroundEnter() {
        musicManager.onBackgroundEnter()
        sfxManager.onBackgroundEnter()
    }

    /**
     * Should be called when the app comes to foreground.
     * Resume the music if any.
     */
    fun onBackgroundResume() {
        musicManager.onBackgroundResume()
        sfxManager.onBackgroundResume()
    }
}
