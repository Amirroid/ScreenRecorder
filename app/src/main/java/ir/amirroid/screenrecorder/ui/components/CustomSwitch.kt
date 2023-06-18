package ir.amirroid.screenrecorder.ui.components

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CustomSwitch(
    enabled: Boolean,
    onToggleChanged: (Boolean) -> Unit,
    scope: CoroutineScope = rememberCoroutineScope()
) {
    val color by animateColorAsState(
        targetValue = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        label = "screen_recorder_toggle_color",
        animationSpec = tween(400, easing = EaseInOut)
    )
    val offset = remember {
        Animatable(0f)
    }
    val source = MutableInteractionSource()
    var isPressed by remember {
        mutableStateOf(false)
    }
    val sizeBoxAnimation by animateDpAsState(
        targetValue = if (isPressed) 24.dp else 20.dp,
        label = "screen_recorder_toggle_size"
    )
    var size = 0f
    var sizeBox by remember {
        mutableStateOf(0f)
    }
    LaunchedEffect(enabled) {
        if (enabled) {
            offset.animateTo(size - sizeBox - 10)
        } else {
            offset.animateTo(10f)
        }
    }
    Box(
        modifier = Modifier
            .width(48.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(48.dp))
            .background(color)
            .onSizeChanged {
                size = it.width.toFloat()
            }
            .clickable(
                source,
                null,
                onClick = { onToggleChanged.invoke(enabled.not()) })
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset {
                    IntOffset(offset.value.toInt(), 0)
                }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onPrimaryContainer)
                .size(sizeBoxAnimation)
                .onSizeChanged {
                    sizeBox = it.width.toFloat()
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { isPressed = true },
                        onHorizontalDrag = { change, _ ->
                            scope.launch {
                                offset.snapTo(
                                    (offset.value + change.position.x).coerceIn(
                                        0f,
                                        size - sizeBox
                                    )
                                )
                            }
                            Log.d("TAG_OFFSET", "CustomSwitch: ${offset.value}")
                            change.consume()
                        },
                        onDragEnd = {
                            if (offset.value.plus(sizeBox.div(2)) <= size.div(2)) {
                                onToggleChanged.invoke(false)
                            } else {
                                onToggleChanged.invoke(true)
                            }
                            isPressed = false
                        },
                    )
                }
        )
    }
}