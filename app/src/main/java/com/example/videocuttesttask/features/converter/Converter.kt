package com.example.videocuttesttask.features.converter

import android.net.Uri
import com.example.videocuttesttask.features.models.ConverterState
import kotlinx.coroutines.flow.MutableStateFlow

interface Converter {

    val converterState: MutableStateFlow<ConverterState?>
    fun convert(uri: Uri)
    fun cancel()
}