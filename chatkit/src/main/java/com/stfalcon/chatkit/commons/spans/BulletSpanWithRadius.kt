package com.stfalcon.chatkit.commons.spans

/**
 * @author Grzegorz Pawełczuk
 * @email grzegorz.pawelczuk@ftlearning.com
 * Nikkei FT Learning Limited
 * @since 29.03.2018
 */

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Path.Direction
import android.text.Layout
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.LeadingMarginSpan

/**
 * Custom Bullet Span implementation (based on [BulletSpan])
 * Default implementation doesn't allow for radius modification
 */

class BulletSpanWithRadius(private val mBulletRadius: Int = STANDARD_BULLET_RADIUS, private val mGapWidth: Int = STANDARD_GAP_WIDTH, private val mColor: Int = Color.TRANSPARENT) : LeadingMarginSpan {

    companion object {
        const val STANDARD_GAP_WIDTH = 2
        const val STANDARD_BULLET_RADIUS = 4
    }

    private var sBulletPath: Path? = null
    private val mWantColor: Boolean = mColor != Color.TRANSPARENT

    override fun getLeadingMargin(first: Boolean): Int {
        return 2 * mBulletRadius + mGapWidth
    }

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

            if (canvas.isHardwareAccelerated) {
                if (sBulletPath == null) {
                    sBulletPath = Path()
                    sBulletPath?.run{
                        addCircle(0.0f, 0.0f, 1.2f * mBulletRadius, Direction.CW)
                    }
                }
                canvas.save()
                canvas.translate(x + dir * (mGapWidth - (mBulletRadius * 1.2f + 1)), (top + bottom) / 2.0f)
                canvas.drawPath(sBulletPath!!, paint)
                canvas.restore()
            } else {
                canvas.drawCircle((x + dir * (mGapWidth - (mBulletRadius + 1))).toFloat(), (top + bottom) / 2.0f, mBulletRadius.toFloat(), paint)
            }

            if (mWantColor) {
                paint.color = oldColor
            }

            paint.style = style
        }
    }
}