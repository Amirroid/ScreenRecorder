package ir.amirroid.screenrecorder.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecorderModel(
    var bitrate: Int,
    var frameRate: Int,
    var audioSource: Int,
    var width: Int,
    var height: Int,
    var openDefaultCamera: Boolean
) : Parcelable


