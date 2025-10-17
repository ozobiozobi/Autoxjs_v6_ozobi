package org.autojs.autoxjs.ozobi.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.autojs.autoxjs.R
import java.io.File

@Composable
fun NewFileDialog(
    targetPath: String,
    fileName: String,
    type: String = "file",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val newFileName = remember { mutableStateOf(fileName) }
    val confirmButtonEnabled = remember { mutableStateOf(false) }
    AlertDialog(
        title = {
            Row(Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.text_create))
                Spacer(Modifier.size(6.dp))
                if (type == "file") {
                    Text(stringResource(R.string.text_file))
                } else if (type == "dir") {
                    Text(stringResource(R.string.text_folder))
                } else if (type == "project") {
                    Text(stringResource(R.string.text_project))
                }
            }
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                Text(targetPath)
                Spacer(Modifier.size(6.dp))
                TextField(value = newFileName.value, onValueChange = {
                    newFileName.value = it
                    confirmButtonEnabled.value = true
                })
            }
        },
        onDismissRequest = onDismiss,
        buttons = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(end = 6.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = {
                    onDismiss()
                }) {
                    Text(stringResource(R.string.text_cancel))
                }
                Spacer(Modifier.size(6.dp))
                Button(onClick = {
                    val newFile = File(targetPath, newFileName.value)
                    if (newFile.exists() && ((type == "file" && newFile.isFile) || ((type == "dir" || type == "project") && newFile.isDirectory))) {
                        confirmButtonEnabled.value = false
                    } else {
                        onConfirm(File(targetPath, newFileName.value).absolutePath)
                    }
                }, enabled = confirmButtonEnabled.value) {
                    Text(stringResource(R.string.text_confirm))
                }
            }
        })
}

