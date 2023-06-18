package ir.amirroid.screenrecorder.ui.features.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ir.amirroid.screenrecorder.ui.components.CustomSwitch
import ir.amirroid.screenrecorder.ui.components.ListItem
import ir.amirroid.screenrecorder.ui.components.ListItemWithDropdown
import ir.amirroid.screenrecorder.utils.Res
import ir.amirroid.screenrecorder.viewModels.RecordViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navigation: NavController) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberScrollState()
    val viewModel: RecordViewModel = hiltViewModel()
    val selectedFrameRate by viewModel.frameRate.collectAsStateWithLifecycle()
    val selectedResolution by viewModel.resolution.collectAsStateWithLifecycle()
    val selectedQuality by viewModel.quality.collectAsStateWithLifecycle()
    val openDefaultCamera by viewModel.openDefaultCamera.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .verticalScroll(scrollState)
    ) {
        MediumTopAppBar(
            title = { Text(text = stringResource(id = Res.String.settings)) },
            scrollBehavior = scrollBehavior,
            navigationIcon = {
                IconButton(onClick = { navigation.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowLeft,
                        contentDescription = null
                    )
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
        ) {
            ListItemWithDropdown(
                text = stringResource(id = Res.String.resolution),
                viewModel.resolutionL.map { "${it.first}*${it.second}" },
                selectedResolution
            ) {
                scope.launch {
                    viewModel.settingsHelper.setResolution(it)
                }
            }
            ListItemWithDropdown(
                text = stringResource(id = Res.String.frameRate),
                viewModel.frameRateL.map { "${it}fps" },
                selectedFrameRate
            ) {
                scope.launch {
                    viewModel.settingsHelper.setFrameRate(it)
                }
            }
            ListItemWithDropdown(
                text = stringResource(id = Res.String.videoQuality),
                viewModel.qualityL.map { "${it}Mbps" },
                selectedQuality
            ) {
                scope.launch {
                    viewModel.settingsHelper.setQuality(it)
                }
            }
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(end = 24.dp, start = 12.dp)
            )
            Text(
                text = stringResource(id = Res.String.otherSettings),
                Modifier
                    .padding(top = 12.dp, start = 12.dp)
                    .alpha(0.6f),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            )
            ListItem(
                text = stringResource(id = Res.String.defaultCamera),
                trailingContent = {
                    CustomSwitch(enabled = openDefaultCamera, onToggleChanged = {
                        scope.launch {
                            viewModel.settingsHelper.setCameraDefaultOpen(it)
                        }
                    })
                }
            ) {
                scope.launch {
                    scope.launch {
                        viewModel.settingsHelper.setCameraDefaultOpen(openDefaultCamera.not())
                    }
                }
            }
        }
    }
}