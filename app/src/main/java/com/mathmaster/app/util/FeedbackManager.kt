package com.mathmaster.app.util

import android.media.AudioManager
import android.media.ToneGenerator

class FeedbackManager {

    private var toneGenerator: ToneGenerator? = null

    private fun generator(): ToneGenerator? = try {
        toneGenerator ?: ToneGenerator(AudioManager.STREAM_MUSIC, 35).also { toneGenerator = it }
    } catch (exception: Exception) {
        null
    }

    fun play(correct: Boolean, soundEnabled: Boolean) {
        if (!soundEnabled) return
        try {
            generator()?.startTone(
                if (correct) ToneGenerator.TONE_PROP_BEEP else ToneGenerator.TONE_PROP_NACK,
                if (correct) 80 else 140
            )
        } catch (exception: Exception) {
            // Audio feedback is optional; practice continues without it.
        }
    }
}
