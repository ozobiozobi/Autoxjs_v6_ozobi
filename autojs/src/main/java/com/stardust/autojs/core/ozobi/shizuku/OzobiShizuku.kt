package com.stardust.autojs.core.ozobi.shizuku

import android.content.pm.PackageManager
import android.os.IBinder
import rikka.shizuku.Shizuku


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
                    
                }else{
                    
                }
            }
        }catch (e:Exception){
            
        }
        return result
    }
    fun execCommand(command:String?){
        if(binder != null ){

        }
    }
}
