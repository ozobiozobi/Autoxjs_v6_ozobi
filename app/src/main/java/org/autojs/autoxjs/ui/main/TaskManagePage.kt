package org.autojs.autoxjs.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.autojs.autoxjs.R
import org.autojs.autoxjs.autojs.AutoJs
import org.autojs.autoxjs.ozobi.dialogs.ConfirmAlert
import org.autojs.autoxjs.timing.TimedTaskManager.allIntentTasksAsList
import org.autojs.autoxjs.timing.TimedTaskManager.allTasksAsList
import org.autojs.autoxjs.ui.main.MainActivity.Companion.addCurRunningTask
import org.autojs.autoxjs.ui.main.MainActivity.Companion.curPendingTaskList
import org.autojs.autoxjs.ui.main.MainActivity.Companion.curRunningTaskList
import org.autojs.autoxjs.ui.main.MainActivity.Companion.indexOfTask
import org.autojs.autoxjs.ui.main.task.Task
import org.autojs.autoxjs.ui.main.task.Task.PendingTask
import org.autojs.autoxjs.ui.main.task.Task.RunningTask

@Composable
fun TaskManagePage() {
    TaskManageList()
}


@Composable
fun TaskManageList() {
    var runningTaskListWeight by remember { mutableStateOf(1f) }
    var pendingTaskListWeight by remember { mutableStateOf(1f) }
    val showCancelAllPendingTaskAlert = remember { mutableStateOf(false) }
    val showStopAllRunningTaskAlert = remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxSize()
            .padding(2.dp), verticalArrangement = Arrangement.Top
    ) {
        Card(
            Modifier
                .fillMaxWidth()
                .height(28.dp),
            backgroundColor = MaterialTheme.colors.primary
        ) {
            Row(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(6.dp, 1.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.text_running_task) + "( ${curRunningTaskList.size} )")
                Spacer(Modifier.weight(1f))
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.text_close),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            showStopAllRunningTaskAlert.value = true
                        }
                )
            }
        }
        Spacer(Modifier.size(6.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(runningTaskListWeight),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(curRunningTaskList.size) { item ->
                RunningTaskCard(curRunningTaskList[item])
            }
        }
        Spacer(Modifier.size(6.dp))
        Card(
            Modifier
                .fillMaxWidth()
                .height(28.dp),
            backgroundColor = MaterialTheme.colors.secondary
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(6.dp, 1.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.text_trigger_task) + "( ${curPendingTaskList.size} )")
                Box(
                    Modifier
                        .height(28.dp)
                        .weight(1f)
                        .pointerInput(Unit) {
                            var isDragging = false
                            detectDragGestures(
                                onDragStart = {
                                    isDragging = true
                                },
                                onDrag = { _, dragAmount ->
                                    if (isDragging) {
                                        if (dragAmount.y < 0) {
                                            if (runningTaskListWeight >= pendingTaskListWeight) {
                                                runningTaskListWeight = 1f
                                                pendingTaskListWeight = 3f
                                                isDragging = false
                                            }
                                        } else {
                                            if (runningTaskListWeight <= pendingTaskListWeight) {
                                                runningTaskListWeight = 3f
                                                pendingTaskListWeight = 1f
                                                isDragging = false
                                            }
                                        }
                                    }
                                },
                                onDragEnd = {
                                    isDragging = false
                                },
                                onDragCancel = {
                                    isDragging = false
                                }
                            )
                        }
                        .clickable {
                            runningTaskListWeight = 1f
                            pendingTaskListWeight = 1f
                        }, contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_up_down),
                        contentDescription = stringResource(R.string.text_adjust_arrow),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.text_close),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            showCancelAllPendingTaskAlert.value = true
                        }
                )
            }
        }
        Spacer(Modifier.size(6.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(pendingTaskListWeight),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(curPendingTaskList.size) { item ->
                PendingTaskCard(curPendingTaskList[item])
            }
        }
    }
    if (showStopAllRunningTaskAlert.value) {
        ConfirmAlert(
            title = { Text(stringResource(R.string.text_stop_all) + " ?") },
            text = {
                Column {
                    curRunningTaskList.forEach {
                        Text(it.name)
                    }
                }
            },
            onDismiss = { showStopAllRunningTaskAlert.value = false },
            onConfirm = {
                showStopAllRunningTaskAlert.value = false
                curRunningTaskList.forEach { task ->
                    task.cancel()
                }
            }
        )
    }
    if (showCancelAllPendingTaskAlert.value) {
        ConfirmAlert(
            title = { Text(stringResource(R.string.text_cancel_all) + " ?") },
            text = {
                Column {
                    curPendingTaskList.forEach {
                        Text(it.name)
                    }
                }
            },
            onDismiss = { showCancelAllPendingTaskAlert.value = false },
            onConfirm = {
                showCancelAllPendingTaskAlert.value = false
                curPendingTaskList.forEach { task ->
                    task.cancel()
                }
            }
        )
    }
}

@Composable
fun TaskDetailDialog(task: Task, onDismiss:()->Unit) {
    AlertDialog(onDismissRequest = onDismiss, confirmButton = {
        Button(onClick = {
            task.cancel()
            onDismiss()
        }) {
            if(task is PendingTask){
                Text(stringResource(R.string.text_cancel))
            }else{
                Text(stringResource(R.string.text_stop))
            }
        }
    }, text = {
        Column {
            if(task is PendingTask){
                Text(stringResource(R.string.text_detail))
            }else{
                Text(stringResource(R.string.text_script_path))
            }
            Text(task.desc, color = Color.Gray, fontSize = 12.sp)
        }
    })
}

@Composable
fun RunningTaskCard(task: RunningTask) {
    val showDetailDialog = remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .pointerInput(task){
                detectTapGestures(
                    onLongPress = {
                        showDetailDialog.value = true
                    }
                )
            },
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp, 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(task.name)
                Text(task.desc, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
            }
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(50.dp)
                    .clickable {
                        task.cancel()
                    }, contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.text_close),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
    if(showDetailDialog.value){
        TaskDetailDialog(task){
            showDetailDialog.value = false
        }
    }
}

@Composable
fun PendingTaskCard(task: PendingTask) {
    val showDetailDialog = remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .pointerInput(task){
                detectTapGestures(
                    onLongPress = {
                        showDetailDialog.value = true
                    }
                )
            },
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp, 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(task.name)
                Text(task.desc, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
            }
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(50.dp)
                    .clickable {
                        task.cancel()
                    }, contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.text_close),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
    if(showDetailDialog.value){
        TaskDetailDialog(task){
            showDetailDialog.value = false
        }
    }
}

fun refreshCurPendingTaskList() {
    curPendingTaskList.clear()
    allTasksAsList.forEach { timedTask ->
        val index = indexOfTask(timedTask)
        if (index != -1) {
            curPendingTaskList[index] = PendingTask(timedTask)
        } else {
            curPendingTaskList.add(PendingTask(timedTask))
        }
    }
    allIntentTasksAsList.forEach { intentTask ->
        val index = indexOfTask(intentTask)
        if (index != -1) {
            curPendingTaskList[index] = PendingTask(intentTask)
        } else {
            curPendingTaskList.add(PendingTask(intentTask))
        }
    }
}

fun refreshCurRunningTaskList() {
    curRunningTaskList.clear()
    val executions = AutoJs.getInstance().scriptEngineService.get()?.scriptExecutions
    executions?.forEach { item ->
        if (!item.engine.isDestroyed) {
            addCurRunningTask(RunningTask(item))
        }
    }
}
