package org.autojs.autoxjs.ozobi.apksigner

import android.util.Log
import org.autojs.autoxjs.build.apksigner.KeyStoreFileManager
import java.io.File
import java.io.InputStream

class Signer(inputApkPath:String, outputApkPath:String?, keyStorePath:String?, password:String?) {
    private var apkFile: File? = null
    private var outputApkFile = File("/storage/emulated/0/脚本/app_signed.apk")
    private var jksFile: File? = File.createTempFile("ozobiKey", "jks").also {
        it.writeBytes((com.mcal.apksigner.ApkSigner::class.java.getResourceAsStream("/keystore/ozobiKey.jks") as InputStream).readBytes())
    }
    private var mPassword = "ozobiozobi"
    private var mAlias = "ozobiKey"
    private var mAliasPassword = "ozobiozobi"

    init {
        apkFile = File(inputApkPath)
        if(!keyStorePath.isNullOrEmpty() && !password.isNullOrEmpty()){
            jksFile = File(keyStorePath)
            val keyStore = KeyStoreFileManager.loadKeyStore(keyStorePath, null)
            mPassword = password
            mAlias = keyStore.aliases().nextElement()
            mAliasPassword = password
        }
        if(!outputApkPath.isNullOrEmpty()){
            outputApkFile = File(outputApkPath)
        }
    }

    constructor(inputApkPath: String):this(inputApkPath,"","",""){
        apkFile = File(inputApkPath)
    }
    constructor(inputApkPath: String, outputApkPath:String):this(inputApkPath,outputApkPath,"",""){
        apkFile = File(inputApkPath)
        outputApkFile = File(outputApkPath)
    }

    fun signDebug() {
        try {
            apkFile?.let { it1 ->
                com.mcal.apksigner.ApkSigner(it1, outputApkFile).apply {
                    useDefaultSignatureVersion = false
                    v1SigningEnabled = true
                    v2SigningEnabled = true
                    v3SigningEnabled = true
                    v4SigningEnabled = true
                }.signDebug()
                Log.d("ozobiLog","saved ${outputApkFile.path}!")
            }
        }catch (e:Exception){
            Log.e("ozobiLog","Signer: e: $e")
        }
    }

    fun signApkWithJks() {
        try {
            jksFile?.let {
                apkFile?.let { it1 ->
                    com.mcal.apksigner.ApkSigner(it1, outputApkFile).apply {
                        useDefaultSignatureVersion = false
                        v1SigningEnabled = true
                        v2SigningEnabled = true
                        v3SigningEnabled = true
                        v4SigningEnabled = false
                    }.signRelease(
                        it,
                        mPassword,
                        mAlias,
                        mAliasPassword,
                    )
                    Log.d("ozobiLog","saved ${outputApkFile.path}!")
                }
            }
        }catch (e:Exception){
            Log.e("ozobiLog","Signer: e: $e")
        }
    }
}