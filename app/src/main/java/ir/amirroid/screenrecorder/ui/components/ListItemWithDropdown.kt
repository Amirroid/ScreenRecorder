package ir.amirroid.screenrecorder.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


@Composable
fun ListItemWithDropdown(
    text: String,
    values: List<String>,
    selected: Int,
    onSelectItem: (Int) -> Unit,
) {
    var isExpand by remember {
        mutableStateOf(false)
    }
    ListItem(text = text, trailingContent = {
        TextMenu(isExpand, values, selected, onSelectItem) {
            isExpand = false
        }
    }) {
        if (isExpand.not()) {
            isExpand = true
        }
    }
}