package com.yugentech.sessions.soundEffects

import android.content.Context
import android.media.MediaPlayer
import com.yugentech.sessions.R

class SoundService(private val context: Context) {

    fun playStart() {
        play(R.raw.session_start)
    }

    fun playCompletion() {
        play(R.raw.session_end)
    }

    private fun play(resId: Int) {
        MediaPlayer.create(context, resId)?.apply {
            setOnCompletionListener { release() }
            start()
        }
    }
}