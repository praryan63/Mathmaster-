package com.mathmaster.app

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class CrashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val trace = intent.getStringExtra("trace") ?: "No trace"
        val text = TextView(this).apply {
            this.text = trace
            textSize = 11f
            setTextIsSelectable(true)
            setPadding(24, 24, 24, 24)
        }
        val copy = Button(this).apply {
            this.text = "Copy error"
            setOnClickListener {
                val cm = this@CrashActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("crash", trace))
            }
        }
        val scroll = ScrollView(this)
        scroll.addView(text)
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(0, 96, 0, 0)
        root.addView(copy)
        root.addView(scroll)
        setContentView(root)
    }
}
