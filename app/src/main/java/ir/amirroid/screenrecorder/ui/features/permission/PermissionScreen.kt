package ir.amirroid.screenrecorder.ui.features.permission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import ir.amirroid.screenrecorder.utils.AppPages
import ir.amirroid.screenrecorder.utils.PermissionUtils

@Composable
fun PermissionScreen(navigation: NavController, permissionUtils: PermissionUtils) {
    val storagePermission = Manifest.permission.READ_EXTERNAL_STORAGE
    val writeStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    val camera = Manifest.permission.CAMERA
    val recordSound = Manifest.permission.RECORD_AUDIO
    val launchOverlyDrawPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        navigation.navigate(AppPages.HomeScreen.route)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it.all { p -> p.value }) {
            if (permissionUtils.hasFloatingWindowServiceFeature()) {
                navigation.navigate(AppPages.HomeScreen.route)
            } else {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + permissionUtils.context.packageName)
                )
                launchOverlyDrawPermission.launch(intent)
            }
        }
    }
    val permissions = mutableListOf(
        camera,
        recordSound
    )
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
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = {
            launcher.launch(
                permissions.toTypedArray()
            )
        }) {
            Text(text = "accept")
        }
    }
}