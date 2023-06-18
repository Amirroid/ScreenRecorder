package ir.amirroid.screenrecorder.ui.components.native

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ir.amirroid.screenrecorder.utils.toDp

class PaintView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    private val paint = Paint().apply {
        color = Color.RED
        strokeWidth = 8.toDp(context)
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private var lastColor: Int = Color.RED
    private var removePaths = mutableListOf<Pair<Path, Int>>()

    var mode = Mode.PAINT

    private var callback: () -> Unit = {}

    private var paths = mutableListOf<Pair<Path, Int>>()
    private var path = Path()
    private var lastX = 0f
    private var lastY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (mode) {
            Mode.PAINT -> touchPaint(event)
            Mode.RECTANGLE -> touchRectangle(event)
            Mode.CIRCLE -> touchCircle(event)
        }
        invalidate()
        callback.invoke()
        return true
    }

    private fun touchCircle(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(event.x, event.y)
                lastX = event.x
                lastY = event.y
            }

            MotionEvent.ACTION_MOVE -> {
                path = Path()
                val inX = event.x
                val inY = event.y
                path.addOval(
                    lastX,
                    lastY,
                    inX,
                    inY,
                    Path.Direction.CW
                )
            }

            MotionEvent.ACTION_UP -> {
                paths.add(Pair(path, lastColor))
                path = Path()
            }
        }
    }

    private fun touchRectangle(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(event.x, event.y)
                lastX = event.x
                lastY = event.y
            }

            MotionEvent.ACTION_MOVE -> {
                path = Path()
                val inX = event.x
                val inY = event.y
                path.addRoundRect(
                    lastX,
                    lastY,
                    inX,
                    inY,
                    6.toDp(context),
                    6.toDp(context),
                    Path.Direction.CW
                )
            }

            MotionEvent.ACTION_UP -> {
                paths.add(Pair(path, lastColor))
                path = Path()
            }
        }
    }

    private fun touchPaint(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(event.x, event.y)
                lastX = event.x
                lastY = event.y
            }

            MotionEvent.ACTION_MOVE -> {
                val inX = event.x
                val inY = event.y
                path.cubicTo(
                    lastX,
                    lastY,
                    (inX + lastX) / 2,
                    (inY + lastY) / 2,
                    inX,
                    inY
                )
                lastX = inX
                lastY = inY
            }

            MotionEvent.ACTION_UP -> {
                paths.add(Pair(path, lastColor))
                path = Path()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        for (path in paths) {
            paint.color = path.second
            canvas.drawPath(
                path.first,
                paint
            )
        }
        paint.color = lastColor
        canvas.drawPath(path, paint)
        super.onDraw(canvas)
    }

    fun changeColor(color: Int) {
        lastColor = color
    }

    fun clear() {
        removePaths.addAll(paths)
        path.reset()
        paths.clear()
        invalidate()
    }

    fun undo() {
        if (paths.isNotEmpty()) {
            val lastPath = paths.last()
            removePaths.add(lastPath)
            paths.remove(lastPath)
            invalidate()
        }
    }

    fun redo() {
        if (removePaths.isNotEmpty()) {
            paths.add(removePaths.last())
            removePaths.removeLast()
            invalidate()
        }
    }
}

enum class Mode {
    PAINT,
    RECTANGLE,
    CIRCLE,
}