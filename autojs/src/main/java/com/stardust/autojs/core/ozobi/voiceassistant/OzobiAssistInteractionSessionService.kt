package com.stardust.autojs.core.ozobi.voiceassistant

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService
import kotlin.jvm.internal.Intrinsics


class OzobiAssistInteractionSessionService: VoiceInteractionSessionService() {

    private val linkToDeath = "assist_interaction_link_to_death"

    override fun onCreate() {
        super.onCreate()
        
    }

    override fun onNewSession(bundle: Bundle?):VoiceInteractionSession  {
        
        startLinkToDeath(applicationContext)
        val voiceInteractionSession = VoiceInteractionSession(this)
        if (Build.VERSION.SDK_INT >= 26) {
            voiceInteractionSession.setUiEnabled(false);
        }
        return voiceInteractionSession
    }
    fun verifyContext(context:Context?):Int{
        if(context == null){
            return 0
        }
        try{
            return Settings.Secure.getInt(context.contentResolver,linkToDeath)
        }catch (e:Exception){
            
        }
        return 0
    }
    fun startLinkToDeath(context: Context) {
        Intrinsics.checkNotNullParameter(context, "context")
        if (verifyContext(context) === 1) {
            return
        }
        Settings.Secure.putInt(context.contentResolver, linkToDeath, 1)
    }

    fun stopLinkToDeath(context: Context) {
        Intrinsics.checkNotNullParameter(context, "context")
        if (verifyContext(context) === 0) {
            return
        }
        Settings.Secure.putInt(context.contentResolver, linkToDeath, 0)
    }
}