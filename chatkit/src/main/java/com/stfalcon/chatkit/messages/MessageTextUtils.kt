package com.stfalcon.chatkit.messages

import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import java.util.regex.Pattern

/**
 * @author Grzegorz Pawełczuk
 * @email grzegorz.pawelczuk@ftlearning.com
 * Nikkei FT Learning Limited
 * @since 04.01.2018
 */


class MessageTextUtils {

    companion object {
        fun getTextPatterns(text: String): MutableList<PatternDescriptor> {
            val list = mutableListOf<PatternDescriptor>()
            val pattern = Pattern.compile("<(.*?)>|\\*(.*?)\\*|_(.*?)_|~(.*?)~")
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                var group = matcher.group()
                var isLink = false
                var isBold = false
                var isItalic = false
                var isStroke = false
                when{
                    group.startsWith("<") -> isLink = true
                    group.startsWith("*") -> isBold = true
                    group.startsWith("_") -> isItalic = true
                    group.startsWith("~") -> isStroke = true
                }

                group = group.substring(1, group.length - 1)
                //w{"GROUP: $group"}

                var url: PatternDescriptor? = null
                if (group.contains("|") && isLink) {
                    val split = group.split("|")
                    if (split.isNotEmpty()) {
                        url = PatternDescriptor(split[0], split[1], true, isBold, isItalic, isStroke)
                    }
                } else {
                    url = PatternDescriptor(group, null, isLink, isBold, isItalic, isStroke)
                }
                url?.run {
                    list.add(this)
                }
            }
            //i{"FOUND: $list"}
            return list
        }

        fun transform(text: String, urls: MutableList<PatternDescriptor>, color: Int) : SpannableString {
            val descriptors : MutableList<PatternSpanDescriptor> = mutableListOf()
            var textToSpan = ""
            var urlText: String
            var textToCheck = text
            urls.forEach{ url ->
                urlText = url.toTag()
                //w{"   SEARCH: $urlText in $textToCheck"}
                if(textToCheck.indexOf(urlText) != -1 ){
                    val parts  = textToCheck.split(urlText)
                    //w{"      PARTS: $parts"}
                    if(parts.isNotEmpty()){
                        textToSpan += parts[0] + url.getLabelToDisplay()

                        descriptors.add(PatternSpanDescriptor(
                                textToSpan.length - url.getLabelToDisplay().length,
                                textToSpan.length,
                                url.content,
                                url.label,
                                url.isLink,
                                url.isBold,
                                url.isItalic,
                                url.isStroke
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

            descriptors.forEach{ content ->
                when {
                    content.isLink -> {
                        spannableString.setSpan(URLSpan(content.content), content.startIndex, content.endIndex, 0)
                        spannableString.setSpan(ForegroundColorSpan(color), content.startIndex, content.endIndex, 0)
                    }
                    content.isBold -> {
                        spannableString.setSpan(StyleSpan(Typeface.BOLD), content.startIndex, content.endIndex, 0)
                    }
                    content.isItalic -> {
                        spannableString.setSpan(StyleSpan(Typeface.ITALIC), content.startIndex, content.endIndex, 0)
                    }
                    content.isItalic -> {
                        spannableString.setSpan(StyleSpan(Typeface.ITALIC), content.startIndex, content.endIndex, 0)
                    }
                    content.isStroke -> {
                        spannableString.setSpan(StrikethroughSpan(), content.startIndex, content.endIndex, 0)
                    }
                }
            }

            return spannableString
        }
    }

    data class PatternSpanDescriptor(val startIndex: Int, val endIndex : Int, val content: String, val label: String? = null, val isLink: Boolean = true, val isBold: Boolean = false, val isItalic: Boolean = false, val isStroke: Boolean = false)

    data class PatternDescriptor(val content: String, val label: String? = null, val isLink: Boolean = true, val isBold: Boolean = false, val isItalic: Boolean = false, val isStroke: Boolean = false){
        fun toTag() : String{
            return when {
                isLink -> {
                    var swapData = "<$content"
                    label?.run {
                        swapData += "|$label"
                    }
                    swapData + ">"
                }
                isBold -> "*$content*"
                isItalic -> "_${content}_"
                isStroke -> "~$content~"
                else -> content
            }
        }

        fun getLabelToDisplay() : String {
            return label ?: content
        }
    }
}

