package com.ozobi.files

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity

object FilePickerUtil {

    // 选择文件（支持多选）
    fun pickFile(
        filePickerLauncher: ActivityResultLauncher<Intent>,
        allowMultiple: Boolean = false,
        mimeTypes: Array<String> = arrayOf("*/*")
    ) {
        val intent = createFilePickerIntent(
            allowMultiple = allowMultiple,
            mimeTypes = mimeTypes // 指定需要的MIME类型，null表示所有文件
        )
        filePickerLauncher.launch(intent)
    }

    // 选择文件夹
    fun pickFolder(filePickerLauncher: ActivityResultLauncher<Intent>) {
        val intent = createFolderPickerIntent()
        filePickerLauncher.launch(intent)
    }

    //
    // 启动文件选择器（支持文件和文件夹）
    fun createFilePickerLauncher(
        activity: ComponentActivity,
        onFileSelected: (Uri?) -> Unit
    ): ActivityResultLauncher<Intent> {
        return activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                onFileSelected(uri)
            } else {
                onFileSelected(null)
            }
        }
    }
    // 启动文件选择器（支持文件和文件夹）
    fun createFilePickerLauncher(
        activity: FragmentActivity,
        onFileSelected: (Uri?) -> Unit
    ): ActivityResultLauncher<Intent> {
        return activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                onFileSelected(uri)
            } else {
                onFileSelected(null)
            }
        }
    }

    fun createFilePickerIntent(
        allowMultiple: Boolean = false,
        mimeTypes: Array<String>? = null
    ): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple)

            // 设置文件类型（null 表示所有文件）
            if (!mimeTypes.isNullOrEmpty()) {
                type = mimeTypes.joinToString(separator = "|")
            } else {
                type = "*/*"
            }
        }
    }

    // 创建选择文件夹的 Intent
    fun createFolderPickerIntent(): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
        }
    }

    // 验证 Uri 是否有效
    fun isValidUri(context: Context, uri: Uri?): Boolean {
        uri ?: return false
        return try {
            context.contentResolver.openInputStream(uri)?.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    // 获取文件真实路径（需要权限）
    fun getRealPathFromUri(context: Context, uri: Uri): String? {
        return when {
            DocumentsContract.isDocumentUri(context, uri) -> {
                when {
                    "com.android.externalstorage.documents" == uri.authority -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":")
                        val type = split[0]
                        if ("primary" == type) {
                            "${Environment.getExternalStorageDirectory()}/${split[1]}"
                        } else {
                            "/storage/${type}/${split[1]}"
                        }
                    }

                    "com.android.providers.downloads.documents" == uri.authority -> {
                        val id = DocumentsContract.getDocumentId(uri)
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(id)
                        )
                        getDataColumn(context, contentUri, null, null)
                    }

                    "com.android.providers.media.documents" == uri.authority -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":")
                        val type = split[0]
                        val contentUri = when (type) {
                            "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                            else -> return null
                        }
                        getDataColumn(context, contentUri, "_id=?", arrayOf(split[1]))
                    }

                    else -> null
                }
            }

            "content".equals(uri.scheme, ignoreCase = true) -> {
                getDataColumn(context, uri, null, null)
            }

            "file".equals(uri.scheme, ignoreCase = true) -> uri.path
            else -> null
        }
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: android.database.Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        return try {
            cursor = context.contentResolver.query(
                uri, projection, selection, selectionArgs, null
            )
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(column)
                    it.getString(columnIndex)
                } else null
            }
        } finally {
            cursor?.close()
        }
    }
}