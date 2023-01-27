package com.example.videocuttesttask.features.player

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class PlayerImpl(private val context: Context) : Player {
    private val TAG = "PlayerImpl"
    private var player: ExoPlayer? = null
    private var currentItem = 0
    private var playbackPosition = 0L
    private var lastUri = ""
    override fun getPlayer(): ExoPlayer {
        player = ExoPlayer.Builder(
            context
        ).build()
        return player!!
    }

    override fun play(uri: String) {
        val mediaItem = MediaItem.fromUri(uri)
        player?.setMediaItem(mediaItem)
        player?.playWhenReady = true
        Log.d(TAG, "currentItem = $currentItem, playbackPosition = $playbackPosition")
        if (lastUri == uri) {
            player?.seekTo(currentItem, playbackPosition)
        } else {
            lastUri = uri
        }

        player?.prepare()
    }

    override fun pause() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            exoPlayer.stop()
            exoPlayer.release()
        }
        player = null
    }

    override fun release() {
        player?.let { exoPlayer ->
            exoPlayer.stop()
            exoPlayer.release()
            playbackPosition = 0
            currentItem = 0
        }
        player = null
    }
}