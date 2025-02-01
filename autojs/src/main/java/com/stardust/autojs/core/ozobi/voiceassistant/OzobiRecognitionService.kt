package com.stardust.autojs.core.ozobi.voiceassistant

import android.content.Intent
import android.speech.RecognitionService
import android.util.Log

class OzobiRecognitionService:RecognitionService() {
    override fun onStartListening(recognizerIntent: Intent?, listener: Callback?) {
        Log.d("ozobiLog","开始倾听")
    }

    override fun onCancel(listener: Callback?) {
        Log.d("ozobiLog","取消")
    }

    override fun onStopListening(listener: Callback?) {
        Log.d("ozobiLog","停止倾听")
    }
}