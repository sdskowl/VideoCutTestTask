package com.example.videocuttesttask.features.player

import androidx.media3.exoplayer.ExoPlayer

interface Player {
    fun getPlayer(): ExoPlayer
    fun play(uri: String)
    fun pause()
    fun release()
}