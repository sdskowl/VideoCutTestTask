package com.example.videocuttesttask.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.example.videocuttesttask.features.converter.Converter
import com.example.videocuttesttask.features.models.ConverterState
import com.example.videocuttesttask.features.player.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val player: Player,
    private val converter: Converter
) : ViewModel() {
    private val converterStateMutable = converter.converterState
    val convertState: StateFlow<ConverterState?> get() = converterStateMutable
    fun player(): ExoPlayer = player.getPlayer()

    fun play(uri: String) {
        player.play(uri)
    }

    fun release() {
        player.release()
    }

    fun pause() {
        player.pause()
    }

    fun convert(uri: Uri) {
        viewModelScope.launch {
            converter.convert(uri)
        }
    }

    fun cancelConverter() {
        converter.cancel()
    }

    fun clearState() {
        converterStateMutable.value = null
    }
}