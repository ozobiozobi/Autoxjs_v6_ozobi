package org.autojs.autoxjs.ui.main.fileitem

data class FileItem(
    val path: String = "",
    val name: String = "",
    val type: String = "",
    val extension: String = "",
    val nameWithoutExtension: String = "",
    val lastModifyTime:Long = 0L,
    val formattedLastModifyTime: String = "",
    val size: Long = 0L,
    val formattedSize: String = ""
)