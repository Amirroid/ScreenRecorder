package ir.amirroid.screenrecorder.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ListItem(
    text: String,
    trailingContent: @Composable () -> Unit,
    onClick: () -> Unit
) {
    ListItem(content = {
        Text(
            text = text, style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        )
    }, trailingContent = trailingContent, onClick = onClick)
}

@Composable
fun ListItem(
    content: @Composable () -> Unit,
    trailingContent: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val isPressed by interactionSource.collectIsPressedAsState()
    val color by animateColorAsState(
        targetValue = if (isPressed) Color.Gray.copy(
            0.2f
        ) else Color.Transparent, label = "list_item_color"
    )
    val paddingHorizontal by animateDpAsState(
        targetValue = if (isPressed) 20.dp else 16.dp,
        label = "list_item_padding"
    )

    Row(
        modifier = Modifier
            .background(color)
            .padding(horizontal = paddingHorizontal)
            .clickable(interactionSource, null, onClick = onClick)
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        content.invoke()
        trailingContent.invoke()
    }
}