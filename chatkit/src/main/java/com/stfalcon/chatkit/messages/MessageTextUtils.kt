package com.stfalcon.chatkit.messages

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import com.github.ajalt.timberkt.w
import java.util.regex.Pattern

/**
 * @author Grzegorz Pawełczuk
 * @email grzegorz.pawelczuk@ftlearning.com
 * Nikkei FT Learning Limited
 * @since 04.01.2018
 */


class MessageTextUtils {

    companion object {
        fun getTextUrls(text: String): MutableList<UrlDescriptor> {
            val list = mutableListOf<UrlDescriptor>()
            val pattern = Pattern.compile("<(.*?)>")
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                var group = matcher.group()
                group = group.substring(1, group.length - 1)
                w { group }
                var url: UrlDescriptor? = null
                if (group.contains("|")) {
                    val split = group.split("|")
                    if (split.isNotEmpty()) {
                        url = UrlDescriptor(split[0], split[1])
                    }
                } else {
                    url = UrlDescriptor(group)
                }
                url?.run {
                    list.add(this)
                }
            }
            return list
        }

        fun transform(text: String, urls: MutableList<UrlDescriptor>, color: Int) : SpannableString {
            val descriptors : MutableList<UrlSpanDescriptor> = mutableListOf()
            var textToSpan = ""
            var urlText: String
            var textToCheck = text
            urls.forEach{ url ->
                urlText = url.toTag()
                while(textToCheck.indexOf(urlText) != -1 ){
                    val parts  = textToCheck.split(urlText)
                    if(parts.isNotEmpty()){
                        textToSpan += parts[0] + url.getLabelToDisplay()
                        descriptors.add(UrlSpanDescriptor(
                                textToSpan.length - url.getLabelToDisplay().length,
                                textToSpan.length,
                                url.url,
                                url.label
                            )
                        )
                        textToCheck = if(parts.size > 1) {
                            var rest = ""
                            (1 until parts.size).forEach {
                                if(it > 1){
                                    rest += url.toTag()
                                }
                                rest += parts[it]
                            }
                            rest
                        }else{
                            ""
                        }
                    }else{
                        textToCheck = ""
                    }
                }
            }

            textToSpan += textToCheck
            val spannableString = SpannableString(textToSpan)

            descriptors.forEach{ url ->
                spannableString.setSpan(URLSpan(url.url), url.startIndex, url.endIndex, 0)
                spannableString.setSpan(ForegroundColorSpan(color), url.startIndex, url.endIndex, 0)
            }

            return spannableString
        }
    }

    data class UrlSpanDescriptor(val startIndex: Int, val endIndex : Int, val url: String, val label: String? = null)

    data class UrlDescriptor(val url: String, val label: String? = null){
        fun toTag() : String{
            var swapData = "<$url"
            label?.run {
                swapData += "|$label"
            }
            return swapData + ">"
        }

        fun getLabelToDisplay() : String {
            return label ?: url
        }
    }
}

