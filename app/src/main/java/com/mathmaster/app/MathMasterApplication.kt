package com.mathmaster.app

import android.app.Application
import android.content.Intent
import com.mathmaster.app.di.ServiceLocator
import java.io.PrintWriter
import java.io.StringWriter

class MathMasterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            try {
                val writer = StringWriter()
                throwable.printStackTrace(PrintWriter(writer))
                val intent = Intent(this, CrashActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .putExtra("trace", writer.toString().take(20000))
                startActivity(intent)
            } catch (ignored: Throwable) {
            }
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(10)
        }
        ServiceLocator.init(this)
    }
}
