package com.stfalcon.chatkit.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

/**
 * @author Grzegorz Pawełczuk
 * @email grzegorz.pawelczuk@ftlearning.com
 * Nikkei FT Learning Limited
 * @since 14.02.2018
 */

class NonbreakingSpan : ReplacementSpan() {

    override fun draw(
            canvas: Canvas,
            text: CharSequence, start: Int, end: Int,
            x: Float, top: Int, y: Int, bottom: Int,
            paint: Paint) {
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }

    override fun getSize(
            paint: Paint,
            text: CharSequence, start: Int, end: Int,
            fm: Paint.FontMetricsInt?): Int {
        return Math.round(paint.measureText(text, start, end))
    }
}

