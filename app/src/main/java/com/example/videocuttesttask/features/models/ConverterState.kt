package com.example.videocuttesttask.features.models

sealed class ConverterState {
    data class Progress(val progress: String) : ConverterState()
    data class Video(val uri: String) : ConverterState()
    data class Error(val error: String) : ConverterState()
}
