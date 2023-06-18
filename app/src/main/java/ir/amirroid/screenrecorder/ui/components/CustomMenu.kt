package ir.amirroid.screenrecorder.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CustomMenu(
    visible: Boolean,
    onSelectItem: (Int) -> Unit,
    selected: Int,
    values: List<String>,
    offset: IntOffset,
    onDismissRequest: () -> Unit
) {
    val density = LocalDensity.current
    val height = 56.dp
    val heightPx = with(density) { height.toPx() }.toInt()
    val offsetYSelectedItem by animateIntAsState(
        targetValue = selected * heightPx,
        label = "custom_menu_y_selected_item",
        animationSpec = spring(
            Spring.DampingRatioLowBouncy,
            Spring.StiffnessLow
        )
    )
    Popup(
        offset = offset - IntOffset(0, 100),
        onDismissRequest = onDismissRequest,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.elevatedCardElevation(4.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .width(200.dp)
                    .wrapContentSize()
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(0, offsetYSelectedItem)
                            }
                            .fillMaxWidth()
                            .height(height)
                            .background(Color.Gray.copy(0.2f))
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        for (i in values.indices) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp)
                                    .height(height)
                                    .clickable(MutableInteractionSource(), null, onClick = {
                                        onSelectItem.invoke(i)
                                    }),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = values[i], style = TextStyle(
                                        fontSize = 16.sp,
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}