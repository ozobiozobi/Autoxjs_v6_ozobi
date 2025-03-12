package org.autojs.autoxjs.ui.compose.widget

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import org.autojs.autoxjs.ui.compose.theme.AutoXJsTheme
import org.autojs.autoxjs.ui.compose.util.getFitRandomColor

@Composable
fun MyIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = AutoXJsTheme.colors.onBackgroundVariant
){
    Icon(painter, contentDescription, modifier, tint)
}

@Composable
fun MyIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = AutoXJsTheme.colors.onBackgroundVariant
){
    Icon(imageVector, contentDescription, modifier, tint)
}

@Composable
fun MyIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    nightMode:Boolean = false
){
    val tint = getFitRandomColor(nightMode)
    Icon(painter, contentDescription, modifier, Color(tint))
}

@Composable
fun MyIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    nightMode:Boolean = false
){
    val tint = getFitRandomColor(nightMode)
    Icon(imageVector, contentDescription, modifier, Color(tint))
}
// <
