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

private class AudioLayerPlayer(var isActive: Boolean, val player: ExoPlayer, var fade: Runnable?)


object AudioManager {

    private var globalVolumeMusic: Float = 1f // 0f..1f

    private var globalVolumeSFX: Float = 1f // 0f..1f

    // Music currently playing. Can be several layers at the same time, inactive ones with volume 0
    private val currentMusic: MutableMap<Int, AudioLayerPlayer> = mutableMapOf()

    val handler = Handler(Looper.getMainLooper())

    // ---------------------------------------------------------------------------------------------

    fun updateGlobalVolumeMusic(newGlobalVolumeMusic: Float) {
        globalVolumeMusic = newGlobalVolumeMusic.coerceIn(0f, 1f)
        updateCurrentMusicVolume()
    }

    fun updateGlobalVolumeSFX(newGlobalVolumeSFX: Float) {
        globalVolumeSFX = newGlobalVolumeSFX.coerceIn(0f, 1f)
    }

    fun playMusic(context: Context, resIds: List<AudioLayer>, fadeDuration: Long = 3000) {
        play(context, resIds, AudioType.MUSIC, fadeDuration)
    }

    fun stopMusic(resId: Int, fadeDuration: Long = 3000) {
        if (currentMusic.contains(resId)) {
            stop(currentMusic[resId]!!, fadeDuration)
        }
    }

    fun playSFX(context: Context, resId: Int, fadeDuration: Long = 0) {
        play(context, listOf(AudioLayer(resId, true)), AudioType.SFX, fadeDuration)
    }

    // ---------------------------------------------------------------------------------------------

    private fun play(context: Context, layers: List<AudioLayer>, type: AudioType, fadeDuration: Long) {
        // Remove all currently playing musics not found in the new music
        if (type == AudioType.MUSIC) {
            for ((currentMusicResId, currentMusicPlayer) in currentMusic) {
                if (!layers.contains(AudioLayer(currentMusicResId, true))) {
                    stop(currentMusicPlayer, fadeDuration)
                }
            }
        }
        for (layer in layers) {
            // Enable/disable layers if already active music
            if (type == AudioType.MUSIC && currentMusic.contains(layer.resId)) {
                val player = currentMusic[layer.resId]!!
                val fromVolume = if (layer.isActive) 0f else globalVolumeMusic
                val toVolume = if (layer.isActive) globalVolumeMusic else 0f
                if (player.isActive != layer.isActive) {
                    player.isActive = layer.isActive
                    fade(player, fromVolume, toVolume, fadeDuration)
                }
            // Start new music
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
        val actualToVolume = if (type == AudioType.MUSIC) globalVolumeMusic else globalVolumeSFX
        val layerPlayer = AudioLayerPlayer(layer.isActive, player, null)
        fade(layerPlayer, 0f, if (layer.isActive) actualToVolume else 0f, if (layer.isActive) fadeDuration else 0, null)
        player.play()
        currentMusic[layer.resId] = layerPlayer
    }

    private fun stop(player: AudioLayerPlayer, fadeDuration: Long) {
        fade(player, 1f, 0f, fadeDuration) {
            player.player.stop()
        }
    }

    private fun fade(player: AudioLayerPlayer, fromVolume: Float, toVolume: Float, duration: Long, onComplete: (() -> Unit)? = null) {
        // Stop previous fade if any
        if (player.fade != null) {
            handler.removeCallbacks(player.fade!!)
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
                    handler.postDelayed(this, stepDuration)
                } else {
                    onComplete?.invoke()
                }
            }
        }
        handler.post(runnable)
        player.fade = runnable
    }

    private fun updateCurrentMusicVolume() {
        for ((currentMusicResId, currentMusicPlayer) in currentMusic) {
            if (currentMusicPlayer.isActive) {
                fade(currentMusicPlayer, currentMusicPlayer.player.volume, globalVolumeMusic, 250)
            }
        }
    }

}
