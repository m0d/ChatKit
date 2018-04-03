package com.stfalcon.chatkit.commons.spans

import android.text.style.URLSpan
import android.view.View
import com.stfalcon.chatkit.commons.events.OpenAppearIn
import org.greenrobot.eventbus.EventBus

/**
 * @author Maciej Madetko
 * @email maciej.madetko@ftlearning.com
 * Nikkei FT Learning Limited
 * @since 21/03/2018.
 */
class CustomUrlSpan(customUl: String) : URLSpan(customUl) {
    override fun onClick(widget: View?) {
        if (url.startsWith("https://appear.in")) {
            EventBus.getDefault().post(OpenAppearIn(url))
        } else {
            super.onClick(widget)
        }
    }
}