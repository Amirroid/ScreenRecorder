package ir.amirroid.screenrecorder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.HiltAndroidApp
import ir.amirroid.screenrecorder.utils.Constants

@HiltAndroidApp
class ScreenRecorderApp : Application() {
    override fun onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.NOTIF_CHANNEL,
                "Screen Recorder Cahnnel",
                NotificationManager.IMPORTANCE_LOW
            )
            NotificationManagerCompat.from(this).createNotificationChannel(channel)
        }
        super.onCreate()
    }
}