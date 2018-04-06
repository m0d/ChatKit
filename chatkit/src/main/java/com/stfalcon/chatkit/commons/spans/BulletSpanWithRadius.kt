package com.stfalcon.chatkit.commons.spans

/**
 * @author Grzegorz Pawełczuk
 * @email grzegorz.pawelczuk@ftlearning.com
 * Nikkei FT Learning Limited
 * @since 29.03.2018
 */

import android.graphics.*
import android.graphics.Path.Direction
import android.text.Layout
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.LeadingMarginSpan

/**
 * Custom Bullet Span implementation (based on [BulletSpan])
 * Default implementation doesn't allow for radius modification
 */

class BulletSpanWithRadius(private val mGapWidth: Int = STANDARD_GAP_WIDTH, private val mColor: Int = Color.TRANSPARENT, private val forceRecalculate: Boolean = false) : LeadingMarginSpan {

    companion object {
        const val STANDARD_GAP_WIDTH = 2
        const val DIM_TEST_STRING = "1."
    }

    private var sBulletPath: Path? = null
    private val mWantColor: Boolean = mColor != Color.TRANSPARENT
    private var mBulletRadius: Float = 0F
    private var mBulletMargin: Int = 0

    override fun getLeadingMargin(first: Boolean): Int = mGapWidth

    override fun drawLeadingMargin(canvas: Canvas, paint: Paint, x: Int, dir: Int, top: Int, baseline: Int, bottom: Int,
                                   text: CharSequence, start: Int, end: Int, first: Boolean, l: Layout) {
        if ((text as Spanned).getSpanStart(this) == start) {
            val style = paint.style
            var oldColor = 0

            if (mWantColor) {
                oldColor = paint.color
                paint.color = mColor
            }

            paint.style = Paint.Style.FILL

            if(mBulletRadius == 0F || forceRecalculate) {
                val mTextBounds = Rect()
                paint.getTextBounds(DIM_TEST_STRING, 0, 1, mTextBounds)
                mBulletRadius = mTextBounds.height() / 4F
                mBulletMargin = mTextBounds.width()
            }

            val xAxis = x + dir * (mGapWidth - (mBulletRadius * 2) - mBulletMargin)
            val yAxis = (top + bottom) / 2F

            if (canvas.isHardwareAccelerated) {
                if (sBulletPath == null) {
                    sBulletPath = Path()
                    sBulletPath?.run{
                        addCircle(0F, 0F, mBulletRadius, Direction.CW)
                    }
                }
                canvas.save()
                canvas.translate(xAxis, yAxis)
                canvas.drawPath(sBulletPath!!, paint)
                canvas.restore()
            } else {
                canvas.drawCircle(xAxis, yAxis, mBulletRadius, paint)
            }

            if (mWantColor) {
                paint.color = oldColor
            }

            paint.style = style
        }
    }
}