package com.example.videocuttesttask.di

import android.content.Context
import com.example.videocuttesttask.features.converter.Converter
import com.example.videocuttesttask.features.converter.ConverterImpl
import com.example.videocuttesttask.features.player.Player
import com.example.videocuttesttask.features.player.PlayerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    @Singleton
    @Provides
    fun providePlayer(@ApplicationContext context: Context): Player = PlayerImpl(context)

    @Singleton
    @Provides
    fun provideConverter(@ApplicationContext context: Context): Converter = ConverterImpl(context)
}