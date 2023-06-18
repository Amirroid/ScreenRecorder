package ir.amirroid.screenrecorder.utils

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class RecorderUtils @Inject constructor(
    @ApplicationContext val context: Context
) {
    val orientation = SparseIntArray()

    init {
        orientation.append(Surface.ROTATION_0, 90)
        orientation.append(Surface.ROTATION_90, 0)
        orientation.append(Surface.ROTATION_180, 270)
        orientation.append(Surface.ROTATION_270, 180)
    }

    lateinit var file: File

    private val matrix = context.resources.displayMetrics
    private val density = matrix.density

    fun createVirtualDisplay(
        projection: MediaProjection,
        width: Int,
        height: Int,
        dpi: Int,
        recorder: MediaRecorder
    ): VirtualDisplay {
        return projection.createVirtualDisplay(
            "MainActivity",
            width,
            height,
            dpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            recorder.surface,
            null,
            null
        )
    }

    fun initializeRecorder(
        mediaRecorder: MediaRecorder,
        audioSource: Int,
        name: String,
        rotation: Int,
        farmRate: Int,
        quality: Int,
        width: Int,
        height: Int
    ) {
        val audioSourceI = when (audioSource) {
            0 -> MediaRecorder.AudioSource.DEFAULT
            else -> MediaRecorder.AudioSource.DEFAULT
        }
        val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (folder.exists().not()) {
            folder.mkdir()
        }
        file = File(
            folder.path,
            name.plus(".mp4")
        )
        if (file.exists().not()
        ) {
            file.createNewFile()
        }
        Log.d("TAGGILE", "initializeRecorder: $file")
        try {
            mediaRecorder.apply {
                setAudioSource(audioSourceI)
                setVideoSource(MediaRecorder.VideoSource.SURFACE)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(file)
//            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setVideoSize(
                    width,
                    height
                )
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setVideoEncodingBitRate(1024 * 1024 * quality)
                setVideoFrameRate(farmRate)
                setOrientationHint(rotation)
                prepare()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getNameForFile(): String {
        return "AmirScreenRecorder-${SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(Date().time)}"
    }
}