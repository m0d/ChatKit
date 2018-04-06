package com.stfalcon.chatkit.commons.spans

/**
 * @author Grzegorz Pawełczuk
 * @email grzegorz.pawelczuk@ftlearning.com
 * Nikkei FT Learning Limited
 * @since 29.03.2018
 */

import android.graphics.*
import android.text.Layout
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.LeadingMarginSpan

/**
 * Custom Numbered Span implementation (based on [BulletSpan])
 */

class NumberedSpan(
        private val mRowNumber: String = "1.",
        private val mGapWidth: Int = STANDARD_GAP_WIDTH,
        private val mColor: Int = Color.TRANSPARENT) : LeadingMarginSpan {

    companion object {
        const val STANDARD_GAP_WIDTH = 2
        const val TEXT_SIZE_RATIO_FACTOR = 5F/6F
    }

    private val mWantColor: Boolean = mColor != Color.TRANSPARENT
    private val mNumberToTextMargin: Int = mGapWidth * 1/8

    override fun getLeadingMargin(first: Boolean): Int = mGapWidth

    override fun drawLeadingMargin(canvas: Canvas, paint: Paint, x: Int, dir: Int, top: Int, baseline: Int, bottom: Int,
                                   text: CharSequence, start: Int, end: Int, first: Boolean, l: Layout) {
        if ((text as Spanned).getSpanStart(this) == start) {
            val styleMem = paint.style
            val sizeMem  = paint.textSize
            val alignMem = paint.textAlign
            var oldColor = 0

            if (mWantColor) {
                oldColor = paint.color
                paint.color = mColor
            }
            with(paint) {
                style = Paint.Style.FILL
                textSize = sizeMem * TEXT_SIZE_RATIO_FACTOR
                textAlign = Paint.Align.RIGHT
            }

            canvas.drawText(mRowNumber, x.toFloat() + mGapWidth - mNumberToTextMargin, baseline.toFloat(), paint)

            if (mWantColor) {
                paint.color = oldColor
            }

            with(paint) {
                style = styleMem
                textSize = sizeMem
                textAlign = alignMem
            }
        }
    }
}