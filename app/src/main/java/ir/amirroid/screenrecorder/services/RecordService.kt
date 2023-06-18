package ir.amirroid.screenrecorder.services

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PixelFormat
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleService
import dagger.hilt.android.AndroidEntryPoint
import ir.amirroid.screenrecorder.MainActivity
import ir.amirroid.screenrecorder.R
import ir.amirroid.screenrecorder.data.db.SavedVideoDao
import ir.amirroid.screenrecorder.data.managers.NotificationManager
import ir.amirroid.screenrecorder.data.models.RecorderModel
import ir.amirroid.screenrecorder.data.models.VideoRecorded
import ir.amirroid.screenrecorder.data.repository.RecordRepository
import ir.amirroid.screenrecorder.ui.components.*
import ir.amirroid.screenrecorder.ui.components.native.CameraCustomView
import ir.amirroid.screenrecorder.ui.components.native.ColorPickerView
import ir.amirroid.screenrecorder.ui.components.native.Mode
import ir.amirroid.screenrecorder.ui.components.native.PaintView
import ir.amirroid.screenrecorder.utils.Constants
import ir.amirroid.screenrecorder.utils.RecorderUtils
import ir.amirroid.screenrecorder.utils.formatTime
import ir.amirroid.screenrecorder.utils.setTint
import ir.amirroid.screenrecorder.utils.toDp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import kotlin.math.abs
import kotlin.properties.Delegates


const val RESULT_CODE = "Amir_Record"
const val RESULT_DATA = "Amir_Record_Data"

@AndroidEntryPoint
class RecordService : LifecycleService() {
    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var recorderUtils: RecorderUtils

    @Inject
    lateinit var repository: RecordRepository


    private var isWindow by Delegates.notNull<Boolean>()

    private lateinit var windowManager: WindowManager

    private lateinit var layoutParams: WindowManager.LayoutParams

    private var width: Int = 0
    private var widthView: Int = 0


    private var cameraSize: Int = 0


    private var floatingFirstX = 0
    private var floatingFirstY = 0
    private var floatingLastX = 0
    private var floatingLastY = 0

    private lateinit var floatingView: ViewGroup
    private lateinit var paintViewGroup: ViewGroup
    private lateinit var timerView: TextView
    private lateinit var touchableView: View
    private lateinit var stopView: View
    private lateinit var paintViewButton: ImageView
    private lateinit var toolboxPaint: LinearLayout
    private lateinit var clearView: ImageView
    private lateinit var undoView: ImageView
    private lateinit var redoView: ImageView
    private lateinit var circleMode: ImageView
    private lateinit var penMode: ImageView
    private lateinit var rectangleMode: ImageView
    private lateinit var paintView: PaintView
    private lateinit var colorPicker: ColorPickerView
    private lateinit var closeButton: ImageView
    private lateinit var bgToggleView: ImageView
    private lateinit var cameraButton: ImageView
    private lateinit var cameraView: CameraCustomView


    private var isBgPaintView = false


    private lateinit var layoutParamsPaint: WindowManager.LayoutParams


    private lateinit var layoutParamsCamera: WindowManager.LayoutParams

    private var layoutType by Delegates.notNull<Int>()

    private lateinit var inflater: LayoutInflater

    private var isPaintViewShowed = false


    private var isCamera = false


    private var time = 0L

    private val job = Job()
    private val scope = CoroutineScope(job)


    // recorder tools
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private lateinit var mediaProjection: MediaProjection
    private lateinit var mediaRecorder: MediaRecorder
    private var virtualDisplay: VirtualDisplay? = null
    private lateinit var mediaProjectionCallback: MediaProjection.Callback
    private lateinit var name: String
    private var isRecording = false
    private lateinit var recorderData: RecorderModel


    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startForeground(313, notificationManager.getNotification())
        recorderData =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) intent?.extras?.getParcelable(
                Constants.DATA_RECORD, RecorderModel::class.java
            )!! else intent?.extras?.getParcelable(
                Constants.DATA_RECORD
            )!!
        name = recorderUtils.getNameForFile()
        Log.d("TAGRECORD", "onStartCommand: $recorderData")
        initializeRecorder()
        initializeProjection(intent)
        isWindow = intent?.getBooleanExtra(Constants.HAS_DRAW_OVERLY_FEATURE, false) ?: false
        if (isWindow) {
            initializeSizes()
            initializeViews()
            addWindow()
        }
        return START_STICKY
    }

    private fun initializeRecorder() {
        mediaRecorder = MediaRecorder()
        val orientation = resources.configuration.orientation
        val rotation = recorderUtils.orientation.get(orientation)
        mediaProjectionCallback = MediaProjectionCallback()
        recorderUtils.initializeRecorder(
            mediaRecorder,
            recorderData.audioSource,
            name,
            rotation,
            recorderData.frameRate,
            recorderData.bitrate,
            recorderData.width,
            recorderData.height
        )
    }

    private fun initializeProjection(intent: Intent?) {
        mediaProjectionManager = getSystemService(MediaProjectionManager::class.java)
        mediaProjection = mediaProjectionManager.getMediaProjection(
            intent?.getIntExtra(RESULT_CODE, 1) ?: 1,
            (
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent?.extras?.getParcelable(RESULT_DATA, Intent::class.java)!!
                    } else {
                        intent?.extras?.getParcelable(RESULT_DATA)!!
                    }
                    )
        )
        mediaProjection.registerCallback(mediaProjectionCallback, Handler())
        virtualDisplay = recorderUtils.createVirtualDisplay(
            mediaProjection,
            recorderData.width,
            recorderData.height,
            resources.displayMetrics.densityDpi,
            mediaRecorder
        )
        mediaRecorder.start()
        isRecording = true
    }

    private fun initializeSizes() {
        val matrix = resources.displayMetrics
        widthView = 48.toDp(this).toInt()
        cameraSize = 120.toDp(this).toInt()
        width = matrix.widthPixels
    }

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    private fun initializeViews() {
        inflater = LayoutInflater.from(this)
        floatingView = inflater.inflate(R.layout.floating_window, null) as LinearLayout
        touchableView = floatingView.findViewById(R.id.touch)
        touchableView.setOnTouchListener { _, motion ->
            val flm = layoutParams
            val totalX = floatingLastX - floatingFirstX
            val totalY = floatingLastY - floatingFirstY
            when (motion.action) {
                MotionEvent.ACTION_DOWN -> {
                    floatingLastX = motion.rawX.toInt()
                    floatingLastY = motion.rawY.toInt()
                    floatingFirstX = floatingLastX
                    floatingFirstY = floatingLastY
                    startAlphaOutAnimation()
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaX = motion.rawX.toInt() - floatingLastX
                    val deltaY = motion.rawY.toInt() - floatingLastY
                    floatingLastX = motion.rawX.toInt()
                    floatingLastY = motion.rawY.toInt()
                    if (abs(totalX) >= 10 || abs(totalY) >= 10) {
                        if (motion.pointerCount == 1) {
                            flm.x += deltaX
                            flm.y += deltaY
                            windowManager.updateViewLayout(floatingView, flm)
                        }
                    }
                }

                MotionEvent.ACTION_UP -> {
                    val x = flm.x

                    (if (x <= width.div(2)) ValueAnimator.ofInt(
                        x,
                        0
                    ) else ValueAnimator.ofInt(
                        x,
                        width.minus(48.toDp(this).toInt())
                    ))
                        .apply {
                            duration = 500
                            interpolator = LinearInterpolator()
                            addUpdateListener {
                                flm.x = it.animatedValue as Int
                                windowManager.updateViewLayout(floatingView, layoutParams)
                            }
                            addListener(object : Animator.AnimatorListener {
                                override fun onAnimationStart(p0: Animator) = Unit

                                override fun onAnimationEnd(p0: Animator) {
                                    startAlphaInAnimation()
                                }

                                override fun onAnimationCancel(p0: Animator) = Unit

                                override fun onAnimationRepeat(p0: Animator) = Unit
                            })
                            start()
                        }
                }
            }
            true
        }
        paintViewGroup = inflater.inflate(R.layout.floating_window_paint, null) as FrameLayout
        paintView = paintViewGroup.findViewById(R.id.paint_view)
        stopView = floatingView.findViewById(R.id.stop)
        stopView.setOnClickListener {
            stopRecord()
            stopSelf()
        }
        timerView = floatingView.findViewById(R.id.timer)
        paintViewButton = floatingView.findViewById(R.id.paint)
        toolboxPaint = paintViewGroup.findViewById(R.id.toolbox_paint)
        clearView = paintViewGroup.findViewById(R.id.clear)
        undoView = paintViewGroup.findViewById(R.id.undo)
        redoView = paintViewGroup.findViewById(R.id.redo)
        colorPicker = paintViewGroup.findViewById(R.id.color_picker)
        penMode = paintViewGroup.findViewById(R.id.pen_mode)
        circleMode = paintViewGroup.findViewById(R.id.circle_mode)
        rectangleMode = paintViewGroup.findViewById(R.id.rectangle_mode)
        closeButton = paintViewGroup.findViewById(R.id.close)
        bgToggleView = paintViewGroup.findViewById(R.id.bg_toggle)
        cameraButton = floatingView.findViewById(R.id.camera)
        cameraView = CameraCustomView(this, this)
        startTimer()
        initializePaint()
        initializeCamera()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeCamera() {
        cameraButton.setOnClickListener {
            if (isCamera) {
                cameraButton.setTint(Color.BLACK)
                windowManager.removeView(cameraView)
            } else {
                cameraView.startCamera()
                cameraButton.setTint(Color.RED)
                windowManager.addView(cameraView, layoutParamsCamera)
            }
            isCamera = isCamera.not()
        }
        cameraView.setOnTouchListener { _, motionEvent ->
            val flm = layoutParamsCamera
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    floatingLastX = motionEvent.rawX.toInt()
                    floatingLastY = motionEvent.rawY.toInt()
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaX = motionEvent.rawX.toInt() - floatingLastX
                    val deltaY = motionEvent.rawY.toInt() - floatingLastY
                    floatingLastX = motionEvent.rawX.toInt()
                    floatingLastY = motionEvent.rawY.toInt()
                    flm.x += deltaX
                    flm.y += deltaY
                    windowManager.updateViewLayout(cameraView, layoutParamsCamera)
                }
            }
            true
        }
        cameraView.onSizeChanged {
            val size = (200.toDp(this) * it).toInt()
            val flm = layoutParamsCamera
            cameraView.layoutParams = FrameLayout.LayoutParams(
                size,
                size
            )
            flm.width = size
            flm.height = size
            windowManager.updateViewLayout(cameraView, flm)
        }
    }

    private fun initializePaint() {
        paintViewButton.setOnClickListener {
            if (isPaintViewShowed) {
                windowManager.removeView(paintViewGroup)
                paintViewButton.setTint(Color.BLACK)
            } else {
                windowManager.addView(paintViewGroup, layoutParamsPaint)
                paintViewButton.setTint(Color.RED)
            }
            isPaintViewShowed = isPaintViewShowed.not()
        }
        clearView.setOnClickListener {
            paintView.clear()
        }
        redoView.setOnClickListener {
            paintView.redo()
        }
        undoView.setOnClickListener {
            paintView.undo()
        }
        colorPicker.setOnColorChanged {
            when (paintView.mode) {
                Mode.PAINT -> penMode.setTint(it)
                Mode.RECTANGLE -> rectangleMode.setTint(it)
                Mode.CIRCLE -> circleMode.setTint(it)
            }
            if (isBgPaintView) {
                bgToggleView.setTint(colorPicker.getColor())
            }
            paintView.changeColor(it)
        }
        closeButton.setOnClickListener {
            windowManager.removeView(paintViewGroup)
            paintViewButton.setTint(Color.BLACK)
            isPaintViewShowed = false
        }
        bgToggleView.setOnClickListener {
            if (isBgPaintView) {
                bgToggleView.setTint(Color.WHITE)
                paintView.setBackgroundColor(Color.TRANSPARENT)
            } else {
                bgToggleView.setTint(colorPicker.getColor())
                paintView.setBackgroundColor(Color.WHITE)
            }
            isBgPaintView = isBgPaintView.not()
        }
        initializeModesOfPaint()
    }

    private fun initializeModesOfPaint() {
        penMode.setTint(Color.RED)
        circleMode.setTint(Color.WHITE)
        rectangleMode.setTint(Color.WHITE)
        penMode.setOnClickListener {
            penMode.setTint(colorPicker.getColor())
            circleMode.setTint(Color.WHITE)
            rectangleMode.setTint(Color.WHITE)
            paintView.mode = Mode.PAINT
        }
        circleMode.setOnClickListener {
            circleMode.setTint(colorPicker.getColor())
            penMode.setTint(Color.WHITE)
            rectangleMode.setTint(Color.WHITE)
            paintView.mode = Mode.CIRCLE
        }
        rectangleMode.setOnClickListener {
            rectangleMode.setTint(colorPicker.getColor())
            circleMode.setTint(Color.WHITE)
            penMode.setTint(Color.WHITE)
            paintView.mode = Mode.RECTANGLE
        }
    }

    private fun stopRecord() {
        if (virtualDisplay == null) return
        if (isRecording) {
            scope.launch {
                repository.insertVide(
                    VideoRecorded(
                        filePath = recorderUtils.file.path,
                        fileName = name,
                        duration = time,
                        dateAdded = Date().time
                    )
                )
            }
            virtualDisplay!!.release()
            mediaProjection.unregisterCallback(mediaProjectionCallback)
            mediaProjection.stop()
            mediaRecorder.stop()
            mediaRecorder.reset()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun startTimer() {
        timerView.text = "00:00:00"
        scope.launch {
            repeat(Int.MAX_VALUE) {
                delay(1000)
                time += 1000
                withContext(Dispatchers.Main) {
                    timerView.text = formatTime(time)
                }
            }
        }
    }

    private fun startAlphaInAnimation() {
        ValueAnimator.ofFloat(1f, 0.3f)
            .apply {
                addUpdateListener {
                    val alpha = it.animatedValue as Float
                    floatingView.alpha = alpha
                }
                start()
            }
    }

    private fun startAlphaOutAnimation() {
        ValueAnimator.ofFloat(0.3f, 1f)
            .apply {
                addUpdateListener {
                    val alpha = it.animatedValue as Float
                    floatingView.alpha = alpha
                }
                start()
            }
    }

    override fun onCreate() {
        super.onCreate()
    }

    private fun addWindow() {
        windowManager = getSystemService(WindowManager::class.java)
        initializeParams()


        windowManager.addView(floatingView, layoutParams)
        startAlphaInAnimation()

    }

    private fun initializeParams() {
        layoutType =
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        layoutParams = WindowManager.LayoutParams(
            widthView,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        layoutParams.gravity = Gravity.START
        layoutParams.x = 0
        layoutParams.y = 0


        layoutParamsPaint = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        layoutParamsPaint.gravity = Gravity.START
        layoutParamsPaint.x = 0
        layoutParamsPaint.y = 0



        layoutParamsCamera = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        layoutParamsCamera.gravity = Gravity.TOP.or(Gravity.START)
        layoutParamsCamera.x = 0
        layoutParamsCamera.y = 0
        if (recorderData.openDefaultCamera) {
            isCamera = true
            cameraButton.setTint(Color.RED)
            windowManager.addView(cameraView.apply { startCamera() }, layoutParamsCamera)
        }
    }


    override fun onDestroy() {
        windowManager.removeView(floatingView)
        if (isPaintViewShowed) {
            windowManager.removeView(paintViewGroup)
        }
        if (isCamera) {
            cameraView.shutdown()
            windowManager.removeView(cameraView)
        }
        scope.cancel()
        stopForeground(true)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        super.onDestroy()
    }

    private inner class MediaProjectionCallback : MediaProjection.Callback() {
        override fun onStop() {
            if (isRecording) {
                isRecording = false
            }
            stopSelf()
        }
    }
}