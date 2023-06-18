package ir.amirroid.screenrecorder.utils

import android.content.Context
import android.graphics.PorterDuff
import android.widget.ImageView
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun Offset.toIntOffset() = IntOffset(x.toInt(), y.toInt())


fun IntSize.toIntOffset() = IntOffset(width, height)


fun Int.toDp(context: Context) = this * context.resources.displayMetrics.density


fun ImageView.setTint(color: Int) {
    setColorFilter(color, PorterDuff.Mode.SRC_IN)
}

fun <T> Flow<T>.asStateFlow(scope: CoroutineScope): StateFlow<T> {
    val state = runBlocking { MutableStateFlow(first()) }
    scope.launch {
        if (scope.isActive)
            collect {
                state.value = it
            }
    }
    return state
}