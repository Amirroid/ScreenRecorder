package ir.amirroid.screenrecorder.ui.features.files

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import ir.amirroid.screenrecorder.ui.components.ListItem
import ir.amirroid.screenrecorder.utils.AppPages
import ir.amirroid.screenrecorder.utils.Constants
import ir.amirroid.screenrecorder.utils.Res
import ir.amirroid.screenrecorder.utils.formatTime
import ir.amirroid.screenrecorder.viewModels.FilesViewModel


@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesScreen(navigation: NavController) {
    val viewModel: FilesViewModel = hiltViewModel()
    val files by viewModel.videos.collectAsStateWithLifecycle(emptyList())
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        item {
            MediumTopAppBar(
                title = { Text(text = stringResource(id = Res.String.files)) },
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
        }
        items(files.size) {
            val model = files[it]
            ListItem(content = {
                Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                    Text(
                        text = model.fileName, style = TextStyle(
                            fontSize = 16.sp
                        ), maxLines = 1, overflow = TextOverflow.Clip
                    )
                    Text(
                        text = model.filePath, style = TextStyle(
                            fontSize = 12.sp
                        ), maxLines = 1, overflow = TextOverflow.Clip
                    )
                }
            }, trailingContent = {
                Text(
                    text = formatTime(model.duration),
                    style = TextStyle(
                        fontSize = 12.sp
                    ),
                )
            }) {
                navigation.navigate(AppPages.VideoScreen.route + "?${Constants.VIDEO_ARG}=${model.filePath}")
            }
        }
    }
}