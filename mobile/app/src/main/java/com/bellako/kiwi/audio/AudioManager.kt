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

class AudioLayer(val resId: Int, val isActive: Boolean) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AudioLayer) return false
        return resId == other.resId
    }
    override fun hashCode(): Int {
        return resId.hashCode()
    }
}

private class AudioLayerPlayer(val resId: Int, var isActive: Boolean, val player: ExoPlayer, var fade: Runnable?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AudioLayer) return false
        return resId == other.resId
    }
    override fun hashCode(): Int {
        return resId.hashCode()
    }
}


object AudioManager {

    private const val DEFAULT_FADE_DURATION: Long = 3000
    private const val DEFAULT_FADE_DURATION_FAST: Long = 200

    // Enabled
    private var _isEnabled: Boolean = true

    // Volume
    private var _globalVolumeMusic: Float = 1f // 0f..1f
    private var _globalVolumeSFX: Float = 1f // 0f..1f

    // Music currently playing (looping). There may be only one music playing simultaneously.
    // But a music can have several layers, all playing at the same time, inactive ones with volume 0
    private val _currentMusic: MutableMap<Int, AudioLayerPlayer> = mutableMapOf()

    // SFXs currently playing
    private val _currentSFXs: MutableSet<AudioLayerPlayer> = mutableSetOf()

    // Event handlers
    private val _handler = Handler(Looper.getMainLooper())

    // ---------------------------------------------------------------------------------------------

    fun setEnabled(isEnabled: Boolean) {
        _isEnabled = isEnabled
    }

    fun updateGlobalVolumeMusic(newGlobalVolumeMusic: Float) {
        _globalVolumeMusic = newGlobalVolumeMusic.coerceIn(0f, 1f)
        updateCurrentMusicVolume()
    }

    fun updateGlobalVolumeSFX(newGlobalVolumeSFX: Float) {
        _globalVolumeSFX = newGlobalVolumeSFX.coerceIn(0f, 1f)
        updateCurrentSFXsVolume()
    }

    fun playMusic(context: Context, resIds: List<AudioLayer>, fadeDuration: Long = DEFAULT_FADE_DURATION) {
        play(context, resIds, AudioType.MUSIC, fadeDuration)
    }

    fun stopMusic(resId: Int, fadeDuration: Long = DEFAULT_FADE_DURATION) {
        if (_currentMusic.contains(resId)) {
            stopPlayer(_currentMusic[resId]!!, AudioType.MUSIC, fadeDuration)
        }
    }

    fun playSFX(context: Context, resId: Int) {
        play(context, listOf(AudioLayer(resId, true)), AudioType.SFX, 0)
    }

    fun onBackgroundResume() {
        for ((currentMusicResId, currentMusicPlayer) in _currentMusic) {
            playPlayer(currentMusicPlayer, AudioType.MUSIC, DEFAULT_FADE_DURATION_FAST)
        }
    }

    fun onBackgroundEnter() {
        for ((currentMusicResId, currentMusicPlayer) in _currentMusic) {
            pausePlayer(currentMusicPlayer, DEFAULT_FADE_DURATION_FAST)
        }
        for (currentSFXPlayer in _currentSFXs) {
            stopPlayer(currentSFXPlayer, AudioType.SFX, DEFAULT_FADE_DURATION_FAST)
        }
    }

    // ---------------------------------------------------------------------------------------------

    private fun play(context: Context, layers: List<AudioLayer>, type: AudioType, fadeDuration: Long) {
        if (!_isEnabled) {
            return
        }
        // Remove all currently playing musics not found in the new music
        if (type == AudioType.MUSIC) {
            for ((currentMusicResId, currentMusicPlayer) in _currentMusic) {
                if (!layers.contains(AudioLayer(currentMusicResId, true))) {
                    stopPlayer(currentMusicPlayer, AudioType.MUSIC, fadeDuration)
                }
            }
        }
        for (layer in layers) {
            // Enable/disable layers if already active music
            if (type == AudioType.MUSIC && _currentMusic.contains(layer.resId)) {
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
        fade(player, 1f, 0f, fadeDuration) {
            player.player.pause()
        }
    }

    private fun stopPlayer(player: AudioLayerPlayer, type: AudioType, fadeDuration: Long) {
        fade(player, 1f, 0f, fadeDuration) {
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
        if (player.fade != null) {
            _handler.removeCallbacks(player.fade!!)
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
        val interval = 50
        val steps = (duration / interval).toInt()
        val stepDuration = duration / steps
        val volumeDelta = (actualToVolume - actualFromVolume) / steps
        var currentStep = 0
        val runnable = object : Runnable {
            override fun run() {
                if (currentStep <= steps) {
                    player.player.volume = actualFromVolume + volumeDelta * currentStep
                    currentStep++
                    _handler.postDelayed(this, stepDuration)
                } else {
                    onComplete?.invoke()
                }
            }
        }
        _handler.post(runnable)
        player.fade = runnable
    }

    private fun updateCurrentMusicVolume() {
        for ((id, player) in _currentMusic) {
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
