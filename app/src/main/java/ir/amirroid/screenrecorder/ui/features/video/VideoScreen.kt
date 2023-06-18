package ir.amirroid.screenrecorder.ui.features.video

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import ir.amirroid.screenrecorder.ui.components.VideoView


@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoScreen(
    filePath: String
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setAudioAttributes(getAudioAttrs(), true)
            .build()
    }
    SideEffect {
        exoPlayer.setMediaSource(
            ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
                .createMediaSource(
                    MediaItem.fromUri(
                        Uri.parse(filePath)
                    )
                )
        )
        exoPlayer.prepare()
        exoPlayer.play()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        VideoView(exoPlayer = exoPlayer)
    }
}

fun getAudioAttrs() = AudioAttributes.Builder()
    .setUsage(C.USAGE_MEDIA)
    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
    .build()