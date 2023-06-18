package ir.amirroid.screenrecorder.ui.features.home

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ir.amirroid.screenrecorder.ui.components.CircleButton
import ir.amirroid.screenrecorder.utils.AppPages
import ir.amirroid.screenrecorder.utils.Res
import ir.amirroid.screenrecorder.viewModels.RecordViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navigation: NavController) {
    val viewModel: RecordViewModel = hiltViewModel()
    val isRecord by viewModel.isRecord.collectAsStateWithLifecycle()
    val animation = updateTransition(targetState = isRecord, "is_record")
    val cornerRadiusRecorderButton by animation.animateDp(label = "") {
        if (it) 50.dp else 4.dp
    }
    val isAlertDialogPermission by viewModel.alertPermission.collectAsStateWithLifecycle()
    val activity = LocalContext.current as Activity
    LaunchedEffect(Unit) {
        viewModel.finish.collectLatest {
            if (it) {
                activity.finish()
            }
        }
    }
    val resultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) {
        viewModel.mediaProjectionIntent.value = null
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            viewModel.startService(true, it.data!!, it.resultCode)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.mediaProjectionIntent.collectLatest { intent ->
            if (intent != null) {
                resultLauncher.launch(intent)
            }
        }
    }
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CenterAlignedTopAppBar(title = { Text(text = stringResource(id = Res.String.screenRecorder)) })
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.2f)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.TopCenter,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CircleButton(
                        onClick = { navigation.navigate(AppPages.FilesScreen.route) },
                        size = 48.dp
                    ) {
                        Icon(
                            painter = painterResource(id = Res.Drawable.folder),
                            contentDescription = null
                        )
                    }
                    CircleButton(onClick = { viewModel.startToggle() }, color = Color.Red) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(cornerRadiusRecorderButton))
                                .background(Color.White)
                        )
                    }
                    CircleButton(
                        onClick = { navigation.navigate(AppPages.SettingsScreen.route) },
                        size = 48.dp
                    ) {
                        Icon(
                            painter = painterResource(id = Res.Drawable.settings),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
    if (isAlertDialogPermission) {
        AlertPermission(
            onDismissRequest = { viewModel.changeAlertShowed(false) }
        ) {
        }
    }
}