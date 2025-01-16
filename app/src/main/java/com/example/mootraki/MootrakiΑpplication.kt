package com.example.mootraki

import android.app.Application
import com.example.mootraki.data.AppContainer
import com.example.mootraki.data.AppDataContainer

/**
 * Application class for initializing global dependencies.
 *
 * This class is responsible for creating and storing an instance of [AppContainer],
 * which provides dependencies to other parts of the application.
 */
class MootrakiApplication : Application() {

    /**
     * [AppContainer] instance used by other classes to obtain dependencies.
     * Initialized in [onCreate].
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Initialize the AppContainer with the application context
        container = AppDataContainer(this)
    }
}
