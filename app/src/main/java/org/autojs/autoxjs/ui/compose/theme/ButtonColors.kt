package org.autojs.autoxjs.ui.compose.theme

import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun errorButtonColors():ButtonColors{
    return ButtonDefaults.buttonColors(contentColor = MaterialTheme.colors.onError, backgroundColor = MaterialTheme.colors.error)
}

@Composable
fun primaryButtonColors():ButtonColors{
    return ButtonDefaults.buttonColors(contentColor = MaterialTheme.colors.onPrimary, backgroundColor = MaterialTheme.colors.primary)
}

@Composable
fun secondaryButtonColors():ButtonColors{
    return ButtonDefaults.buttonColors(contentColor = MaterialTheme.colors.onSecondary, backgroundColor = MaterialTheme.colors.secondary)
}
