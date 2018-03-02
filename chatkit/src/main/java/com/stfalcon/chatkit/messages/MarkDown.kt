package com.stfalcon.chatkit.messages

import android.support.annotation.IntDef

/**
 * @author Mikołaj Kowal <mikolaj.kowal@nftlearning.com>
 * Nikkei FT Learning Limited
 * @since 04.01.2018
 * 14-02-2018 - Mikołaj Kowal - IntDef class with text types
 * 16-02-2018 - Grzegorz Pawełczuk - Deprecation fix
 * 01-03-2018 - Grzegorz Pawełczuk - QUOTE++
 */

object MarkDown {

    const val NONE = 0L
    const val ITALIC = 1L
    const val STROKE = 2L
    const val LINK = 3L
    const val BOLD = 4L
    const val QUOTE = 5L

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(NONE, BOLD, ITALIC, STROKE, LINK, QUOTE)
    internal annotation class MarkDowns
}
