package org.autojs.autoxjs.ui.compose.util

import androidx.compose.runtime.Composable

@Composable
fun getFitRandomColor(nightMode: Boolean):Int{
    val dayMode = !nightMode
    var hue = Math.random() * 180 + 30
    var bright = Math.random() * 0.1 + 0.9
    var saturation = Math.random()*0.3 + 0.3
    val alpha = 230
    if(dayMode){
        hue = (hue + 180) % 360
        bright = Math.random() * 0.6 + 0.3
        saturation = Math.random() * 0.2 + 0.8
    }
    val fArr = FloatArray(3)
    fArr[0] = hue.toFloat()
    fArr[1] = saturation.toFloat()
    fArr[2] = bright.toFloat()
    return android.graphics.Color.HSVToColor(alpha, fArr)
}
fun getFitRandomColorNormal(nightMode: Boolean):Int{
    val dayMode = !nightMode
    var hue = Math.random() * 180 + 30
    var bright = Math.random() * 0.1 + 0.9
    var saturation = Math.random()*0.3 + 0.3
    val alpha = 230
    if(dayMode){
        hue = (hue + 180) % 360
        bright = Math.random() * 0.6 + 0.3
        saturation = Math.random() * 0.2 + 0.8
    }
    val fArr = FloatArray(3)
    fArr[0] = hue.toFloat()
    fArr[1] = saturation.toFloat()
    fArr[2] = bright.toFloat()
    return android.graphics.Color.HSVToColor(alpha, fArr)
}
