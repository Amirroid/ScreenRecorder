package ir.amirroid.screenrecorder.utils

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.screenrecorder.services.RecordService
import javax.inject.Inject

class PermissionUtils @Inject constructor(
    @ApplicationContext val context: Context
) {
    private val storagePermission = Manifest.permission.READ_EXTERNAL_STORAGE
    private val writeStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val camera = Manifest.permission.CAMERA
    private val recordSound = Manifest.permission.RECORD_AUDIO


    private val permissions = mutableListOf(
        camera,
        recordSound
    )

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            permissions.addAll(
                listOf(
                    storagePermission,
                    writeStoragePermission,
                )
            )
        }
    }

    fun hasFloatingWindowServiceFeature(): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun checkServiceRun(): Boolean {
        val serviceManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return serviceManager.getRunningServices(Int.MAX_VALUE)
            .any { it.service.className == RecordService::class.java.name }
    }

    fun checkPermissions(): Boolean {
        for (permission in permissions) {
            if (context.checkSelfPermission(permission) != PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}