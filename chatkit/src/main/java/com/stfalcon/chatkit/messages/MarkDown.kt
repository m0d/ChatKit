package com.stfalcon.chatkit.messages

import android.support.annotation.IntDef

/**
 * @author Mikołaj Kowal <mikolaj.kowal@nftlearning.com>
 * Nikkei FT Learning Limited
 * @since 04.01.2018
 * 14-02-2018 - Mikołaj Kowal - IntDef class with text types
 * 16-02-2018 - Grzegorz Pawełczuk - Deprecation fix
 */

object MarkDown {

    const val NONE = 0
    const val ITALIC = 1
    const val STROKE = 2
    const val LINK = 3
    const val BOLD = 4

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(BOLD.toLong(), ITALIC.toLong(), STROKE.toLong(), LINK.toLong())
    internal annotation class MarkDowns
}
