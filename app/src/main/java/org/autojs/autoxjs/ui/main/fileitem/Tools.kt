package org.autojs.autoxjs.ui.main.fileitem

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.ozobi.files.formatFileSize
import com.ozobi.files.getFileSize
import com.ozobi.files.readFileAsString
import com.ozobi.formatTimestamp
import org.autojs.autoxjs.ui.main.ExplorerViewModel
import org.autojs.autoxjs.ui.main.isFolderContainFileName
import org.json.JSONObject
import java.io.File


fun sortFileItemList(
    fileItemList: MutableList<FileItem>,
    sortBy: Int,
    isDes: Boolean = false
): MutableList<FileItem> {
    return when (sortBy) {
        ExplorerViewModel.Companion.SortBy.NAME -> {
            val resultList = fileItemList.sortedWith{a,b->
                FileItemComparator().compare(a,b)
            }.toMutableList()
            if (!isDes) {
                return resultList
            }
            resultList.asReversed().toMutableList()
        }

        ExplorerViewModel.Companion.SortBy.LAST_MODIFY_TIME -> {
            if (isDes) {
                fileItemList.sortedByDescending { it.lastModifyTime }.toMutableList()
            } else {
                fileItemList.sortedBy { it.lastModifyTime }.toMutableList()
            }
        }

        ExplorerViewModel.Companion.SortBy.SIZE -> {
            if (isDes) {
                fileItemList.sortedByDescending { it.size }.toMutableList()
            } else {
                fileItemList.sortedBy { it.size }.toMutableList()
            }
        }

        ExplorerViewModel.Companion.SortBy.EXTENSION -> {
            if (isDes) {
                fileItemList.sortedByDescending { it.extension }.toMutableList()
            } else {
                fileItemList.sortedBy { it.extension }.toMutableList()
            }
        }

        else -> fileItemList
    }
}

fun getFileItems(
    folderPath: String,
    filter: (File) -> Boolean = { true }
): MutableList<FileItem> {
    val scriptFile = File(folderPath)
    val fileList = scriptFile.listFiles()
    val fileItemList = SnapshotStateList<FileItem>()
    fileList?.forEach { file ->
        if (filter(file)) {
            val size = getFileSize(file)
            val fileItem = FileItem(
                path = file.absolutePath,
                name = file.name,
                type = if (file.isDirectory) {
                    if (isFolderContainFileName(file, "project.json")) {
                        "project"
                    } else {
                        "dir"
                    }
                } else "file",
                extension = file.extension,
                nameWithoutExtension = file.nameWithoutExtension,
                lastModifyTime = file.lastModified(),
                formattedLastModifyTime = formatTimestamp(file.lastModified(), "yyyy-MM-dd HH:mm:ss"),
                size = size,
                formattedSize = formatFileSize(size)
            )
            fileItemList.add(fileItem)
        }
    }
    return fileItemList
}

fun getMainScriptFile(parentDir: String): File {
    val parentDirFile = File(parentDir)
    val projectFile = File(parentDirFile.absoluteFile, "project.json")
    val jsonString = readFileAsString(projectFile)
    val jsonObject = JSONObject(jsonString)
    return if(jsonObject.getString("main").isNotEmpty()){
        File(parentDir, jsonObject.getString("main"))
    }else{
        File(parentDir, "main.js")
    }
}