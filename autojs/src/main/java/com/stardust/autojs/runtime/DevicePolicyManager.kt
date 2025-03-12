package com.stardust.autojs.runtime

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import com.stardust.autojs.core.ozobi.deviceadmin.OzobiDeviceAdminReceiver
import com.stardust.autojs.runtime.ScriptRuntime.getApplicationContext


object DevicePolicyManager {
    val devicePolicyManager: DevicePolicyManager = getSystemService(getApplicationContext(),DevicePolicyManager::class.java) as DevicePolicyManager
    val componentName = ComponentName(getApplicationContext(), OzobiDeviceAdminReceiver::class.java)

    fun lockNow(){
        if(devicePolicyManager.isAdminActive(componentName)){
            devicePolicyManager.lockNow()
        }
        Log.d("error","设备管理员未激活")
    }

    fun setTimeToLock(time:Long){
        if(devicePolicyManager.isAdminActive(componentName)){
            devicePolicyManager.setMaximumTimeToLock(componentName, time)
        }
        Log.d("error","设备管理员未激活")
    }

    fun setCameraDisabled(disabled:Boolean){
        if(devicePolicyManager.isAdminActive(componentName)){
            devicePolicyManager.setCameraDisabled(componentName, disabled)
        }
        Log.d("error","设备管理员未激活")
    }

    fun resetPassword(password:String){
        if(devicePolicyManager.isAdminActive(componentName)){
            devicePolicyManager.resetPassword(password,DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY)
        }
        Log.d("error","设备管理员未激活")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setResetPasswordToken(token:ByteArray){
        if(devicePolicyManager.isAdminActive(componentName)){
            devicePolicyManager.setResetPasswordToken(componentName,token)
        }
        Log.d("error","设备管理员未激活")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun resetPasswordWithToken(password: String, token: ByteArray){
        if(devicePolicyManager.isAdminActive(componentName)){
            devicePolicyManager.resetPasswordWithToken(componentName,password,token,DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY)
        }
        Log.d("error","设备管理员未激活")
    }
}