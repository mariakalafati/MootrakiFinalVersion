package com.example.mootraki

import android.app.Application
import com.example.mootraki.data.AppContainer
import com.example.mootraki.data.AppDataContainer

class MootrakiApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}