package ir.amirroid.screenrecorder.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ir.amirroid.screenrecorder.utils.Res
import ir.amirroid.screenrecorder.utils.toIntOffset

@Composable
fun TextMenu(
    isExpand: Boolean,
    values: List<String>,
    selected: Int,
    onSelectItem: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    val rotate by animateFloatAsState(
        targetValue = if (isExpand) 180f else 0f,
        label = "text_menu_rotate"
    )
    var offset = IntOffset.Zero
    Box {
        Row(modifier = Modifier.onGloballyPositioned {
            offset = it.positionInWindow().toIntOffset()
        }) {
            Text(text = values[selected])
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = Res.Drawable.arrowDown),
                contentDescription = null,
                modifier = Modifier.rotate(rotate)
            )
        }
        CustomMenu(
            visible = isExpand,
            onSelectItem = onSelectItem,
            selected = selected,
            values = values,
            offset = offset,
            onDismissRequest = onDismissRequest
        )
    }
}