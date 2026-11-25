package com.mathmaster.app

import android.app.Application
import com.mathmaster.app.di.ServiceLocator

class MathMasterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
