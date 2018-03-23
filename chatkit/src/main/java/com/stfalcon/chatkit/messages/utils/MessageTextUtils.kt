package com.stfalcon.chatkit.messages.utils

import android.graphics.Typeface
import android.support.annotation.ColorInt
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.widget.TextView
import com.github.ajalt.timberkt.w
import com.stfalcon.chatkit.commons.events.CustomUrlSpan
import com.stfalcon.chatkit.messages.MarkDown
import com.stfalcon.chatkit.utils.NonbreakingSpan
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.regex.Pattern

/**
 * @author Grzegorz Pawełczuk <grzegorz.pawelczuk@ftlearning.com>
 * @author Mikołaj Kowal <mikolaj.kowal@nftlearning.com>
 * Nikkei FT Learning Limited
 * @since 04.01.2018
 * 13-2-2018 - Mikołaj Kowal - added support for nested expressions
 * 02-03-2018 - Grzegorz Pawełczuk - Quote support
 */


class MessageTextUtils {

    companion object {

        private const val QUOTE_INSET = 32

        fun applyTextTransformations(view: TextView, rawText: String, @ColorInt linkColor: Int) {
            Flowable.fromCallable {
                val text = EmojiTextUtils.transform(rawText)
                view.text = MessageTextUtils.transform(text, linkColor)
                view.movementMethod = LinkMovementMethod.getInstance()
            }
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({},{e -> w{"${e.message}"}})
        }

        private fun transform(text: String, @ColorInt linkColor: Int): SpannableString {
            return MessageTextUtils.transform(text, getTextPatterns(text), linkColor)
        }

        fun getTextPatterns(text: String): MutableList<PatternDescriptor> {
            val list: MutableList<PatternDescriptor> = mutableListOf()
            val pattern = Pattern.compile("(&gt;)(.*)|<(.*?)>|(?<!(([\\p{Alnum}])|\\*))\\*([^*\\n]+)\\*(?!(([\\p{Alnum}])|\\*))|_(.*?)_|~(.*?)~")
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                var group = matcher.group()
                var isLink = false
                var isBold = false
                var isItalic = false
                var isStroke = false
                var isQuote = false
                var surrounding = MarkDown.NONE
                when {
                    group.startsWith("&gt;") -> {
                        isQuote = true
                        surrounding = MarkDown.QUOTE
                    }
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
                group = group.substring(
                        if (isQuote) 4 else 1,
                        group.length - (if (!isQuote) 1 else 0)
                )
                if (findNextMarkDown(0, group) != -1) {
                    val reqList = getTextPatterns(group)
                    if (reqList.size > 0) {
                        reqList.forEach { unit: PatternDescriptor ->
                            when {
                                isBold -> unit.isBold = true
                                isItalic -> unit.isItalic = true
                                isStroke -> unit.isStroke = true
                                isLink -> unit.isLink = true
                                isQuote -> unit.isQuote = true
                            }
                            list.add(unit)
                        }
                    }
                }
                var url: PatternDescriptor? = null
                if (group.contains("|") && isLink) {
                    val split = group.split("|")
                    if (split.isNotEmpty()) {
                        url = PatternDescriptor(removeMarkDowns(split[0]), removeMarkDowns(split[1]), true, isBold, isItalic, isStroke, isQuote, surrounding = surrounding)
                    }
                } else {
                    url = PatternDescriptor(removeMarkDowns(group), null, isLink, isBold, isItalic, isStroke, isQuote, surrounding = surrounding)
                }
                url?.run {
                    list.add(this)
                }
            }
            return list
        }

        private fun findNextMarkDown(currentIndex: Int, text: String): Int {
            var closestIndex = Int.MAX_VALUE
            val pattern = "*~<>_"
            var thisIndex: Int
            pattern.forEach { tag ->
                thisIndex = text.indexOf(tag, currentIndex)
                if (thisIndex != -1 && thisIndex < closestIndex) {
                    closestIndex = thisIndex
                }
            }
            if (closestIndex == Int.MAX_VALUE) {
                return -1
            }
            return closestIndex
        }

        private fun removeMarkDowns(markDownText: String): String {
            val pattern = Pattern.compile("<(.*?)>|(?<!(([\\p{Alnum}])|\\*))\\*([^*\\n]+)\\*(?!(([\\p{Alnum}])|\\*))|_(.*?)_|~(.*?)~")
            val matcher = pattern.matcher(markDownText)
            var text: String
            var toReturn = markDownText
            while (matcher.find()) {
                text = matcher.group()
                toReturn = text.substring(1, text.length - 1)
            }

            if (toReturn.startsWith("&gt;")) {
                toReturn = toReturn.substring("&gt;".length, toReturn.length)
            }

            return toReturn
        }

        private fun howManyLevelsIn(url: PatternDescriptor): Int {
            var i = -1
            if (url.isBold) {
                i += 1
            }
            if (url.isItalic) {
                i += 1
            }
            if (url.isLink) {
                i += 1
            }
            if (url.isStroke) {
                i += 1
            }
            if (url.isQuote) {
                i += 4
            }
            return i
        }

        private fun calculateOffset(urls: MutableList<PatternDescriptor>): MutableList<PatternDescriptor> {
            var thisUrl: PatternDescriptor
            for (i in 0 until urls.size) {
                thisUrl = urls[i]
                if (thisUrl.isLink && thisUrl.surrounding != MarkDown.LINK) {
                    var j = 0
                    var nextUrl: PatternDescriptor
                    while (i + j < urls.size) {
                        nextUrl = urls[i + j]
                        if (nextUrl.surrounding == MarkDown.LINK) {
                            thisUrl.offset = nextUrl.content.length + 1
                            break
                        }
                        j++
                    }
                }
                urls[i] = thisUrl

            }
            return urls
        }

        private fun transform(text: String, urls: MutableList<PatternDescriptor>, color: Int): SpannableString {
            val checkedUrls = calculateOffset(urls)
            val descriptors: MutableList<PatternSpanDescriptor> = mutableListOf()
            var urlText: String
            var textToCheck = text
            checkedUrls.forEach { url ->
                urlText = url.toTag()
                if (textToCheck.indexOf(urlText) != -1) {
                    val parts = textToCheck.split(urlText)
                    if (parts.isNotEmpty()) {
                        textToCheck = parts[0] + url.getLabelToDisplay() + parts.drop(1).joinToString(separator = urlText)
                        val element = PatternSpanDescriptor(
                                parts[0].length - howManyLevelsIn(url) - url.offset,
                                parts[0].length + url.getLabelToDisplay().length - howManyLevelsIn(url) - url.offset,
                                url.content,
                                url.label,
                                url.isLink,
                                url.isBold,
                                url.isItalic,
                                url.isStroke,
                                url.isQuote
                        )
                        descriptors.add(element)
                    }
                }
            }

            val spannableString = SpannableString(textToCheck)

            descriptors.forEach { content ->

                if (content.isLink) {
                    if (content.content.startsWith("https://appear.in")) {
                        spannableString.setSpan(CustomUrlSpan(content.content), content.startIndex, content.endIndex, 0)
                    } else {
                        spannableString.setSpan(URLSpan(content.content), content.startIndex, content.endIndex, 0)
                    }
                    spannableString.setSpan(ForegroundColorSpan(color), content.startIndex, content.endIndex, 0)
                    spannableString.setSpan(ForegroundColorSpan(color), content.startIndex, content.endIndex, 0)

                    spannableString.getSpans(0, textToCheck.length, URLSpan::class.java)
                }
                if (content.isBold) {
                    spannableString.setSpan(StyleSpan(Typeface.BOLD), content.startIndex, content.endIndex, 0)
                }
                if (content.isItalic) {
                    spannableString.setSpan(StyleSpan(Typeface.ITALIC), content.startIndex, content.endIndex, 0)
                }
                if (content.isStroke) {
                    spannableString.setSpan(StrikethroughSpan(), content.startIndex, content.endIndex, 0)
                }
                if (content.isQuote) {
                    spannableString.setSpan(LeadingMarginSpan.Standard(QUOTE_INSET, QUOTE_INSET), content.startIndex, content.endIndex, 0)
                }
            }

            return transformCommandLike(spannableString)
        }

        private fun transformCommandLike(spannableString: SpannableString): SpannableString {
            val text = spannableString.toString()
            val pattern = Pattern.compile("(/(.*?)\\s)|(/(.*+))")

            val matcher = pattern.matcher(text)
            var group: String
            while (matcher.find()) {
                group = matcher.group()
                if (!group.startsWith("//")) { // its url
                    val indexOf = text.indexOf(group)
                    spannableString.setSpan(NonbreakingSpan(), indexOf, indexOf + group.length, 0)
                }
            }
            return spannableString
        }
    }

    data class PatternSpanDescriptor(val startIndex: Int, val endIndex: Int, val content: String,
                                     val label: String? = null, val isLink: Boolean = true,
                                     val isBold: Boolean = false, val isItalic: Boolean = false,
                                     val isStroke: Boolean = false,
                                     val isQuote: Boolean = false)

    data class PatternDescriptor(var content: String, val label: String? = null,
                                 var isLink: Boolean = true, var isBold: Boolean = false,
                                 var isItalic: Boolean = false, var isStroke: Boolean = false,
                                 var isQuote: Boolean = false,
                                 var beginIndex: Int = 0, var endIndex: Int = 0, var offset: Int = 0, @MarkDown.MarkDowns var surrounding: Long = MarkDown.NONE) {
        fun toTag(): String {
            if (content.contains("|")) {
                content = content.split("|")[1]
            }
            return when (surrounding) {
                MarkDown.LINK -> {
                    var swapData = "<$content"
                    label?.run {
                        swapData += "|$label"
                    }
                    swapData + ">"
                }
                MarkDown.BOLD -> "*$content*"
                MarkDown.ITALIC -> "_${content}_"
                MarkDown.STROKE -> "~$content~"
                MarkDown.QUOTE -> "&gt;$content"
                else -> content
            }
        }

        fun getLabelToDisplay(): String {
            return label ?: content
        }
    }
}
