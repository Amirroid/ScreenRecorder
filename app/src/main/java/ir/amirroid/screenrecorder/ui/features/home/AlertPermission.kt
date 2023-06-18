package ir.amirroid.screenrecorder.ui.features.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import ir.amirroid.screenrecorder.utils.PermissionUtils
import ir.amirroid.screenrecorder.utils.Res


@Composable
fun AlertPermission(
    onDismissRequest: () -> Unit,
    onResult: (Boolean) -> Unit,
) {

    val result = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        onResult.invoke(it.resultCode == PackageManager.PERMISSION_GRANTED)
    }
    val context = LocalContext.current
    AlertDialog(onDismissRequest = onDismissRequest, confirmButton = {
        Button(onClick = {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.packageName)
            )
            result.launch(intent)
        }) {
            Text(text = stringResource(id = Res.String.openSettings))
        }
    },
        dismissButton = {
            Button(onClick = {
                onDismissRequest.invoke()
            }) {
                Text(text = stringResource(id = Res.String.cancel))
            }
        })
}