package ir.amirroid.screenrecorder.viewModels

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.screenrecorder.data.db.SettingsHelper
import ir.amirroid.screenrecorder.data.models.RecorderModel
import ir.amirroid.screenrecorder.services.RESULT_CODE
import ir.amirroid.screenrecorder.services.RESULT_DATA
import ir.amirroid.screenrecorder.services.RecordService
import ir.amirroid.screenrecorder.utils.Constants
import ir.amirroid.screenrecorder.utils.ListUtils
import ir.amirroid.screenrecorder.utils.PermissionUtils
import ir.amirroid.screenrecorder.utils.asStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class RecordViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val permissionUtils: PermissionUtils,
    val settingsHelper: SettingsHelper
) : ViewModel() {

    // lists
    val frameRateL = ListUtils.frameRate
    val qualityL = ListUtils.videoQuality
    val resolutionL = ListUtils.resolution
    val finish = MutableStateFlow(false)

    val soundSourceL = ListUtils.soundSources
        .map {
            context.getString(it)
        }

    private val mediaProjectionManager =
        context.getSystemService(MediaProjectionManager::class.java)


    val mediaProjectionIntent = MutableStateFlow<Intent?>(null)


    // states
    val frameRate = settingsHelper.frameRate.asStateFlow(viewModelScope)
    val quality = settingsHelper.quality.asStateFlow(viewModelScope)
    val resolution = settingsHelper.resolution.asStateFlow(viewModelScope)
    private val soundSource = 0
    val openDefaultCamera = settingsHelper.defaultCamera.asStateFlow(viewModelScope)


    private val _isRecord = MutableStateFlow(permissionUtils.checkServiceRun())
    val isRecord = _isRecord.asStateFlow()

    private val _alertPermission = MutableStateFlow(false)
    val alertPermission = _alertPermission.asStateFlow()


    init {
        if (permissionUtils.hasFloatingWindowServiceFeature()) {
            _isRecord.value = permissionUtils.checkServiceRun()
        }
    }


    fun startToggle() {
        if (permissionUtils.checkServiceRun().not()) {
            getIntent()
        } else {
            val intent = Intent(context, RecordService::class.java)
            context.stopService(intent)
        }
        refreshData()
    }

    private fun getIntent() {
        mediaProjectionIntent.value = mediaProjectionManager.createScreenCaptureIntent()
    }

    fun startService(isDrawing: Boolean, data: Intent, resultCode: Int) {
        val intent = Intent(context, RecordService::class.java)
        intent.putExtra(Constants.HAS_DRAW_OVERLY_FEATURE, isDrawing)
        intent.putExtra(RESULT_DATA, data)
        intent.putExtra(
            Constants.DATA_RECORD, RecorderModel(
                qualityL[quality.value],
                frameRateL[frameRate.value],
                soundSource,
                resolutionL[resolution.value].second,
                resolutionL[resolution.value].first,
                openDefaultCamera.value
            )
        )
        intent.putExtra(RESULT_CODE, resultCode)
        context.startService(intent)
        finish()
    }

    private fun finish() {
        finish.value = true
    }

    private fun refreshData() {
        val run = permissionUtils.checkServiceRun()
        Log.d("TAG_SERVICE", "refreshData: $run")
        _isRecord.value = run
    }

    fun changeAlertShowed(isVisible: Boolean) {
        _alertPermission.value = isVisible
    }
}