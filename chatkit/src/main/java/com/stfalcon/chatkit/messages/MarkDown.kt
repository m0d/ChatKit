package com.stfalcon.chatkit.messages

import android.support.annotation.IntDef

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @author Mikołaj Kowal <mikolaj.kowal@nftlearning.com>
 * Nikkei FT Learning Limited
 * @since 04.01.2018
 * 14-02-2018 - Mikołaj Kowal - IntDef class with text types
 */

object MarkDown {

    const val NONE = 0
    const val ITALIC = 1
    const val STROKE = 2
    const val LINK = 3
    const val BOLD = 4

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(BOLD.toLong(), ITALIC.toLong(), STROKE.toLong(), LINK.toLong())
    internal annotation class MarkDowns
}
