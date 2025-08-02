package com.ozobi.files

import android.util.Log
import java.io.File
import java.io.IOException


fun copyFile(from: String, to: String, overwrite: Boolean = false): File {
    val fromPath = File(from)
    val toPath = File(to)
    return copyFile(fromPath, toPath, overwrite = overwrite)
}

@Throws(IOException::class)
fun copyFile(from: File, to: File, overwrite: Boolean = false): File {
    val targetParent = to.parentFile
    if (targetParent != null && !targetParent.exists()) {
        targetParent.mkdirs()
    }
    return from.copyTo(to, overwrite = overwrite)
}

fun copyDir(from: String, to: String, overwrite: Boolean = false) {
    val fromPath = File(from)
    val toPath = File(to)
    copyDir(fromPath, toPath, overwrite = overwrite)
}

fun copyDir(from: File, to: File, overwrite: Boolean = false) {
    // 防止将文件夹复制到其自身内部
    if (to.absolutePath.startsWith(from.absolutePath) && to.absoluteFile.endsWith(from.name)) {
        Log.d("copyDir", "Copying to a subdirectory of source directory is not allowed")
        return
    }
    if (!to.exists()) {
        to.mkdirs()
    }
    from.listFiles()?.forEach { file ->
        val targetFile = File(to, file.name)
        if (file.isDirectory) {
            copyDir(file, targetFile, overwrite = overwrite)
        } else {
            copyFile(file, targetFile, overwrite = overwrite)
        }
    }
}

fun deleteFile(target: String): Boolean {
    val targetFile = File(target)
    return deleteFile(targetFile)
}

fun deleteFile(targetFile: File): Boolean {
    return targetFile.delete()
}

fun deleteDir(target: String): Boolean {
    val targetDir = File(target)
    return deleteDir(targetDir)
}

fun deleteDir(targetFile: File): Boolean {
    return targetFile.deleteRecursively()
}

fun renameFolder(from: String, to: String, deleteOld:Boolean = false):Boolean {
    return renameFolder(File(from), File(to))
}

fun renameFolder(from: File, to: File, deleteOld:Boolean = false):Boolean {
    if(!from.exists()){
        return false
    }
    if(to.exists() && to.isDirectory && !deleteOld){
        return false
    }
    return from.renameTo(to)
}


fun formatFileSize(sizeInBytes: Long): String {
    if (sizeInBytes < 0) {
        return "Not a file"
    }
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var size = sizeInBytes.toDouble()
    var unitIndex = 0
    while (size >= 1024.0 && unitIndex < units.size - 1) {
        size /= 1024.0
        unitIndex++
    }
    return String.format("%.2f %s", size, units[unitIndex])
}

fun getFileSize(filePath: String): Long {
    val file = File(filePath)
    return getFileSize(file)
}

fun getFileSize(file: File): Long {
    return if (file.isFile && file.exists()) {
        file.length()
    } else {
        -1L
    }
}
