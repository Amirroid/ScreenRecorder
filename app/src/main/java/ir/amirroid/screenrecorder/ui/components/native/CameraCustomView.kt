package ir.amirroid.screenrecorder.ui.components.native

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import ir.amirroid.screenrecorder.R
import ir.amirroid.screenrecorder.utils.toDp
import kotlin.math.min

@SuppressLint("ViewConstructor")
class CameraCustomView(
    context: Context,
    private val lifecycleOwner: LifecycleOwner
) : FrameLayout(context) {
    private var previewView: PreviewView
    private var sizeButton: ImageView
    private var switchButton: ImageView
    private lateinit var preview: Preview
    private lateinit var cameraSelector: CameraSelector
    private lateinit var cameraProvider: ProcessCameraProvider


    private var lastX = 0f
    private var lastY = 0f

    private var sizeMode = 1f

    init {
        LayoutInflater.from(context).inflate(R.layout.camera_floating_window, this, true)
        previewView = findViewById(R.id.camera_view)
        sizeButton = findViewById(R.id.size_change)
        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        switchButton = findViewById(R.id.switch_camera)
        initializeSize()
        initializeSwitchSelector()
    }


    private var onSizeChanged: (Float) -> Unit = { }

    private fun initializeSwitchSelector() {
        switchButton.setOnClickListener {
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA
            shutdown()
            startCamera()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeSize() {
        sizeButton.setOnClickListener {
            sizeMode = when (sizeMode) {
                1f -> 0.5f
                0.5f -> -.8f
                0.8f -> 1f
                else -> 1f
            }
            onSizeChanged
                .invoke(
                    sizeMode
                )
        }
    }

    fun startCamera() {
        val cameraProviderFeature = ProcessCameraProvider.getInstance(context)
        cameraProviderFeature.addListener({
            cameraProvider = cameraProviderFeature.get()
            preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }


    @SuppressLint("RestrictedApi")
    fun shutdown() {
        cameraProvider.shutdown()
    }

    fun onSizeChanged(callback: (Float) -> Unit) {
        this.onSizeChanged = callback
    }
}