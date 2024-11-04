package com.mashnu.geofencedemo

import android.app.Application
import com.clevertap.android.sdk.ActivityLifecycleCallback
import com.clevertap.android.sdk.CleverTapAPI

class MainApplication: Application() {

    override fun onCreate() {
        ActivityLifecycleCallback.register(this)
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE)
        super.onCreate()
    }
}