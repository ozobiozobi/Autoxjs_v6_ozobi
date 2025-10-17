package com.ozobi.ppocrv5

import android.content.Context
import android.graphics.Bitmap

object PPOCRv5Ncnn {

    var soFileLoaded = false

    init {
        try {
            System.loadLibrary("ppocrv5ncnn")
            soFileLoaded = true
        } catch (e: Exception) { }
    }


    external fun loadModel(
        context: Context, modelId: Int, sizeid: Int, cpugpu: Int, detParamPath: String,
        detModelPath: String, recParamPath: String,
        recModelPath: String
    ): Boolean


    external fun detectAndRecognize(
        img: Bitmap,
        obj: MutableList<RecResult>,
        x: Int = 0,
        y: Int = 0,
        width: Int = 0,
        height: Int = 0
    ): Int


    external fun release()
    external fun OO0OOO00(context: Context)
}