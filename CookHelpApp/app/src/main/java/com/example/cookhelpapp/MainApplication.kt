package com.example.cookhelpapp

import android.app.Application
import com.example.cookhelpapp.di.moduloRed
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun  onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(moduloRed)
        }
    }
}