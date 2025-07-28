package com.bellako.kiwi.audio

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player

private enum class AudioType { MUSIC, SFX }

data class AudioLayer(val resId: Int, val isActive: Boolean)

private class AudioLayerPlayer(val resId: Int, var isActive: Boolean, val player: ExoPlayer, var fade: Runnable?)


/**
 * For centralized audio management. Music, SFXs and volume.
 */
object AudioManager {

    private const val DEFAULT_FADE_DURATION = 2000L
    private const val DEFAULT_FADE_DURATION_FAST = 200L

    // Enabled
    private var _isEnabled: Boolean = true

    // Volume
    private var _globalVolumeMusic: Float = 1f // 0f..1f
    private var _globalVolumeSFX: Float = 1f // 0f..1f

    // Music (layers) currently playing. See @playMusic()
    private val _currentMusic: MutableMap<Int, AudioLayerPlayer> = mutableMapOf()

    // SFXs currently playing. See @playSFX()
    private val _currentSFXs: MutableSet<AudioLayerPlayer> = mutableSetOf()

    // Event handlers
    private val _handler = Handler(Looper.getMainLooper())

    // ---------------------------------------------------------------------------------------------

    /** Disables the whole audio. Used for android tests. */
    fun setEnabled(isEnabled: Boolean) {
        _isEnabled = isEnabled
    }

    /** Updates the music volume of the whole app. */
    fun updateGlobalVolumeMusic(newGlobalVolumeMusic: Float) {
        _globalVolumeMusic = newGlobalVolumeMusic.coerceIn(0f, 1f)
        updateCurrentMusicVolume()
    }

    /** Updates the SFXs volume of the whole app. */
    fun updateGlobalVolumeSFX(newGlobalVolumeSFX: Float) {
        _globalVolumeSFX = newGlobalVolumeSFX.coerceIn(0f, 1f)
        updateCurrentSFXsVolume()
    }

    /**
     * Plays a new music looping.
     * There may be only one music playing simultaneously, so if there is any already,
     * it will fade out while the new one is fading in.
     * A music can have several layers, all playing at the same time, inactive ones with volume 0.
     * Use this function also to enable/disable layers.
     */
    fun playMusic(context: Context, resIds: List<AudioLayer>, fadeDuration: Long = DEFAULT_FADE_DURATION) {
        play(context, resIds, AudioType.MUSIC, fadeDuration)
    }

    /** Stops the music currently playing (every layer) */
    fun stopMusic(fadeDuration: Long = DEFAULT_FADE_DURATION) {
        for ((_, player) in _currentMusic) {
            stopPlayer(player, AudioType.MUSIC, fadeDuration)
        }
    }

    /** Plays a new SFX once */
    fun playSFX(context: Context, resId: Int) {
        play(context, listOf(AudioLayer(resId, true)), AudioType.SFX, 0)
    }

    /**
     * Should be called when the app comes to foreground.
     * Resume the music if any.
     */
    fun onBackgroundResume() {
        for ((_, player) in _currentMusic) {
            playPlayer(player, AudioType.MUSIC, DEFAULT_FADE_DURATION_FAST)
        }
    }

    /**
     * Should be called when the app goes to background or screen turns off.
     * Pause the music and SFXs if any.
     */
    fun onBackgroundEnter() {
        for ((_, player) in _currentMusic) {
            pausePlayer(player, DEFAULT_FADE_DURATION_FAST)
        }
        for (player in _currentSFXs) {
            stopPlayer(player, AudioType.SFX, DEFAULT_FADE_DURATION_FAST)
        }
    }

    // ---------------------------------------------------------------------------------------------

    private fun play(context: Context, layers: List<AudioLayer>, type: AudioType, fadeDuration: Long) {
        if (!_isEnabled) {
            return
        }
        // Remove all currently playing musics not found in the new music
        if (type == AudioType.MUSIC) {
            for ((currentMusicResId, currentMusicPlayer) in _currentMusic.toMap()) {
                if (!layers.any { it.resId == currentMusicResId }) {
                    stopPlayer(currentMusicPlayer, AudioType.MUSIC, fadeDuration)
                }
            }
        }
        for (layer in layers) {
            // Enable/disable layers if already active music
            if (type == AudioType.MUSIC && _currentMusic.containsKey(layer.resId)) {
                val player = _currentMusic[layer.resId]!!
                val fromVolume = if (layer.isActive) 0f else _globalVolumeMusic
                val toVolume = if (layer.isActive) _globalVolumeMusic else 0f
                if (player.isActive != layer.isActive) {
                    player.isActive = layer.isActive
                    fade(player, fromVolume, toVolume, fadeDuration)
                }
            // Start new music or SFX
            } else {
                addLayer(context, layer, type, fadeDuration)
            }
        }
    }

    private fun addLayer(context: Context, layer: AudioLayer, type: AudioType, fadeDuration: Long) {
        val player = ExoPlayer.Builder(context).build()
        val uri = Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE).authority(context.packageName).appendPath(layer.resId.toString()).build()
        player.setMediaItem(MediaItem.fromUri(uri))
        player.repeatMode = if (type == AudioType.MUSIC) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
        player.prepare()
        val layerPlayer = AudioLayerPlayer(layer.resId, layer.isActive, player, null)
        playPlayer(layerPlayer, type, fadeDuration)

        if (type == AudioType.MUSIC) {
            _currentMusic[layer.resId] = layerPlayer
        } else {
            _currentSFXs.add(layerPlayer)
            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        stopPlayer(layerPlayer, AudioType.SFX, 0)
                    }
                }
            })
        }
    }

    private fun playPlayer(player: AudioLayerPlayer, type: AudioType, fadeDuration: Long) {
        val actualToVolume = if (type == AudioType.MUSIC) _globalVolumeMusic else _globalVolumeSFX
        fade(player, 0f, if (player.isActive) actualToVolume else 0f, if (player.isActive) fadeDuration else 0, null)
        player.player.play()
    }

    private fun pausePlayer(player: AudioLayerPlayer, fadeDuration: Long) {
        fade(player, player.player.volume, 0f, fadeDuration) {
            player.player.pause()
        }
    }

    private fun stopPlayer(player: AudioLayerPlayer, type: AudioType, fadeDuration: Long) {
        fade(player, player.player.volume, 0f, fadeDuration) {
            player.player.stop()

            if (type == AudioType.MUSIC) {
                _currentMusic.remove(player.resId)
            } else {
                _currentSFXs.remove(player)
            }
        }
    }

    private fun fade(player: AudioLayerPlayer, fromVolume: Float, toVolume: Float, duration: Long, onComplete: (() -> Unit)? = null) {
        // Stop previous fade if any
        player.fade?.let { fade ->
            _handler.removeCallbacks(fade)
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
        val interval = 50L
        val steps = (duration / interval).toInt().coerceAtLeast(1)
        val stepDuration = duration / steps
        val volumeDelta = (actualToVolume - actualFromVolume) / steps
        var currentStep = 0
        val runnable = object : Runnable {
            override fun run() {
                if (currentStep <= steps) {
                    player.player.volume = (actualFromVolume + volumeDelta * currentStep).coerceIn(0f, 1f)
                    currentStep++
                    _handler.postDelayed(this, stepDuration)
                } else {
                    onComplete?.invoke()
                }
            }
        }
        player.fade = runnable
        _handler.post(runnable)
    }

    private fun updateCurrentMusicVolume() {
        for ((_, player) in _currentMusic) {
            if (player.isActive) {
                fade(player, player.player.volume, _globalVolumeMusic, DEFAULT_FADE_DURATION_FAST)
            }
        }
    }

    private fun updateCurrentSFXsVolume() {
        for (player in _currentSFXs) {
            fade(player, player.player.volume, _globalVolumeSFX, DEFAULT_FADE_DURATION_FAST)
        }
    }

}
