package com.ozobi.voiceassistant

import android.content.Intent


open class OzobiVoiceAssistantRequest {
    val stable: Int = 8
    private val intent: Intent? = null

    fun getAction(): String {
        var action: String? = ""
        val intent = getIntent()
        return if ((intent == null || (intent.action.also {
                action = it
            }) == null)) "No action" else action!!
    }

    fun getIntent(): Intent? {
        return this.intent
    }
}