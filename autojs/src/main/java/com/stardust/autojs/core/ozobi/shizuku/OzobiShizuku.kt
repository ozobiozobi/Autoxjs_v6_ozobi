package com.stardust.autojs.core.ozobi.shizuku

import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import rikka.shizuku.Shizuku

// Created by ozobi - 2025/01/03

class OzobiShizuku {
    companion object{
        var binder:IBinder? = null
    }
    val logTag = "ozobiLog"
    fun checkPermission(): Boolean {
        var result = false
        try {
            result = Shizuku.checkSelfPermission() === PackageManager.PERMISSION_GRANTED
            if(result){
                if(binder == null){
                    binder = Shizuku.getBinder()
                }
                if(binder == null){
                    Log.d(logTag,"OzobiShizuku: checkPermission: Shizuku绑定失败")
                }else{
                    Log.d(logTag,"OzobiShizuku: checkPermission: Shizuku绑定成功")
                }
            }
        }catch (e:Exception){
            Log.d("ozobiLog",e.toString())
        }
        return result
    }
    fun execCommand(command:String?){
        if(binder != null ){

        }
    }
}
