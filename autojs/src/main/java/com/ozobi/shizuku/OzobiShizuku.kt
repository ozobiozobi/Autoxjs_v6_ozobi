package com.ozobi.shizuku

import android.content.Context
import android.content.pm.PackageManager
import android.os.IBinder
import android.widget.Toast
import rikka.shizuku.Shizuku


class OzobiShizuku {
    companion object{
        var binder:IBinder? = null
        fun openShizuku(context:Context){
            val packageName = "moe.shizuku.privileged.api"
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Shizuku 未安装", Toast.LENGTH_SHORT).show()
            }
        }
        fun requestPermision(context:Context,code:Int):Boolean{
            try{
                if (Shizuku.isPreV11()) {
                    // Pre-v11 is unsupported
                    return false;
                }
                if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                    // Granted
                    return false
                } else if (Shizuku.shouldShowRequestPermissionRationale()) {
                    // Users choose "Deny and don't ask again"
                    return false
                } else {
                    // Request the permission
                    Shizuku.requestPermission(code);
                    return false;
                }
            }catch (e:Exception){
                openShizuku(context)
                return false
            }
        }
    }
    fun checkPermission(): Boolean {
        var result = false
        try {
            result = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
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
