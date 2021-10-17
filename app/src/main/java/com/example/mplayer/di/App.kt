package com.example.mplayer.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class App : Application() {
    companion object {
        val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

}