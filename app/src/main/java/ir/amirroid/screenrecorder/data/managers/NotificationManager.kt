package ir.amirroid.screenrecorder.data.managers

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.screenrecorder.data.receivers.RecordingStopReceiver
import ir.amirroid.screenrecorder.utils.Constants
import ir.amirroid.screenrecorder.utils.Res
import javax.inject.Inject
import javax.inject.Singleton

class NotificationManager @Inject constructor(
    @ApplicationContext val context: Context
) {
    private val intent = Intent(context, RecordingStopReceiver::class.java)
    private val pi = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    private val manager =
        context.getSystemService(android.app.NotificationManager::class.java)

    private val notification: Notification =
        NotificationCompat.Builder(context, Constants.NOTIF_CHANNEL)
            .setContentTitle(context.getString(Res.String.recording))
            .setContentText(context.getString(Res.String.descriptionNotification))
            .addAction(NotificationCompat.Action(null, context.getString(Res.String.stop), pi))
            .build()

    fun getNotification() = notification
}