package com.ozobi.files

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

fun readFileAsString(filePath: String): String {
    val file = File(filePath)
    return readFileAsString(file)
}

fun readFileAsString(file:File): String {
    val reader = BufferedReader(FileReader(file))
    val stringBuilder = StringBuilder()
    reader.forEachLine {
        stringBuilder.append(it)
    }
    reader.close()
    return stringBuilder.toString()
}
