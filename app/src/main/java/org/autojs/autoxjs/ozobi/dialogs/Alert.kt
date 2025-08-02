package org.autojs.autoxjs.ozobi.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.flurry.sdk.it
import org.autojs.autoxjs.R
import org.autojs.autoxjs.ui.compose.theme.primaryButtonColors
import org.autojs.autoxjs.ui.compose.theme.secondaryButtonColors

@Composable
fun SimpleAlert(
    title: String,
    text: String,
    onDismiss: () -> Unit = {},
    okText: String = stringResource(R.string.text_ok)
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                color = MaterialTheme.colors.primary
            )
        },
        text = { Text(text, color = MaterialTheme.colors.primary) },
        buttons = {
            Row(modifier = Modifier.padding(6.dp)) {
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    onDismiss()
                }) {
                    Text(okText)
                }
            }
        },
        backgroundColor = MaterialTheme.colors.background
    )
}

@Composable
fun SingleChoiceAlert(
    title: String,
    selectionAndCallBack: Map<String, () -> Unit>,
    onDismiss: () -> Unit = {}
) {
    val items: Array<String> = selectionAndCallBack.keys.toTypedArray()
    if (items.isEmpty()) {
        return
    }
    var curSelected by remember { mutableStateOf(items[0]) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                color = MaterialTheme.colors.primary
            )
        },
        text = {
            Column {
                Text("", modifier = Modifier.height(6.dp))
                Column {
                    items.forEach { text ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = (curSelected == text), onClick = {
                                curSelected = text
                            })
                            TextButton(onClick = { curSelected = text }) {
                                Text(
                                    text,
                                    color = MaterialTheme.colors.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                selectionAndCallBack[curSelected]?.let { it() }
                onDismiss()
            }) {
                Text(stringResource(R.string.text_confirm))
            }
        },
        backgroundColor = MaterialTheme.colors.background
    )
}

@Composable
fun StatusAlert(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    indicatorModifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    properties: DialogProperties = DialogProperties(),
    buttons: @Composable () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = title,
        text = {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(modifier = indicatorModifier)
            }
        },
        buttons = buttons,
        modifier = modifier,
        properties = properties,
        backgroundColor = MaterialTheme.colors.background
    )
}

@Composable
fun ConfirmAlert(title: @Composable () -> Unit, text:@Composable () -> Unit,onConfirm:() -> Unit = {},onDismiss: () -> Unit = {},){
    AlertDialog(
        title = title,
        text = text,
        onDismissRequest = onDismiss,
        dismissButton = {
            Button(onClick = {
                onDismiss()
            },colors = secondaryButtonColors()) {
                Text(stringResource(R.string.text_cancel))
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm()
            }) {
                Text(stringResource(R.string.text_confirm))
            }
        },
        backgroundColor = MaterialTheme.colors.background
    )
}


@Composable
fun MultiChoiceAlert(
    title: @Composable () -> Unit,
    items: Array<String>,
    modifier: Modifier = Modifier,
    preSelectedItems: Array<String> = arrayOf(),
    onDismiss: () -> Unit = {},
    onConfirm: (Array<String>) -> Unit = {}
) {
    val selected = remember { mutableStateMapOf<String,Boolean>() }
    preSelectedItems.forEach { item->
        selected[item] = true
    }
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = title,
        text = {
            Column(Modifier.fillMaxWidth()) {
                items.forEach { item ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = selected[item] == true, onCheckedChange = {
                            selected[item] = it
                        })
                        Text(
                            item,
                            color = MaterialTheme.colors.onBackground,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selected[item] = selected[item] != true
                                }
                        )
                    }
                }
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
                val resultArray = mutableListOf<String>()
                selected.forEach{item->
                    if(item.value){
                        resultArray.add(item.key)
                    }
                }
                onConfirm(resultArray.toTypedArray())
            }, colors = primaryButtonColors()) {
                Text(stringResource(R.string.text_confirm))
            }
        },
        backgroundColor = MaterialTheme.colors.background
    )
}


