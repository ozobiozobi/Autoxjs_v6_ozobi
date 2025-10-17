package com.ozobi.ppocrv5

data class RecResult(
    val text: String = "",
    val prob: Float = 0f,
    val ori: Int = 0,
    val rRect: RotatedRect = RotatedRect(),
    val vertices:Vertices = Vertices(),
    val rect:Rect = Rect()
)