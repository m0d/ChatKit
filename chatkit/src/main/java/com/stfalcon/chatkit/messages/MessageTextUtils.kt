package com.stfalcon.chatkit.messages

import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import java.util.regex.Pattern

/**
 * @author Grzegorz Pawełczuk <grzegorz.pawelczuk@ftlearning.com>
 * @author Mikołaj Kowal <mikolaj.kowal@nftlearning.com>
 * Nikkei FT Learning Limited
 * @since 04.01.2018
 * //2-12-2018 - Mikołaj Kowal - added support for nested expressions
 */



class MessageTextUtils {

    companion object {
        fun getTextPatterns(text: String): MutableList<PatternDescriptor> {
            var list: MutableList<PatternDescriptor> = mutableListOf()
            val pattern = Pattern.compile("<(.*?)>|\\*(.*?)\\*|_(.*?)_|~(.*?)~")
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                var group = matcher.group()
                var isLink = false
                var isBold = false
                var isItalic = false
                var isStroke = false
                var surrounding = MarkDown.NONE
                when {
                    group.startsWith("<") -> {
                        isLink = true
                        surrounding = MarkDown.LINK
                    }
                    group.startsWith("*") -> {
                        isBold = true
                        surrounding = MarkDown.BOLD
                    }
                    group.startsWith("_") -> {
                        isItalic = true
                        surrounding = MarkDown.ITALIC
                    }
                    group.startsWith("~") -> {
                        isStroke = true
                        surrounding = MarkDown.STROKE
                    }
                }
                group = group.substring(1, group.length - 1)
                if(findNextMarkDown(0,group)!= -1) {
                    var reqList = getTextPatterns(group)
                    if(reqList.size > 0)
                        reqList.forEach{unit: PatternDescriptor ->
                            when{
                                isBold == true -> unit.isBold = true
                                isItalic == true -> unit.isItalic = true
                                isStroke == true -> unit.isStroke = true
                                isLink == true -> unit.isLink = true
                            }
                            list.add(unit)
                        }
                }
                var url: PatternDescriptor? = null
                if (group.contains("|") && isLink) {
                    val split = group.split("|")
                    if (split.isNotEmpty()) {
                        url = PatternDescriptor(removeMarkDowns(split[0]), removeMarkDowns(split[1]), true, isBold, isItalic, isStroke, surrounding)
                    }
                } else {
                    url = PatternDescriptor(removeMarkDowns(group), null, isLink, isBold, isItalic, isStroke, surrounding)
                }
                url?.run {
                    list?.add(this)
                }
            }
            return list
        }

        fun findNextMarkDown(currentIndex: Int, text: String) : Int
        {
            var closestIndex = Int.MAX_VALUE
            val patern = "*~<>_"
            patern.forEach { c: Char ->
                var thisIndex = text.indexOf(c,currentIndex)
                if(thisIndex != -1 && thisIndex < closestIndex)
                    closestIndex = thisIndex
            }
            if (closestIndex == Int.MAX_VALUE)
                return  -1
            return closestIndex
        }

        fun removeMarkDowns(markDownText: String): String
        {
            var patern = "*~<>_"
            var toReturn = String(markDownText.toCharArray())
            for (i in 0 until patern.length){
                var thisSign = patern[i].toString()
                toReturn = toReturn.replace(thisSign,"")
            }
            return toReturn
        }

        fun howManyLevelsIn(url: PatternDescriptor): Int
        {
            var i = -1
            if(url.isBold)
                i+=1
            if(url.isItalic)
                i+=1
            if(url.isLink)
                i+=1
            if(url.isStroke)
                i+=1
            return i
        }

        fun transform(text: String, urls: MutableList<PatternDescriptor>, color: Int) : SpannableString {
            val descriptors : MutableList<PatternSpanDescriptor> = mutableListOf()
            var urlText: String
            var textToCheck = text
            urls.forEach{ url ->
                urlText = url.toTag()
                //w{"   SEARCH: $urlText in $textToCheck"}
                if(textToCheck.indexOf(urlText) != -1 ){
                    val parts  = textToCheck.split(urlText)
                    //w{"      PARTS: $parts"}
                    if(parts.isNotEmpty()){
                        textToCheck = parts[0] + url.getLabelToDisplay() + parts.drop(1).joinToString(separator = urlText)
                        descriptors.add(PatternSpanDescriptor(
                                parts[0].length - howManyLevelsIn(url),
                                parts[0].length+url.getLabelToDisplay().length - howManyLevelsIn(url),
                                url.content,
                                url.label,
                                url.isLink,
                                url.isBold,
                                url.isItalic,
                                url.isStroke
                            )
                        )
                    }
                }
            }

            val spannableString = SpannableString(textToCheck)

            descriptors.forEach{ content ->

                if(content.isLink) {
                    spannableString.setSpan(URLSpan(content.content), content.startIndex, content.endIndex, 0)
                    spannableString.setSpan(ForegroundColorSpan(color), content.startIndex, content.endIndex, 0)
                }
                if(content.isBold) {
                    spannableString.setSpan(StyleSpan(Typeface.BOLD), content.startIndex, content.endIndex, 0)
                }
                if(content.isItalic) {
                    spannableString.setSpan(StyleSpan(Typeface.ITALIC), content.startIndex, content.endIndex, 0)
                }
                if(content.isItalic) {
                    spannableString.setSpan(StyleSpan(Typeface.ITALIC), content.startIndex, content.endIndex, 0)
                }
                if(content.isStroke) {
                    spannableString.setSpan(StrikethroughSpan(), content.startIndex, content.endIndex, 0)
                }
            }

            return spannableString
        }
    }

    data class PatternSpanDescriptor(val startIndex: Int, val endIndex : Int, val content: String, val label: String? = null, val isLink: Boolean = true, val isBold: Boolean = false, val isItalic: Boolean = false, val isStroke: Boolean = false)

    data class PatternDescriptor(var content: String, val label: String? = null, var isLink: Boolean = true, var isBold: Boolean = false, var isItalic: Boolean = false, var isStroke: Boolean = false, var surrounding: MarkDown = MarkDown.NONE, var beginIndex: Int = 0, var endIndex: Int = 0){
        fun toTag() : String{
            if(content.contains("|"))
                content = content.split("|")[1]
            return when {
                surrounding == MarkDown.LINK -> {
                    var swapData = "<$content"
                    label?.run {
                        swapData += "|$label"
                    }
                    swapData + ">"
                }
                surrounding == MarkDown.BOLD -> "*$content*"
                surrounding == MarkDown.ITALIC -> "_${content}_"
                surrounding == MarkDown.STROKE -> "~$content~"
                else -> content
            }
        }

        fun getLabelToDisplay() : String {
            return label ?: content
        }
    }

    enum class MarkDown{
        BOLD, ITALIC, STROKE, LINK, NONE
    }
}

