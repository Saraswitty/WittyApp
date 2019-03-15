package com.saraswitty.wittyapp

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import mu.KotlinLogging

class MyApplication: Application() {
    private val logger = KotlinLogging.logger {}

    override fun onCreate() {
        super.onCreate()
        logger.debug{"Initializing Facebook SDK"}
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
    }
}