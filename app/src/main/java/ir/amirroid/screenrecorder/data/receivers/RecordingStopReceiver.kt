package ir.amirroid.screenrecorder.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ir.amirroid.screenrecorder.services.RecordService

class RecordingStopReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, inetnt: Intent?) {
        val service = Intent(context, RecordService::class.java)
        context?.stopService(service)
    }
}