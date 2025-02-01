package com.stardust.autojs.core.ozobi.voiceassistant

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.service.voice.VoiceInteractionService
import android.util.Log


// Created by ozobi - 2025/01/03

open class OzobiAssistInteractionService: VoiceInteractionService() {
    companion object{
        var active = false
    }

    fun getActive():Boolean{
        Log.d("ozobiLog","OzobiAssistInteractionService : doSomething")
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
        Log.d("ozobiLog","OzobiAssistInteractionService : onCreate")
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d("ozobiLog","OzobiAssistInteractionService : onBind")
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