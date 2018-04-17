package com.stfalcon.chatkit.commons.spans

/**
 * @author Grzegorz Pawełczuk
 * @email grzegorz.pawelczuk@ftlearning.com
 * Nikkei FT Learning Limited
 * @since 04.04.2018
 */

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import android.text.style.LineBackgroundSpan


/**
 * Custom Bullet Span implementation (based on [LeadingMarginSpan])
 */

class QuoteSpan(private val mGapWidth: Int = STANDARD_GAP_WIDTH, private val mColor: Int = Color.TRANSPARENT) : LeadingMarginSpan, LineBackgroundSpan {

    companion object {
        const val STANDARD_GAP_WIDTH = 2
        const val QUOTE_SIZE_RATIO_FACTOR = 3F
    }

    private val mWantColor: Boolean = mColor != Color.TRANSPARENT
    override fun getLeadingMargin(first: Boolean): Int = mGapWidth

    override fun drawLeadingMargin(canvas: Canvas, paint: Paint, x: Int, dir: Int, top: Int, baseline: Int, bottom: Int,
                                   text: CharSequence, start: Int, end: Int, first: Boolean, l: Layout) {}

    override fun drawBackground(c: Canvas, p: Paint, left: Int, right: Int, top: Int, baseline: Int, bottom: Int, text: CharSequence, start: Int, end: Int, lnum: Int) {
        val paintColor = p.color
        if(mWantColor) {
            p.color = mColor
        }
        c.drawRect(left.toFloat(), top.toFloat(), mGapWidth / QUOTE_SIZE_RATIO_FACTOR, bottom.toFloat(), p)
        p.color = paintColor
    }
}