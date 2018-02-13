package com.stfalcon.chatkit.messages;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Mikołaj on 2/13/2018.
 */

public class MarkDown {

    @Retention(RetentionPolicy.SOURCE)

    @IntDef({
        BOLD,
        ITALIC,
        STROKE,
        LINK
    })
    @interface MarkDowns{

    }

    public static final int NONE = 0;
    public static final int ITALIC = 1;
    public static final int STROKE = 2;
    public static final int LINK = 3;
    public static final int BOLD = 4;
}
