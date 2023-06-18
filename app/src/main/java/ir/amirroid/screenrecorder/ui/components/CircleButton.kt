package ir.amirroid.screenrecorder.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    elevation: Dp = 2.dp,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource
        .collectIsPressedAsState()
    val sizeCard by animateDpAsState(
        targetValue = if (isPressed) size - 12.dp else size,
        label = "circle_button_size"
    )
    val colorCard by animateColorAsState(
        targetValue = if (isPressed) color.copy(0.6f) else color,
        label = "circle_button _color"
    )


    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .clip(CircleShape)
                .size(sizeCard)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .then(modifier),
            elevation = CardDefaults.elevatedCardElevation(elevation),
            colors = CardDefaults.cardColors(colorCard)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                content.invoke()
            }
        }
    }
}