package org.autojs.autoxjs.ozobi.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun LoopingVerticalMove(targetValue: Float, duration: Int,modifier: Modifier = Modifier, target: @Composable () -> Unit) {
    val isMovingUp = remember { mutableStateOf(false) }
    val offset by animateFloatAsState(
        if (isMovingUp.value) 0f else targetValue,
        animationSpec = tween(durationMillis = duration)
    )
    LaunchedEffect(Unit) {
        while (true) {
            isMovingUp.value = !isMovingUp.value
            delay((duration *(Math.random()*0.5+0.8)).toLong())
        }
    }
    Box(modifier = modifier.offset { IntOffset(0, offset.roundToInt()) }, contentAlignment = Alignment.Center) {
        target()
    }
}