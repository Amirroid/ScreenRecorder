package ir.amirroid.screenrecorder.ui.components.native

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import ir.amirroid.screenrecorder.utils.toDp
import kotlin.math.abs


class ColorPickerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attributeSet, defStyle) {
    private val colors = intArrayOf(
        Color.RED,
        Color.MAGENTA,
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.YELLOW,
        Color.WHITE,
        Color.BLACK,
    )

    private var centerX = 7.toDp(context)
        private set(value) {
            field = value.coerceIn(7.toDp(context), width.minus(7.toDp(context)))
            invalidate()
        }


    private var radius = 6.toDp(context)
        private set(value) {
            field = value
            invalidate()
        }


    private lateinit var bitmap: Bitmap

    private var onColorChanged: (Int) -> Unit = {}

    //    private val positions = floatArrayOf(
//        width / 6f,
//        width / 5f,
//        width / 4f,
//        width / 3f,
//        width / 2f,
//        width.toFloat(),
//    )


    private val paint = Paint()
        .apply {
            isAntiAlias = true
            style = Paint.Style.FILL_AND_STROKE
        }

    //    )

    private fun createBitmap() {
        paint.apply {
            isDither = true
            shader =
                LinearGradient(
                    6.toDp(context),
                    height.div(2f),
                    width.toFloat().minus(6.toDp(context)),
                    height.div(2f),
                    colors,
                    null,
                    Shader.TileMode.MIRROR
                )
        }
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawRoundRect(
            RectF(
                6.toDp(context),
                0f,
                width.toFloat().minus(6.toDp(context)),
                height.toFloat()
            ),
            height.div(2f),
            height.div(2f),
            paint
        )
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        createBitmap()
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        paint.shader = null
        paint.color = Color.BLACK
        canvas.drawCircle(centerX, height.div(2f), radius.plus(1.toDp(context)), paint)
        paint.color = Color.WHITE
        canvas.drawCircle(centerX, height.div(2f), radius, paint)
        super.onDraw(canvas)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val isBigRadius =
            MotionEvent.ACTION_DOWN == event.action || event.action == MotionEvent.ACTION_MOVE
        ValueAnimator.ofFloat(
            radius,
            if (isBigRadius) 8.toDp(context) else 6.toDp(context)
        ).apply {
            addUpdateListener {
                radius = it.animatedValue as Float
            }
            start()
        }
        centerX = event.x
        onColorChanged.invoke(bitmap.getPixel(centerX.toInt(), height.div(2)))
        return true
    }

    fun setOnColorChanged(onColorChanged: (Int) -> Unit) {
        this.onColorChanged = onColorChanged
    }

    fun getColor(): Int {
        return bitmap.getPixel(centerX.toInt(), height.div(2))
    }
}