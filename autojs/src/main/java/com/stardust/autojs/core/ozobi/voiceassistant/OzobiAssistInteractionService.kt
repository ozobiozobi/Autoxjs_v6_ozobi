package com.stardust.autojs.core.ozobi.voiceassistant

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.service.voice.VoiceInteractionService


open class OzobiAssistInteractionService: VoiceInteractionService() {
    companion object{
        var active = false
    }

    fun getActive():Boolean{
        
        return active
    }
    // 定义一个内部类 MyBinder，继承自 Binder
    inner class MyBinder : Binder() {
        // 获取服务的实例
        fun getService(): OzobiAssistInteractionService {
            return this@OzobiAssistInteractionService
        }
    }
    override fun onCreate() {
        super.onCreate()
        active = true
        
    }

    override fun onBind(intent: Intent?): IBinder {
        
        return MyBinder()
    }

//
//    fun setAirplaneMode(context: Context,enabled:Boolean){
//        val intent = Intent("android.settings.VOICE_CONTROL_AIRPLANE_MODE")
//        intent.putExtra("airplane_mode_enabled",enabled)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        context.startActivity(intent)
//    }


}