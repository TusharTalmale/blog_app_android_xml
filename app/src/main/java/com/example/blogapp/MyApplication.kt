package com.example.blogapp

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val config = HashMap<String, String>()
        config["cloud_name"] = "dengfxb5y"

        MediaManager.init(this, config)
    }
}
