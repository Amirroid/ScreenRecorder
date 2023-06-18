package ir.amirroid.screenrecorder.utils

import java.util.concurrent.TimeUnit


fun formatTime(time: Long): String {
    return String.format(
        "%02d:%02d:%02d",
        TimeUnit.MILLISECONDS.toHours(time) % 60,
        TimeUnit.MILLISECONDS.toMinutes(time) % 60,
        TimeUnit.MILLISECONDS.toSeconds(time) % 60,
    )
}