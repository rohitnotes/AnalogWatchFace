package com.vlad1m1r.watchface.components.ticks

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import com.vlad1m1r.watchface.R
import com.vlad1m1r.watchface.data.ColorStorage
import com.vlad1m1r.watchface.data.DataStorage
import com.vlad1m1r.watchface.model.Mode
import com.vlad1m1r.watchface.model.Point
import com.vlad1m1r.watchface.utils.getLighterGrayscale
import kotlin.math.*

class TicksLayout1(context: Context, dataStorage: DataStorage, colorStorage: ColorStorage) : TicksLayout(context, dataStorage) {

    private val tickLength = context.resources.getDimension(R.dimen.design1_tick_length)
    private val watchHourTickColor = colorStorage.getHourTicksColor()
    private val tickWidth = context.resources.getDimension(R.dimen.design1_tick_width)

    private val tickLengthMinute = context.resources.getDimension(R.dimen.design1_tick_length_minute)
    private val watchMinuteTickColor = colorStorage.getMinuteTicksColor()
    private val tickWidthMinute = context.resources.getDimension(R.dimen.design1_tick_width_minute)

    private val tickBurnInPadding = context.resources.getDimension(R.dimen.design1__tick_padding)
    private var tickPadding = tickBurnInPadding

    override var centerInvalidated = true
        private set

    private val tickPaint = Paint().apply {
        color = watchHourTickColor
        strokeWidth = tickWidth
        isAntiAlias = true
        style = Paint.Style.STROKE
        setShadowLayer(
            shadowRadius, 0f, 0f, shadowColor
        )
    }
    private val tickPaintMinute = Paint().apply {
        color = watchMinuteTickColor
        strokeWidth = tickWidthMinute
        isAntiAlias = true
        style = Paint.Style.STROKE
        setShadowLayer(
            shadowRadius, 0f, 0f, shadowColor
        )
    }

    private var center = Point()
    private var outerTickRadius: Float = 0f
    private var innerTickRadius: Float = 0f
    private var innerTickRadiusMinute: Float = 0f

    override fun setCenter(center: Point) {
        centerInvalidated = false
        this.center = center
        this.outerTickRadius = center.x - tickPadding
        this.innerTickRadius = center.x - tickLength - tickPadding
        this.innerTickRadiusMinute = center.x - tickLengthMinute - tickPadding
    }

    override fun draw(canvas: Canvas) {
        for (tickIndex in 0..59) {
            val tickRotation = tickIndex * PI / 30

            val adjust = if(shouldAdjustToSquareScreen) adjustToSquare(tickRotation) else 1.0

            if (tickIndex % 5 == 0) {

                val innerX = sin(tickRotation) * innerTickRadius * adjust
                val innerY = -cos(tickRotation) * innerTickRadius * adjust

                val outerX = sin(tickRotation) * outerTickRadius * adjust
                val outerY = -cos(tickRotation) * outerTickRadius * adjust



                canvas.drawLine(
                    (center.x + innerX).toFloat(), (center.y + innerY).toFloat(),
                    (center.x + outerX).toFloat(), (center.y + outerY).toFloat(), tickPaint
                )
            } else {
                val innerX = sin(tickRotation) * innerTickRadiusMinute * adjust
                val innerY = -cos(tickRotation) * innerTickRadiusMinute * adjust
                val outerX = sin(tickRotation) * outerTickRadius * adjust
                val outerY = -cos(tickRotation) * outerTickRadius * adjust
                canvas.drawLine(
                    (center.x + innerX).toFloat(), (center.y + innerY).toFloat(),
                    (center.x + outerX).toFloat(), (center.y + outerY).toFloat(), tickPaintMinute
                )
            }
        }
    }

    override fun setMode(mode: Mode) {
        tickPaint.apply {
            if (mode.isAmbient) {
                inAmbientMode(getLighterGrayscale(watchHourTickColor))
                if (mode.isBurnInProtection) {
                    strokeWidth = 0f
                }
            } else {
                inInteractiveMode(watchHourTickColor)
                strokeWidth = tickWidth
            }
        }
        tickPaintMinute.apply {
            if (mode.isAmbient) {
                inAmbientMode(getLighterGrayscale(watchMinuteTickColor))
                if (mode.isBurnInProtection) {
                    strokeWidth = 0f
                }
            } else {
                inInteractiveMode(watchMinuteTickColor)
                strokeWidth = tickWidthMinute
            }
        }
        tickPadding = if (shouldAdjustForBurnInProtection(mode)) {
            tickBurnInPadding
        } else {
            -2f
        }
        centerInvalidated = true
    }
}