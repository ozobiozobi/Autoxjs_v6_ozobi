package com.ozobi.ppocrv5

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.stardust.autojs.core.image.ImageWrapper

class PPOCRV5(context: Context) {

    companion object {
        const val LOG_TAG = "PPOCRV5"

        const val DEFAULT_MODEL = 0
        const val DEFAULT_INFERENCE_BY = 0

        const val SIZE_320 = 0
        const val SIZE_400 = 1
        const val SIZE_480 = 2
        const val SIZE_560 = 3
        const val SIZE_640 = 4
//        val SIZE_ARRAY = arrayOf(320, 400, 480, 560, 640)

        const val MOBILE = 0
        const val SERVER = 1

        const val CPU = 0
        const val GPU = 1
        const val TURNIP = 2
//        val INFERENCE_BY_ARRAY = arrayOf("cpu", "gpu", "turnip")

        var currentModel = DEFAULT_MODEL

        var currentSize = SIZE_320

        var currentCpuGpu = DEFAULT_INFERENCE_BY

        var loadSuccess = true

        var detModelParamPath = ""
        var detModelBinPath = ""
        var recModelParamPath = ""
        var recModelBinPath = ""
//        fun loadSoFile(context: Context, soFilePath: String) {
//            if (soFileLoaded)
//                return
//            val soFile = File(soFilePath)
//            if (!soFile.exists() || !soFile.isFile)
//                throw Exception("文件不存在: $soFilePath")
//            val targetPath = File(context.filesDir.absoluteFile, soFile.name).absolutePath
//            soFile.copyTo(File(targetPath), true)
//            System.load(targetPath)
//            soFileLoaded = true
//        }
    }

    var mContext: Context = context
//    val fileDir = mContext.filesDir.absoluteFile

//    init {
//        setTargetSize(SIZE_640)
//    }

    fun init(
        detModelParam: String,
        detModelBin: String,
        recModelParam: String,
        recModelBin: String
    ) {
        detModelParamPath = detModelParam
        detModelBinPath = detModelBin
        recModelParamPath = recModelParam
        recModelBinPath = recModelBin
        setTargetSize(SIZE_640)
    }

    fun reload() {
        if(!PPOCRv5Ncnn.soFileLoaded)
            return
        if (detModelParamPath.contains("server") || detModelBinPath.contains("server") || recModelParamPath.contains(
                "server"
            ) || recModelBinPath.contains("server")
        ) {
            currentModel = SERVER
        }
        if (detModelParamPath.isEmpty() || detModelBinPath.isEmpty() || recModelParamPath.isEmpty() || recModelBinPath.isEmpty()) {
            Toast.makeText(mContext, "缺少模型文件\n请先设置模型路径", Toast.LENGTH_LONG).show()
            return
        }
        try {
            PPOCRv5Ncnn.OO0OOO00(mContext)
            val retInit = PPOCRv5Ncnn.loadModel(
                mContext,
                currentModel,
                currentSize,
                currentCpuGpu,
                detModelParamPath,
                detModelBinPath,
                recModelParamPath,
                recModelBinPath
            )
            if (!retInit) {
                Log.e(LOG_TAG, "ppocRv5Ncnn loadModel failed")
                loadSuccess = false
            }
        } catch (e: Exception) {
            loadSuccess = false
            Log.e(LOG_TAG, e.toString())
        }
    }

//    fun release() {
//        PPOCRv5Ncnn.OO0OO0O0()
//    }

    fun setModel(model: Int) {
        if (currentModel != model) {
            currentModel = model
            reload()
        }
    }

    fun setTargetSize(size: Int) {
        if (currentSize != size) {
            currentSize = size
            reload()
        }
    }

    fun setCpuGpu(cpugpu: Int) {
        if (currentCpuGpu != cpugpu) {
            currentCpuGpu = cpugpu
            reload()
        }
    }

    fun ocr(image: ImageWrapper, x: Int, y: Int, width: Int, height: Int): MutableList<RecResult>? {
        return ocr(image.bitmap, x, y, width, height)
    }

    fun ocr(image: ImageWrapper): MutableList<RecResult>? {
        return ocr(image.bitmap)
    }

    fun ocr(bitmap: Bitmap, x: Int, y: Int, width: Int, height: Int): MutableList<RecResult>? {
        try {
            val result = mutableListOf<RecResult>()
            val cropBitmap = Bitmap.createBitmap(bitmap, x, y, width, height)
            PPOCRv5Ncnn.detectAndRecognize(cropBitmap, result, x, y, width, height)
            cropBitmap.recycle()
            return result
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.toString())
            return null
        }
    }

    fun ocr(bitmap: Bitmap): MutableList<RecResult>? {
        try {
            val result = mutableListOf<RecResult>()
            PPOCRv5Ncnn.detectAndRecognize(bitmap, result)
            return result
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.toString())
            return null
        }
    }
}