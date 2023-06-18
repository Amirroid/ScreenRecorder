package ir.amirroid.screenrecorder.utils

object ListUtils {
    val resolution = listOf(
        2400 to 1080,
        1280 to 720,
        800 to 400,
    )
    val videoQuality = listOf(
        50,
        32,
        24,
        16,
        8,
        6,
        1,
    )
    val soundSources = listOf(
        Res.String.mute,
        Res.String.mic,
    )
    val frameRate = listOf(
        30, 24, 15
    )
}