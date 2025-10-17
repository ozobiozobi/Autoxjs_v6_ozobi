package org.autojs.autoxjs.ui.main

import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.getSystemService
import com.ozobi.files.copyDir
import com.ozobi.files.copyFile
import com.ozobi.files.renameFolder
import com.stardust.app.GlobalAppContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.autojs.autoxjs.R
import org.autojs.autoxjs.external.ScriptIntents
import org.autojs.autoxjs.model.script.ScriptFile
import org.autojs.autoxjs.model.script.Scripts.openByOtherApps
import org.autojs.autoxjs.model.script.Scripts.runRepeatedly
import org.autojs.autoxjs.model.script.Scripts.send
import org.autojs.autoxjs.ozobi.dialogs.ConfirmAlert
import org.autojs.autoxjs.ozobi.dialogs.MultiChoiceAlert
import org.autojs.autoxjs.ozobi.dialogs.SimpleAlert
import org.autojs.autoxjs.ozobi.dialogs.StatusAlert
import org.autojs.autoxjs.ui.build.BuildActivity
import org.autojs.autoxjs.ui.compose.theme.errorButtonColors
import org.autojs.autoxjs.ui.compose.theme.primaryButtonColors
import org.autojs.autoxjs.ui.compose.theme.secondaryButtonColors
import org.autojs.autoxjs.ui.edit.EditActivity
import org.autojs.autoxjs.ui.main.MainActivity.Companion.curDisplayPath
import org.autojs.autoxjs.ui.main.MainActivity.Companion.curFilterFileList
import org.autojs.autoxjs.ui.main.MainActivity.Companion.curFilterFolderList
import org.autojs.autoxjs.ui.main.MainActivity.Companion.curPathScrollStateMap
import org.autojs.autoxjs.ui.main.MainActivity.Companion.curScriptFilePath
import org.autojs.autoxjs.ui.main.MainActivity.Companion.curSelectedFileMap
import org.autojs.autoxjs.ui.main.MainActivity.Companion.curSelectedFolderMap
import org.autojs.autoxjs.ui.main.MainActivity.Companion.environmentPath
import org.autojs.autoxjs.ui.main.MainActivity.Companion.isSearching
import org.autojs.autoxjs.ui.main.MainActivity.Companion.lastOperationFilePath
import org.autojs.autoxjs.ui.main.MainActivity.Companion.multiSelect
import org.autojs.autoxjs.ui.main.fileitem.FileItem
import org.autojs.autoxjs.ui.main.fileitem.getFileItems
import org.autojs.autoxjs.ui.main.fileitem.getMainScriptFile
import org.autojs.autoxjs.ui.main.fileitem.sortFileItemList
import org.autojs.autoxjs.ui.shortcut.ShortcutCreateActivity
import org.autojs.autoxjs.ui.timing.TimedTaskSettingActivity_
import java.io.File
import java.util.Locale

@Composable
fun HomePage(viewModel: ExplorerViewModel) {
    FileManageList(
        viewModel,
        curDisplayPath,
        modifier = Modifier
            .fillMaxWidth(),
        onBeforeRefreshPathChange = { viewModel.updateCurDisplayPath(it) },
        onDisPlayPathChange = { curDisplayPath = it }
    )
}

fun refreshExplorerList(
    curDisplayPath: String,
    onDisPlayPathChange: (String) -> Unit,
    onBeforeRefreshPathChange: (String) -> Unit,
    callback: () -> Unit = {}
) {
    if (isSearching.value) {
        refreshCurFilterList()
    }
    onBeforeRefreshPathChange(curDisplayPath)
    onDisPlayPathChange("")
    callback()
}

@Composable
fun FileManageList(
    viewModel: ExplorerViewModel,
    curDisplayPath: String,
    modifier: Modifier = Modifier,
    onDisPlayPathChange: (String) -> Unit,
    onBeforeRefreshPathChange: (String) -> Unit
) {
    val context = LocalContext.current
    val curSortBy by viewModel.curSortBy.collectAsState()
    val isDes by viewModel.isDesSort.collectAsState()
    val sortHintMap = mapOf(
        ExplorerViewModel.Companion.SortBy.NAME to context.getString(R.string.text_name),
        ExplorerViewModel.Companion.SortBy.LAST_MODIFY_TIME to context.getString(R.string.text_time),
        ExplorerViewModel.Companion.SortBy.SIZE to context.getString(R.string.text_size),
        ExplorerViewModel.Companion.SortBy.EXTENSION to context.getString(R.string.text_suffix),
    )
    val curSortByText = remember { mutableStateOf(sortHintMap[curSortBy]) }
    val showSortConfigDialog = remember { mutableStateOf(false) }
    val isCurPathProject = remember(curDisplayPath) {
        mutableStateOf(
            isFolderContainFileName(
                File(curDisplayPath),
                "project.json"
            )
        )
    }
    var folderList =
        remember(curDisplayPath, isSearching.value, curFilterFolderList.size) {
            if (isSearching.value) {
                curFilterFolderList
            } else {
                getFileItems(curDisplayPath) {
                    return@getFileItems it.isDirectory
                }
            }
        }
    folderList = sortFileItemList(folderList, curSortBy, isDes)
    var fileList =
        remember(curDisplayPath, isSearching.value, curFilterFileList.size) {
            if (isSearching.value) {
                curFilterFileList
            } else {
                getFileItems(curDisplayPath) { return@getFileItems it.isFile }
            }
        }
    fileList = sortFileItemList(fileList, curSortBy, isDes)

    val togetherList = remember(
        curDisplayPath, curSortBy,
        isDes
    ) { mutableListOf<FileItem>() }

    togetherList.clear()
    togetherList.addAll(folderList)
    if (folderList.size != 0 && fileList.size != 0) {
        togetherList.add(FileItem())
    }
    togetherList.addAll(fileList)

    if (curSelectedFileMap.isEmpty() && curSelectedFolderMap.isEmpty()) {
        multiSelect = false
    }
    val folderPathScrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    val showJumpAlert = remember { mutableStateOf(false) }
    val curListScrollState = remember(curDisplayPath) {
        if (curPathScrollStateMap[curDisplayPath] != null) {
            curPathScrollStateMap[curDisplayPath] ?: LazyListState()
        } else {
            LazyListState()
        }
    }
//    val curFileListScrollState = remember(curDisplayPath) {
//        if (curPathScrollStateMap[curDisplayPath] != null) {
//            curPathScrollStateMap[curDisplayPath]?.get(1) ?: LazyListState()
//        } else {
//            LazyListState()
//        }
//    }
    LaunchedEffect(curDisplayPath) {
        delay(100)
        if (curDisplayPath == "") {
            onDisPlayPathChange(viewModel.curDisplayPath.value)
        }
    }
    Column(
        modifier = modifier.padding(2.dp, 0.dp)
    ) {
        if (showJumpAlert.value) {
            ChangePathDialog {
                showJumpAlert.value = false
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(folderPathScrollState)
                .clickable {
                    showJumpAlert.value = true
                }
        ) {
            Text(curDisplayPath, modifier = Modifier.padding(2.dp, 2.dp))
            scope.launch {
                folderPathScrollState.scrollBy(2000f)
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            backgroundColor = MaterialTheme.colors.primary
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Row {
                        Text(stringResource(R.string.text_folder))
                        if (curSelectedFolderMap.isNotEmpty()) {
                            Text("(${curSelectedFolderMap.size}/${folderList.size})")
                        } else {
                            Text("(${folderList.size})")
                        }
                    }
                    Row {
                        Text(stringResource(R.string.text_file))
                        if (curSelectedFileMap.isNotEmpty()) {
                            Text("(${curSelectedFileMap.size}/${fileList.size})")
                        } else {
                            Text("(${fileList.size})")
                        }
                    }
                }
                if (isCurPathProject.value) {
                    Row(Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_android_black_48dp),
                            contentDescription = stringResource(R.string.text_descent),
                            modifier = Modifier
                                .size(28.dp)
                                .clickable {
                                    BuildActivity.start(context, curDisplayPath)
                                }
                        )
                        Icon(
                            painter = painterResource(R.drawable.ic_run_gray),
                            contentDescription = stringResource(R.string.text_descent),
                            modifier = Modifier
                                .size(28.dp)
                                .clickable {
                                    try {
                                        val mainScriptFile = getMainScriptFile(curDisplayPath)
                                        if (mainScriptFile.exists() && mainScriptFile.isFile) {
                                            runScript(mainScriptFile)
                                        } else {
                                            throw Exception("file not exit")
                                        }
                                    } catch (e: Exception) {
                                        Toast
                                            .makeText(
                                                context,
                                                "请确保project.json文件里至少有{}\n同时确保主脚本文件名称为main.js\n或者指定主脚本文件名\n例如{\"main\":\"myMain.js\"}",
                                                Toast.LENGTH_LONG
                                            )
                                            .show()
                                    }
                                }
                        )
                    }
                } else {
                    Spacer(Modifier.weight(1f))
                }
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.clickable {
                        showSortConfigDialog.value = true
                    }, horizontalAlignment = Alignment.CenterHorizontally) {
                        curSortByText.value?.let {
                            Text(it)
                        }
                        if (isDes) {
                            Icon(
                                painter = painterResource(R.drawable.ic_descent),
                                contentDescription = stringResource(R.string.text_descent),
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_ascent),
                                contentDescription = stringResource(R.string.text_ascent),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Text(
                        stringResource(R.string.text_refresh),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clickable {
                                refreshExplorerList(
                                    curDisplayPath,
                                    onDisPlayPathChange = onDisPlayPathChange,
                                    onBeforeRefreshPathChange = onBeforeRefreshPathChange
                                )
                            }
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = curListScrollState,
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(togetherList.size) { item ->
                ListItemCard(
                    curDisplayPath,
                    togetherList[item],
                    curListScrollState,
                    onDisPlayPathChange = onDisPlayPathChange,
                    onBeforeRefreshPathChange = onBeforeRefreshPathChange
                )
            }
        }
    }
    if (showSortConfigDialog.value) {
        SortConfigDialog(viewModel, onDismiss = {
            showSortConfigDialog.value = false
        }) {
            viewModel.updateCurSortBy(it[0])
            viewModel.updateIsDesSort(it[1] != 0)
            showSortConfigDialog.value = false
            curSortByText.value = sortHintMap[it[0]]
            refreshExplorerList(
                curDisplayPath,
                onDisPlayPathChange = onDisPlayPathChange,
                onBeforeRefreshPathChange = onBeforeRefreshPathChange)
        }
    }
}

@Composable
fun SortConfigDialog(
    viewModel: ExplorerViewModel,
    onDismiss: () -> Unit,
    onConfirm: (Array<Int>) -> Unit
) {
    val context = LocalContext.current
    val curSortBy = remember { mutableStateOf(viewModel.curSortBy.value) }
    val isDes = remember { mutableStateOf(viewModel.isDesSort.value) }
    val sortHintMap = mapOf(
        ExplorerViewModel.Companion.SortBy.NAME to context.getString(R.string.text_name),
        ExplorerViewModel.Companion.SortBy.LAST_MODIFY_TIME to context.getString(R.string.text_last_modify_time),
        ExplorerViewModel.Companion.SortBy.SIZE to context.getString(R.string.text_size),
        ExplorerViewModel.Companion.SortBy.EXTENSION to context.getString(R.string.text_suffix),
    )
    AlertDialog(
        title = { Text(stringResource(R.string.text_sort)) },
        text = {
            Column(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth()) {
                    sortHintMap.forEach { item ->
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = (curSortBy.value == item.key), onClick = {
                                curSortBy.value = item.key
                            })
                            TextButton(onClick = { curSortBy.value = item.key }) {
                                Text(
                                    item.value,
                                    color = MaterialTheme.colors.primary,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isDes.value, onCheckedChange = { isDes.value = it })
                    TextButton(onClick = { isDes.value = !isDes.value }) {
                        Text(
                            stringResource(R.string.text_descent),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        onDismissRequest = onDismiss,
        dismissButton = {},
        confirmButton = {
            Button(onClick = {
                val result = arrayOf(curSortBy.value, if (isDes.value) 1 else 0)
                onConfirm(result)
            }) {
                Text(stringResource(R.string.text_confirm))
            }
        })
}


fun vibrate(context: Context, time: Long) {
    val vibrator = getSystemService(context, Vibrator::class.java)
    vibrator?.vibrate(time)
}

@Composable
fun ChangePathDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val wantToJumpPath = remember(curDisplayPath) { mutableStateOf(TextFieldValue(curDisplayPath)) }
    val canJump = remember { mutableStateOf(false) }
    AlertDialog(
        title = { Text(stringResource(R.string.text_jump_to)) },
        onDismissRequest = {
            onDismiss()
            wantToJumpPath.value = TextFieldValue(curDisplayPath)
        },
        text = {
            TextField(value = wantToJumpPath.value, onValueChange = {
                wantToJumpPath.value = it
                val targetFile = File(it.text)
                canJump.value = targetFile.exists()
            })
        },
        confirmButton = {
            Column(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth()) {
                    Button(onClick = {
                        val text = curScriptFilePath.value
                        wantToJumpPath.value =
                            TextFieldValue(
                                text = text,
                                selection = TextRange(text.length)
                            )
                        canJump.value = true
                    }, colors = secondaryButtonColors()) {
                        Text(stringResource(R.string.text_scripts_file_path))
                    }
                    Spacer(Modifier.weight(1f))
                    Button(onClick = {
                        val text =
                            curScriptFilePath.value + "/" + context.getString(R.string.text_sample)
                        wantToJumpPath.value =
                            TextFieldValue(
                                text = text,
                                selection = TextRange(text.length)
                            )
                        canJump.value = true
                    }, colors = secondaryButtonColors()) {
                        Text(stringResource(R.string.text_sample))
                    }
                }
                Spacer(Modifier.size(10.dp))
                Row(Modifier.fillMaxWidth()) {
                    Button(onClick = {
                        val text = wantToJumpPath.value.text + "/"
                        wantToJumpPath.value =
                            TextFieldValue(text = text, selection = TextRange(text.length))
                    }) {
                        Text("/")
                    }
                    Spacer(Modifier.weight(1f))
                    Button(
                        enabled = canJump.value,
                        onClick = {
                            val result = jumpToPath(wantToJumpPath.value.text)
                            if (!result) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.text_failed),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // 跳转成功后取消搜索
                                isSearching.value = false
                                onDismiss()
                            }
                        }
                    ) {
                        Text(stringResource(R.string.text_confirm))
                    }
                }
            }
        }
    )
}

fun jumpToPath(path: String): Boolean {
    val targetPathFile = File(path)
    if (!targetPathFile.exists()) {
        return false
    }
    var targetPath =
        if (targetPathFile.isDirectory) targetPathFile.absolutePath else targetPathFile.parentFile?.absolutePath
    targetPath?.let {
        if (it.startsWith("/sdcard")) {
            targetPath = it.replace("/sdcard", environmentPath)
        }
    }
    curDisplayPath = targetPath ?: curDisplayPath
    return targetPath != null
}

@Composable
fun ListItemCard(
    curDisplayPath: String,
    fileItem: FileItem,
    curListScrollState: LazyListState,
    onDisPlayPathChange: (String) -> Unit,
    onBeforeRefreshPathChange: (String) -> Unit
) {
    val context = LocalContext.current
    var showSingleSelectionOperationAlert by remember { mutableStateOf(false) }
    var showMultiSelectionOperationAlert by remember { mutableStateOf(false) }
    val pressedCardTextColor = MaterialTheme.colors.primary
    val normalCardTextColor = MaterialTheme.colors.onBackground
    var curCardTextColor by remember { mutableStateOf(normalCardTextColor) }
    val isLastOperationCard =
        remember(
            curDisplayPath,
            multiSelect,
            lastOperationFilePath.value
        ) { mutableStateOf(!multiSelect && fileItem.path == lastOperationFilePath.value) }
    val rowHorizontalArrangement = if (fileItem.type.isEmpty()) {
        Arrangement.Center
    } else {
        Arrangement.Start
    }
    curCardTextColor = if (isLastOperationCard.value) {
        pressedCardTextColor
    } else {
        normalCardTextColor
    }
    Card(
        elevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .wrapContentHeight()
    ) {
        if (showSingleSelectionOperationAlert) {
            SingleSelectionOperationDialog(
                fileItem,
                curDisplayPath,
                fileItem,
                onDisPlayPathChange = onDisPlayPathChange,
                onBeforeRefreshPathChange = onBeforeRefreshPathChange
            ) {
                showSingleSelectionOperationAlert = false
                if (!multiSelect) {
                    unselectCard(fileItem)
                }
            }
        }
        if (showMultiSelectionOperationAlert) {
            val selectedFiles = mutableListOf<FileItem>()
            curSelectedFolderMap.forEach {
                selectedFiles.add(it.value)
            }
            curSelectedFileMap.forEach {
                selectedFiles.add(it.value)
            }
            MultiSelectionOperationDialog(
                fileItem,
                curDisplayPath,
                selectedFiles,
                onBeforeRefreshPathChange = onBeforeRefreshPathChange,
                onDisPlayPathChange = onDisPlayPathChange
            ) {
                showMultiSelectionOperationAlert = false
            }
        }
        Row(
            modifier = Modifier
                .padding(start = 6.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = rowHorizontalArrangement
        ) {
            if (isCardSelected(fileItem) || isLastOperationCard.value) {
                Row(Modifier.clickable {
                    if (isCardSelected(fileItem)) {
                        unselectCard(fileItem)
                    } else {
                        multiSelect = true
                        selectCard(fileItem)
                    }
                }) {
                    if (multiSelect && isCardSelected(fileItem)) {
                        Row(Modifier.clickable {
                            unselectCard(fileItem)
                        }) {
                            Spacer(Modifier.size(28.dp))
                            Icon(
                                painter = painterResource(R.drawable.ic_selected),
                                contentDescription = stringResource(R.string.text_selected),
                                tint = MaterialTheme.colors.onBackground,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    } else if (isLastOperationCard.value) {
                        Spacer(Modifier.size(28.dp))
                    }
                }
            }
            when (fileItem.type) {
                "dir" -> {
                    Icon(
                        painter = painterResource(R.drawable.ic_floating_action_menu_open),
                        contentDescription = stringResource(R.string.text_folder),
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                if (isCardSelected(fileItem)) {
                                    unselectCard(fileItem)
                                } else {
                                    multiSelect = true
                                    selectCard(fileItem)
                                }
                            }
                    )
                }

                "project" -> {
                    Image(
                        painter = painterResource(R.drawable.ic_project),
                        contentDescription = stringResource(R.string.text_project),
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                if (isCardSelected(fileItem)) {
                                    unselectCard(fileItem)
                                } else {
                                    multiSelect = true
                                    selectCard(fileItem)
                                }
                            }
                    )
                }

                else -> {
                    if (fileItem.path.isNotEmpty()) {
                        FileTypeIcon(fileItem.extension, modifier = Modifier.clickable {
                            if (isCardSelected(fileItem)) {
                                unselectCard(fileItem)
                            } else {
                                multiSelect = true
                                selectCard(fileItem)
                            }
                        })
                    }
                }
            }
            Spacer(modifier = Modifier.size(4.dp))
            Column(
                Modifier
                    .weight(1f)
                    .pointerInput(
                        curDisplayPath,
                        curFilterFileList.size,
                        curFilterFolderList.size
                    ) {
                        if (fileItem.type.isNotEmpty()) {
                            detectTapGestures(
                                onTap = {
                                    lastOperationFilePath.value = fileItem.path
                                    curPathScrollStateMap[curDisplayPath] = curListScrollState
                                    if (!multiSelect) {
                                        if (fileItem.type == "dir" || fileItem.type == "project") {
                                            if (isSearching.value) {
                                                isSearching.value = false
                                            }
                                            onDisPlayPathChange(fileItem.path)
                                            clearSelectedMap()
                                        } else if (isTextFile(fileItem.extension)) {
                                            lastOperationFilePath.value = fileItem.path
                                            EditActivity.editFile(context, fileItem.path, true)
                                        }
//                                        else if (isCompressedFile(fileItem.fileNameSuffix)) {
//                                            Toast
//                                                .makeText(context, "解压", Toast.LENGTH_SHORT)
//                                                .show()
//                                        }
                                        else {
                                            openByOtherApps(File(fileItem.path))
                                            Toast
                                                .makeText(
                                                    context,
                                                    "其他应用打开",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                    } else {
                                        if (isCardSelected(fileItem)) {
                                            unselectCard(fileItem)
                                        } else {
                                            selectCard(fileItem)
                                        }
                                    }
                                },
                                onLongPress = {
                                    lastOperationFilePath.value = fileItem.path
                                    curPathScrollStateMap[curDisplayPath] = curListScrollState
                                    val file = File(fileItem.path)
                                    if (file.exists()) {
                                        if (multiSelect) {
                                            if (isCardSelected(fileItem)) {
                                                showMultiSelectionOperationAlert = true
                                            } else {
                                                selectCard(fileItem)
                                            }
                                        } else {
                                            showSingleSelectionOperationAlert = true
                                        }
                                    } else {
                                        Toast
                                            .makeText(
                                                context,
                                                context.getString(R.string.text_target_not_exist),
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                    vibrate(context, 20L)
                                }
                            )
                        }
                    }
            ) {
                Spacer(Modifier.size(6.dp))
                Text(
                    fileItem.name,
                    textAlign = TextAlign.Start,
                    color = curCardTextColor,
                    maxLines = 1
                )
                Spacer(Modifier.size(6.dp))
                Row {
                    Text(fileItem.formattedLastModifyTime, fontSize = 10.sp, color = Color.Gray)
                    if (fileItem.type == "file") {
                        Spacer(Modifier.size(6.dp))
                        Text(fileItem.formattedSize, fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }

            if (isJsFile(fileItem.extension)) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .padding(end = 6.dp)
                        .clickable {
                            if (multiSelect) {
                                if (isCardSelected(fileItem)) {
                                    unselectCard(fileItem)
                                } else {
                                    multiSelect = true
                                    selectCard(fileItem)
                                }
                            } else {
                                lastOperationFilePath.value = fileItem.path
                                runScript(File(fileItem.path))
                            }
                        }, contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_run_gray),
                        contentDescription = stringResource(R.string.text_run),
                        tint = MaterialTheme.colors.onBackground,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

data class LoopConfig(
    val loopTimes: Int = 0,
    val loopInterval: Float = 0f,
    var loopDelay: Float = 0f
)

fun startScriptRunningLoop(scriptFile: ScriptFile, loopConfig: LoopConfig) {
    try {
        runRepeatedly(
            scriptFile,
            loopConfig.loopTimes,
            loopConfig.loopDelay.toLong(),
            (loopConfig.loopInterval * 1000L).toLong()
        )
    } catch (e: NumberFormatException) {
        GlobalAppContext.toast(R.string.text_number_format_error)
    }
}

@Composable
fun LoopRunDialog(onDismiss: () -> Unit, onConfirm: (LoopConfig) -> Unit) {
    val loopTimes = remember { mutableStateOf("0") }
    val loopTimesInvalid = remember { mutableStateOf(false) }
    val loopInterval = remember { mutableStateOf("1.0") }
    val loopIntervalInvalid = remember { mutableStateOf(false) }
    val loopDelay = remember { mutableStateOf("0.0") }
    val loopDelayInvalid = remember { mutableStateOf(false) }
    val confirmButtonEnabled = remember { mutableStateOf(true) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.text_loop_config)) },
        text = {
            Column {
                if (loopTimesInvalid.value) {
                    Text(
                        stringResource(R.string.text_loop_times) + " >>> " + stringResource(R.string.text_invalid),
                        color = MaterialTheme.colors.error
                    )
                } else {
                    Text(stringResource(R.string.text_loop_times))
                }
                TextField(value = loopTimes.value, onValueChange = {
                    loopTimes.value = it
                    confirmButtonEnabled.value = true
                    loopTimesInvalid.value = false
                })
                Spacer(Modifier.size(6.dp))

                if (loopIntervalInvalid.value) {
                    Text(
                        stringResource(R.string.text_loop_interval) + " >>> " + stringResource(R.string.text_invalid),
                        color = MaterialTheme.colors.error
                    )
                } else {
                    Text(stringResource(R.string.text_loop_interval) + "(s)")
                }
                TextField(value = loopInterval.value, onValueChange = {
                    loopInterval.value = it
                    confirmButtonEnabled.value = true
                    loopIntervalInvalid.value = false
                })
                Spacer(Modifier.size(6.dp))

                if (loopDelayInvalid.value) {
                    Text(
                        stringResource(R.string.text_loop_delay) + " >>> " + stringResource(
                            R.string.text_invalid
                        ), color = MaterialTheme.colors.error
                    )
                } else {
                    Text(stringResource(R.string.text_loop_delay) + "(s)")
                }
                TextField(value = loopDelay.value, onValueChange = {
                    loopDelay.value = it
                    confirmButtonEnabled.value = true
                    loopDelayInvalid.value = false
                })
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }, colors = secondaryButtonColors()) {
                Text(stringResource(R.string.text_cancel))
            }
        },
        confirmButton = {
            Button(onClick = {
                val times = loopTimes.value.toIntOrNull()
                if (times == null || times < 0) {
                    confirmButtonEnabled.value = false
                    loopTimesInvalid.value = true
                    return@Button
                }
                val interval = loopInterval.value.toFloatOrNull()
                if (interval == null || interval < 0) {
                    confirmButtonEnabled.value = false
                    loopIntervalInvalid.value = true
                    return@Button
                }
                val delay = loopDelay.value.toFloatOrNull()
                if (delay == null || delay < 0) {
                    confirmButtonEnabled.value = false
                    loopDelayInvalid.value = true
                    return@Button
                }
                onConfirm(LoopConfig(times, interval, delay))
            }, enabled = confirmButtonEnabled.value, colors = primaryButtonColors()) {
                Text(stringResource(R.string.text_confirm))
            }
        }
    )
}

fun isCardSelected(fileItem: FileItem): Boolean {
    return curSelectedFileMap.containsKey(fileItem.path) || curSelectedFolderMap.containsKey(
        fileItem.path
    )
}

fun selectCard(fileItem: FileItem) {
    lastOperationFilePath.value = fileItem.path
    if (fileItem.type == "dir" || fileItem.type == "project") {
        curSelectedFolderMap[fileItem.path] = fileItem
    } else if (fileItem.type == "file") {
        curSelectedFileMap[fileItem.path] = fileItem
    }
}

fun unselectCard(fileItem: FileItem) {
    curSelectedFileMap.remove(fileItem.path)
    curSelectedFolderMap.remove(fileItem.path)
}

fun runScript(scriptFile: File) {
    org.autojs.autoxjs.model.script.Scripts.run(ScriptFile(path = scriptFile.absolutePath))
}

fun selectAllFolder(path: String, onAddToFolderList: (List<FileItem>) -> Unit) {
    val folderList = getFileItems(path) { file ->
        return@getFileItems file.isDirectory
    }
    onAddToFolderList(folderList)
}

fun selectAllFile(path: String, onAddToFileList: (List<FileItem>) -> Unit) {
    val fileList = getFileItems(path) { file ->
        return@getFileItems file.isFile
    }
    onAddToFileList(fileList)
}

@Composable
fun MultiSelectionOperationDialog(
    operationFileItem: FileItem,
    curDisplayPath: String,
    selectedFileList: List<FileItem>,
    onDisPlayPathChange: (String) -> Unit,
    onBeforeRefreshPathChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var show by remember { mutableStateOf(true) }
    var showDeleteConfirmAlert by remember { mutableStateOf(false) }
    val confirmDelete = remember { mutableStateOf(false) }
    val showMultiChoiceAlert = remember { mutableStateOf(false) }
    val showSelectInverseAlert = remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = {
            show = false
        },
        confirmButton = {},
        text = {
            Column {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Button(onClick = {
                            showDeleteConfirmAlert = true
                        }, colors = errorButtonColors()) {
                            Text(stringResource(R.string.text_delete))
                        }
                    }
                    item {
                        Button(onClick = {
                            showMultiChoiceAlert.value = true
                        }, colors = secondaryButtonColors()) {
                            Text(stringResource(R.string.text_select_all))
                        }
                    }
                    item {
                        Button(onClick = {
                            multiSelect = true
                            showSelectInverseAlert.value = true
                        }, colors = secondaryButtonColors()) {
                            Text(stringResource(R.string.text_select_inverse))
                        }
                    }
                }
            }
        }
    )
    if (showSelectInverseAlert.value) {
        val preSelected =
            if (operationFileItem.type == "file") arrayOf(context.getString(R.string.text_file)) else arrayOf(
                context.getString(R.string.text_folder)
            )
        MultiChoiceOptionsAlert(onConfirm = {
            dealSelectInverseDialogResult(context, it)
            showMultiChoiceAlert.value = false
            show = false
        }, onDismiss = { showMultiChoiceAlert.value = false }, preSelected = preSelected)
    }
    if (showMultiChoiceAlert.value) {
        val preSelected =
            if (operationFileItem.type == "file") arrayOf(context.getString(R.string.text_file)) else arrayOf(
                context.getString(R.string.text_folder)
            )
        MultiChoiceOptionsAlert(onConfirm = {
            dealMultiChoiceDialogResult(context, it)
            showMultiChoiceAlert.value = false
            show = false
        }, onDismiss = { showMultiChoiceAlert.value = false }, preSelected = preSelected)
    }
    if (showDeleteConfirmAlert) {
        ConfirmAlert(
            title = {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        stringResource(R.string.text_delete) + "?",
                        color = MaterialTheme.colors.error
                    )
                    Spacer(Modifier.weight(1f))
                    if (curSelectedFolderMap.isNotEmpty()) {
                        Text(
                            stringResource(R.string.text_folder) + ": ${curSelectedFolderMap.size}",
                            color = MaterialTheme.colors.primary
                        )
                    }
                    Spacer(Modifier.size(8.dp))
                    if (curSelectedFileMap.isNotEmpty()) {
                        Text(
                            stringResource(R.string.text_file) + ": ${curSelectedFileMap.size}",
                            color = MaterialTheme.colors.secondary
                        )
                    }
                }
            },
            text = {
                LazyColumn(
                    Modifier
                        .heightIn(max = 200.dp)
                ) {
                    items(selectedFileList.size) {
                        if (selectedFileList[it].type == "file") {
                            Text(
                                selectedFileList[it].name,
                                color = MaterialTheme.colors.secondary
                            )
                        } else {
                            Text(
                                selectedFileList[it].name,
                                color = MaterialTheme.colors.primary
                            )
                        }
                    }
                }
            },
            onDismiss = {
                showDeleteConfirmAlert = false
                confirmDelete.value = false
            }, onConfirm = {
                showDeleteConfirmAlert = false
                confirmDelete.value = true
            }
        )
    }
    if (confirmDelete.value) {
        DeleteFileStatusAlert(
            selectedFileList,
            onDismiss = {
                confirmDelete.value = false
                show = false
                refreshExplorerList(
                    curDisplayPath,
                    onDisPlayPathChange = onDisPlayPathChange,
                    onBeforeRefreshPathChange = onBeforeRefreshPathChange
                )
            },
            onHideClick = { show = false }
        )
    }
    if (!show) {
        onDismiss()
    }
}

@Composable
fun MultiChoiceOptionsAlert(
    onConfirm: (Array<String>) -> Unit,
    onDismiss: () -> Unit,
    preSelected: Array<String> = arrayOf()
) {
    MultiChoiceAlert(
        title = {
            Text(
                stringResource(R.string.text_include),
                color = MaterialTheme.colors.onBackground
            )
        },
        items = arrayOf(stringResource(R.string.text_folder), stringResource(R.string.text_file)),
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        preSelectedItems = preSelected
    )
}

@Composable
fun SingleSelectionOperationDialog(
    operationFileItem: FileItem,
    curDisplayPath: String,
    fileItem: FileItem,
    onDisPlayPathChange: (String) -> Unit,
    onBeforeRefreshPathChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isJsFile = isJsFile(fileItem.extension)
//    val isCompressedFile = isCompressedFile(fileItem.fileNameSuffix)
    var showDeleteConfirmAlert by remember { mutableStateOf(false) }
    val confirmDelete = remember { mutableStateOf(false) }
    val showMultiChoiceAlert = remember { mutableStateOf(false) }
    val renameFolderResult = remember { mutableStateOf(true) }
    val newFilePath = remember { mutableStateOf("") }
    val showSelectInverseAlert = remember { mutableStateOf(false) }
    val showLoopRunDialog = remember { mutableStateOf(false) }
    val showImportDialog = remember { mutableStateOf(false) }
    val importButtonEnabled =
        remember { mutableStateOf(fileItem.path != curScriptFilePath.value) }
    val showMore = remember { mutableStateOf(false) }
    val showRenameDialog = remember { mutableStateOf(false) }
    if (showRenameDialog.value) {
        RenameDialog(fileItem, onConfirm = {
            refreshExplorerList(
                curDisplayPath,
                onDisPlayPathChange = onDisPlayPathChange,
                onBeforeRefreshPathChange = onBeforeRefreshPathChange
            )
            clearSelectedMap()
            onDismiss()
        }) {
            onDismiss()
        }
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            modifier = Modifier.wrapContentHeight(),
            confirmButton = {},
            text = {
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text(fileItem.name)
                    }
                    Spacer(Modifier.size(4.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
//                        if (isCompressedFile) {
//                            item {
//                                Button(onClick = {
//                                    onDismiss()
//                                    /*TODO 解压*/
//                                    Toast.makeText(context, "解压", Toast.LENGTH_SHORT).show()
//                                }) {
//                                    Text(stringResource(R.string.text_decompress))
//                                }
//                            }
//                        }
                        if (showMore.value) {
                            if (fileItem.type == "file") {
                                item {
                                    Button(onClick = {
                                        onDismiss()
                                        send(ScriptFile(fileItem.path))
                                    }, colors = secondaryButtonColors()) {
                                        Text(stringResource(R.string.text_share))
                                    }
                                }
                            }
                            if (fileItem.type == "file") {
                                item {
                                    Button(onClick = {
                                        onDismiss()
                                        openByOtherApps(File(fileItem.path))
                                    }, colors = secondaryButtonColors()) {
                                        Text(stringResource(R.string.text_open_by))
                                    }
                                }
                            }
                            item {
                                Button(onClick = {
                                    multiSelect = true
                                    onDismiss()
                                    selectCard(fileItem)
                                }, colors = secondaryButtonColors()) {
                                    Text(stringResource(R.string.text_multi_select))
                                }
                            }
                            item {
                                Button(onClick = {
                                    multiSelect = true
                                    showMultiChoiceAlert.value = true
                                }, colors = secondaryButtonColors()) {
                                    Text(stringResource(R.string.text_select_all))
                                }
                            }
                        } else {
                            if (isJsFile || fileItem.type == "project") {
                                item {
                                    Button(onClick = {
                                        onDismiss()
                                        BuildActivity.start(context, fileItem.path)
                                    }) {
                                        Text(stringResource(R.string.text_build_apk))
                                    }
                                }
                            }
                            if (isJsFile) {
                                item {
                                    Button(onClick = {
                                        showLoopRunDialog.value = true
                                    }) {
                                        Text(stringResource(R.string.text_loop_run))
                                    }
                                }
                                item {
                                    Button(onClick = {
                                        onDismiss()
                                        TimedTaskSettingActivity_.intent(context)
                                            .extra(ScriptIntents.EXTRA_KEY_PATH, fileItem.path)
                                            .start()
                                    }) {
                                        Text(stringResource(R.string.text_trigger_task))
                                    }
                                }
                                item {
                                    Button(onClick = {
                                        onDismiss()
                                        context.startActivity(Intent(context, ShortcutCreateActivity::class.java).putExtra(ShortcutCreateActivity.EXTRA_FILE, ScriptFile(fileItem.path)))
                                    }) {
                                        Text(stringResource(R.string.text_send_shortcut))
                                    }
                                }
                            }
                            item {
                                Button(onClick = {
                                    showRenameDialog.value = true
                                }) {
                                    Text(stringResource(R.string.text_rename))
                                }
                            }
                            if (importButtonEnabled.value) {
                                item {
                                    Button(onClick = {
                                        showImportDialog.value = true
                                    }, colors = primaryButtonColors()) {
                                        Text(stringResource(R.string.text_import))
                                    }
                                }
                            }
                            item {
                                Button(onClick = {
                                    showDeleteConfirmAlert = true
                                }, colors = errorButtonColors()) {
                                    Text(stringResource(R.string.text_delete))
                                }
                            }
                            item {
                                Button(onClick = {
                                    showMore.value = true
                                }, colors = secondaryButtonColors()) {
                                    Text(stringResource(R.string.text_more))
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    if (showImportDialog.value) {
        ImportDialog(fileItem) {
            onDismiss()
        }
    }
    if (showLoopRunDialog.value) {
        LoopRunDialog(onDismiss = {
            showLoopRunDialog.value = false
        }, onConfirm = { config ->
            startScriptRunningLoop(ScriptFile(fileItem.path), config)
            showLoopRunDialog.value = false
            onDismiss()
        })
    }
    if (showSelectInverseAlert.value) {
        val preSelected =
            if (operationFileItem.type == "file") arrayOf(context.getString(R.string.text_file)) else arrayOf(
                context.getString(R.string.text_folder)
            )
        MultiChoiceOptionsAlert(onConfirm = {
            dealSelectInverseDialogResult(context, it)
            showMultiChoiceAlert.value = false
            onDismiss()
        }, onDismiss = { showMultiChoiceAlert.value = false }, preSelected = preSelected)
    }
    if (!renameFolderResult.value) {
        SimpleAlert(
            title = stringResource(R.string.text_rename_failed),
            onDismiss = { renameFolderResult.value = true },
            text = newFilePath.value
        )
    }
    if (showMultiChoiceAlert.value) {
        val preSelected =
            if (operationFileItem.type == "file") arrayOf(context.getString(R.string.text_file)) else arrayOf(
                context.getString(R.string.text_folder)
            )
        MultiChoiceOptionsAlert(onConfirm = {
            dealMultiChoiceDialogResult(context, it)
            showMultiChoiceAlert.value = false
            onDismiss()
        }, onDismiss = { showMultiChoiceAlert.value = false }, preSelected = preSelected)
    }
    if (showDeleteConfirmAlert) {
        ConfirmAlert(
            title = {
                Text(
                    stringResource(R.string.text_delete) + "?",
                    color = MaterialTheme.colors.error
                )
            },
            text = {
                Text(fileItem.name)
                if (fileItem.type == "file") {
                    Text(fileItem.name, color = MaterialTheme.colors.secondary)
                } else {
                    Text(fileItem.name, color = MaterialTheme.colors.primary)
                }
            },
            onDismiss = {
                showDeleteConfirmAlert = false
            }, onConfirm = {
                showDeleteConfirmAlert = false
                confirmDelete.value = true
            }
        )
    }
    if (confirmDelete.value) {
        DeleteFileStatusAlert(
            listOf(fileItem),
            onDismiss = {
                confirmDelete.value = false
                refreshExplorerList(
                    curDisplayPath,
                    onDisPlayPathChange = onDisPlayPathChange,
                    onBeforeRefreshPathChange = onBeforeRefreshPathChange
                ) {
                    onDismiss()
                }
            },
            onHideClick = {
                confirmDelete.value = false
                onDismiss()
            }
        )
    }
}

@Composable
fun RenameDialog(fileItem: FileItem, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val isFile = fileItem.type == "file"
    var newPureName by remember { mutableStateOf(fileItem.nameWithoutExtension) }
    var newSuffix by remember { mutableStateOf(if (isFile && fileItem.extension.isNotEmpty()) "." + fileItem.extension else "") }
    var confirmButtonEnabled by remember { mutableStateOf(false) }
    val renameFolderResult = remember { mutableStateOf(true) }
    val newFilePath = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.wrapContentHeight(),
        confirmButton = {},
        text = {
            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(fileItem.name)
                }
                Spacer(Modifier.size(4.dp))
                TextField(value = newPureName, onValueChange = {
                    newPureName = it
                    confirmButtonEnabled = it != fileItem.nameWithoutExtension
                })
                if (isFile) {
                    Spacer(Modifier.size(4.dp))
                    TextField(value = newSuffix, onValueChange = {
                        newSuffix = it
                        confirmButtonEnabled = it != fileItem.extension
                    })
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        onDismiss()
                    }) { Text(stringResource(R.string.text_cancel)) }
                    Button(onClick = {
                        newPureName = fileItem.nameWithoutExtension
                        if (isFile) {
                            newSuffix = "." + fileItem.extension
                        }
                        confirmButtonEnabled = false
                    }) { Text(stringResource(R.string.text_restore)) }
                    Button(enabled = confirmButtonEnabled, onClick = {
                        if (isFolderContainFileName(
                                File(curDisplayPath),
                                newPureName + newSuffix
                            )
                        ) {
                            confirmButtonEnabled = false
                            Toast.makeText(
                                context,
                                context.getString(R.string.text_name_existed),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val targetFile = File(fileItem.path)
                            newFilePath.value = targetFile.absolutePath.replace(
                                fileItem.name,
                                newPureName + newSuffix
                            )
                            if (targetFile.isFile) {
                                val newFile = File(newFilePath.value)
                                lastOperationFilePath.value = newFile.absolutePath
                                targetFile.copyTo(newFile, false)
                                if (newFile.exists()) {
                                    targetFile.delete()
                                } else {
                                    renameFolderResult.value = false
                                }
                            } else if (targetFile.isDirectory) {
                                renameFolderResult.value =
                                    renameFolder(targetFile, File(newFilePath.value))
                                if (renameFolderResult.value) {
                                    lastOperationFilePath.value = newFilePath.value
                                }
                            }
                            onConfirm()
                        }
                    }) { Text(stringResource(R.string.text_confirm)) }
                }
            }
        }
    )
}

@Composable
fun ImportDialog(fileItem: FileItem, onDismiss: () -> Unit) {
    val newFileName = remember { mutableStateOf(fileItem.name) }
    val confirmButtonEnabled = remember { mutableStateOf(true) }
    AlertDialog(
        title = {
            Row(Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.text_import))
                Spacer(Modifier.size(6.dp))
                when (fileItem.type) {
                    "dir" -> {
                        Text(stringResource(R.string.text_folder))
                    }

                    "project" -> {
                        Text(stringResource(R.string.text_project))
                    }

                    "file" -> {
                        Text(stringResource(R.string.text_file))
                    }
                }
                if (!confirmButtonEnabled.value) {
                    Spacer(Modifier.size(6.dp))
                    Text(
                        stringResource(R.string.text_name_existed),
                        color = MaterialTheme.colors.error
                    )
                }
            }
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                Text("-> " + curScriptFilePath.value)
                Spacer(Modifier.size(8.dp))
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
                }, colors = secondaryButtonColors()) {
                    Text(stringResource(R.string.text_cancel))
                }
                Spacer(Modifier.size(6.dp))
                Button(onClick = {
                    val to = File(curScriptFilePath.value, newFileName.value)
                    val from = File(fileItem.path)
                    if (to.exists() && ((from.isFile && to.isFile) || (from.isDirectory && to.isDirectory))) {
                        confirmButtonEnabled.value = false
                    } else {
                        if (from.isFile) {
                            copyFile(from, to)
                        } else {
                            copyDir(from, to)
                        }
                        onDismiss()
                    }
                }, enabled = confirmButtonEnabled.value, colors = primaryButtonColors()) {
                    Text(stringResource(R.string.text_confirm))
                }
            }
        }
    )
}

@Composable
fun DeleteFileStatusAlert(
    selectedFiles: List<FileItem>,
    onDismiss: () -> Unit,
    onHideClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val isDoneDeleting = remember { mutableStateOf(false) }
    StatusAlert(
        title = { Text(stringResource(R.string.text_deleting)) },
        properties = DialogProperties(dismissOnBackPress = false),
        onDismiss = {},
        buttons = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = {
                    onHideClick()
                }) {
                    Text(stringResource(R.string.text_hide))
                }
            }
        }
    )
    LaunchedEffect(Unit) {
        selectedFiles.forEach { item ->
            curSelectedFileMap.remove(item.path)
            curSelectedFolderMap.remove(item.path)
            val targetFile = File(item.path)
            if (targetFile.exists()) {
                if (targetFile.isFile) {
                    targetFile.delete()
                } else if (targetFile.isDirectory) {
                    targetFile.deleteRecursively()
                }
            }
        }
        isDoneDeleting.value = true
    }
    scope.launch {
        while (!isDoneDeleting.value) {
            delay(100)
        }
        onDismiss()
    }
}

fun dealSelectInverseDialogResult(context: Context, result: Array<String>) {
    if (result.contains(context.getString(R.string.text_folder))) {
        selectInverseFolder(
            curDisplayPath,
            curSelectedFolderMap,
            onClearOldFolderMap = { curSelectedFolderMap.clear() }) {
            it.forEach { item ->
                curSelectedFolderMap[item.path] = item
            }
        }
    }
    if (result.contains(context.getString(R.string.text_file))) {
        selectInverseFile(
            curDisplayPath,
            curSelectedFileMap,
            onClearOldFileMap = { curSelectedFileMap.clear() }) {
            it.forEach { item ->
                curSelectedFileMap[item.path] = item
            }
        }
    }
}

fun dealMultiChoiceDialogResult(context: Context, result: Array<String>) {
    if (result.contains(context.getString(R.string.text_folder))) {
        selectAllFolder(curDisplayPath) { list ->
            list.forEach { item ->
                curSelectedFolderMap[item.path] = item
            }
        }
    }
    if (result.contains(context.getString(R.string.text_file))) {
        selectAllFile(curDisplayPath) { list ->
            list.forEach { item ->
                curSelectedFileMap[item.path] = item
            }
        }
    }
}

fun selectInverseFolder(
    path: String,
    curSelectedFolderMap: Map<String, FileItem>,
    onClearOldFolderMap: () -> Unit,
    onAddToFolderList: (List<FileItem>) -> Unit
) {
    val folderList = getFileItems(path) { file ->
        return@getFileItems file.isDirectory && !curSelectedFolderMap.containsKey(file.absolutePath)
    }
    onClearOldFolderMap()
    onAddToFolderList(folderList)
}

fun selectInverseFile(
    path: String,
    curSelectedFileMap: Map<String, FileItem>,
    onClearOldFileMap: () -> Unit,
    onAddToFileList: (List<FileItem>) -> Unit
) {
    val fileList = getFileItems(path) { file ->
        return@getFileItems file.isFile && !curSelectedFileMap.containsKey(file.absolutePath)
    }
    onClearOldFileMap()
    onAddToFileList(fileList)
}


fun clearSelectedMap() {
    curSelectedFolderMap.clear()
    curSelectedFileMap.clear()
}

fun isJsFile(targetName: String): Boolean {
    return Regex("js$", RegexOption.IGNORE_CASE).matches(targetName)
}

fun isTextFile(targetName: String): Boolean {
    return Regex("(js|txt|java|json|log|c|cpp|h)$", RegexOption.IGNORE_CASE).matches(targetName)
}

fun isCompressedFile(targetName: String): Boolean {
    return Regex("(zip|7z|rar|tar)$", RegexOption.IGNORE_CASE).matches(targetName)
}


@Composable
fun FileTypeIcon(suffix: String, modifier: Modifier = Modifier, size: Dp = 28.dp) {
    val iconModifier = Modifier.size(size)
    var painterResource = painterResource(R.drawable.ic_floating_action_menu_file)
    var showLetter = false
    var iconTint = true
    Box(modifier, contentAlignment = Alignment.Center) {
        if (isJsFile(suffix)) {
            painterResource = painterResource(R.drawable.ic_file_type__node_js_fill)
            iconTint = false
        } else if (Regex("txt$", RegexOption.IGNORE_CASE).matches(suffix)) {
            painterResource = painterResource(R.drawable.ic_file_type_txt)
        } else if (Regex("(apk|apks)$", RegexOption.IGNORE_CASE).matches(suffix)) {
            painterResource = painterResource(R.drawable.ic_android_fill)
        } else if (Regex("json$", RegexOption.IGNORE_CASE).matches(suffix)) {
            painterResource = painterResource(R.drawable.ic_file_type_json)
        } else if (Regex("(mp4|mkv|avi)$", RegexOption.IGNORE_CASE).matches(suffix)) {
            painterResource = painterResource(R.drawable.ic_file_type_vedio)
        } else if (Regex(
                "(mp3|avi|wav|flac|wma|m4a)$",
                RegexOption.IGNORE_CASE
            ).matches(suffix)
        ) {
            painterResource = painterResource(R.drawable.ic_file_type_audio)
        } else if (Regex("xml$", RegexOption.IGNORE_CASE).matches(suffix)) {
            painterResource = painterResource(R.drawable.ic_file_type_xml)
        } else if (Regex("java", RegexOption.IGNORE_CASE).matches(suffix)) {
            painterResource = painterResource(R.drawable.ic_file_type_java)
        } else if (Regex("db", RegexOption.IGNORE_CASE).matches(suffix)) {
            painterResource = painterResource(R.drawable.ic_file_type_database)
        } else if (Regex("jar", RegexOption.IGNORE_CASE).matches(suffix)) {
            painterResource = painterResource(R.drawable.ic_file_type_jar)
        } else if (Regex("bak$", RegexOption.IGNORE_CASE).matches(suffix)) {
            painterResource = painterResource(R.drawable.ic_file_type_bak)
        } else if (Regex("(jks|keystore)$", RegexOption.IGNORE_CASE).matches(suffix)) {
            painterResource = painterResource(R.drawable.ic_file_type_keystore)
        } else if (Regex("so$", RegexOption.IGNORE_CASE).matches(suffix)) {
            painterResource = painterResource(R.drawable.ic_file_type_so)
        } else if (Regex("(png|jpg|bmp)$", RegexOption.IGNORE_CASE).matches(suffix)) {
            painterResource = painterResource(R.drawable.ic_file_type_picture)
        } else if (Regex("(dex|bin)$", RegexOption.IGNORE_CASE).matches(suffix)) {
            painterResource = painterResource(R.drawable.ic_file_type_binary)
        } else if (isCompressedFile(suffix)) {
            painterResource = painterResource(R.drawable.ic_file_type_compress)
        } else {
            showLetter = true
        }
        if (iconTint) {
            Image(
                painter = painterResource,
                contentDescription = suffix,
                modifier = iconModifier,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary)
            )
        } else {
            Image(
                painter = painterResource,
                contentDescription = suffix,
                modifier = iconModifier
            )
        }
        if (showLetter) {
            val letter = if (suffix.isNotEmpty()) suffix[0] else '?'
            Text(letter.uppercase(Locale.ROOT), color = MaterialTheme.colors.onSecondary)
        }
    }
}

fun isFolderContainFileName(file: File, targetName: String): Boolean {
    if (file.isFile) {
        return false
    }
    file.listFiles()?.forEach { item ->
        if (item.name == targetName) {
            return true
        }
    }
    return false
}