package org.autojs.autoxjs.network.ozobi

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondFile
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.autojs.autoxjs.R
import java.io.File
import java.io.FileOutputStream

class KtorDocsService: Service() {

    private lateinit var server:ApplicationEngine
    private lateinit var targetDir:File
    private val CHANNEL_ID = "docs_service_channel"
    private val NOTIFICATION_ID = 1

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        targetDir = File(applicationContext.filesDir, "docs/v1")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Docs Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Docs Service")
            .setContentText(DocsServiceAddress.ip+":"+DocsServiceAddress.port)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        Log.d("ozobiLog","KtorDocsService: onStartCommand: start"+targetDir.path)
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .edit()
            .putBoolean(applicationContext.getString(R.string.ozobi_key_docs_service), true)
            .apply()
        server = embeddedServer(Netty, 16868) {
            // 配置内容协商（JSON 序列化）
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    allowSpecialFloatingPointValues = true
                    allowStructuredMapKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }

            // 配置路由
            routing {
                route("{path...}"){
                    get{
                        val path = call.parameters.getAll("path")?.joinToString("/")?:""
                        val file = if(path.isEmpty()){
                            File(targetDir.absolutePath, "index.html")
                        } else if(path.contains(".")){
                            File(targetDir.absolutePath, path)
                        }else{
                            File(targetDir.absolutePath, "$path/index.html")
                        }
//                    Log.d("ozobiLog","file path: "+file.path)
                        if(file.exists()){
                            call.respondFile(file)
                        }
                    }
                }
            }
        }
        server.start(wait = false)
        // 启动前台服务
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        server.stop(500,1000)
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .edit()
            .putBoolean(applicationContext.getString(R.string.ozobi_key_docs_service), false)
            .apply()
    }

    companion object{
        fun copyFileFromAssets(assetManager: AssetManager, from:String, to:String){
            //    Log.d("ozobiLog","copyFile: from: $from")
            //    Log.d("ozobiLog","copyFile: to: $to")
            val targetFile = File(to)
            try{
                targetFile.delete()
                val inputStream = assetManager.open(from)
                val outputStream = FileOutputStream(targetFile)
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
            }catch(e:Exception){
                //        Log.d("ozobiLog",targetFile.path+" 不是文件")
                targetFile.mkdirs()
                val files = assetManager.list(from) ?: emptyArray()
                var source = from
                if(source[source.length-1] != '/'){
                    source += '/'
                }
                var target = to
                if(target[target.length-1] != '/'){
                    target += '/'
                }
                for (file in files){
                    copyFileFromAssets(assetManager,source+file,target+file)
                }
            }
        }

        fun getDocs(context: Context){
            Log.d("ozobiLog","KtorServer: getDocs")
            val assetManager = context.assets // 获取 AssetManager
            // 定义目标目录
            val targetDir = File(context.filesDir, "docs/v1/")

            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }

            // 从 docs 中提取文件
            val files = assetManager.list("docs/v1") ?: emptyArray()

            for (file in files) {
                copyFileFromAssets(assetManager,"docs/v1/$file",targetDir.path+"/"+file)
            }
        }
    }
}