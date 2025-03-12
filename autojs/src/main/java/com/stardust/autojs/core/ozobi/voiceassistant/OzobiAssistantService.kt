package com.stardust.autojs.core.ozobi.voiceassistant

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log

class OzobiAssistantService : OzobiAssistInteractionService() {

    private var serviceConnection: ServiceConnection? = null
    var ozobiService: OzobiAssistInteractionService? = null

    override fun onCreate() {
        super.onCreate()
        bindToOzobiService()
    }

    fun bindToOzobiService() {
        val serviceIntent = Intent(this, OzobiAssistInteractionService::class.java)
        

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, serviceBinder: IBinder?) {
                
                active = true
                val binder = serviceBinder as OzobiAssistInteractionService.MyBinder
                ozobiService = binder.getService()
                ozobiService?.getActive()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                
                active = false
                ozobiService = null
            }
        }

        if (!bindService(serviceIntent, serviceConnection!!, Context.BIND_AUTO_CREATE)) {
            Log.e("ozobiLog", "Failed to bind to OzobiAssistInteractionService")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection!!)
        serviceConnection = null
        ozobiService = null
        active = false
        
    }
}




//

//    var service:Service? = null
//
//    override fun onCreate() {
//        super.onCreate()
//        service = getService(applicationContext)
//    }
//
//    fun getService(context: Context):Service?{

//        // 客户端代码


//        var service: Service? = null
//        val bindResult = bindService(serviceIntent, object :
//            ServiceConnection {
//            override fun onServiceConnected(name: ComponentName?, serviceBinder: IBinder?) {

//                active = true

//                // 获取服务实例
//                service = binder.getService()
//                // 调用服务中的方法

//            }
//
//            override fun onServiceDisconnected(name: ComponentName?) {
//                // 服务连接断开时的回调

//            }
//        }, Context.BIND_AUTO_CREATE)
//        if(bindResult){
//            return service
//        }
//        return null
//    }
//}