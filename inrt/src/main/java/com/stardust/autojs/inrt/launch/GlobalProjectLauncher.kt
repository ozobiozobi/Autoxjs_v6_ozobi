package com.stardust.autojs.inrt.launch

import com.stardust.app.GlobalAppContext

/**
 * Created by Stardust on 2018/3/21.
 */

import java.lang.ref.WeakReference

class GlobalProjectLauncher : AssetsProjectLauncher("project", GlobalAppContext.get()) {
    companion object {
        private var instance: WeakReference<GlobalProjectLauncher>? = null

        @JvmStatic
        fun getInstance(): GlobalProjectLauncher {
            val currentInstance = instance?.get()
            if (currentInstance == null) {
                val newInstance = GlobalProjectLauncher()
                instance = WeakReference(newInstance)
                return newInstance
            }
            return currentInstance
        }
    }
}