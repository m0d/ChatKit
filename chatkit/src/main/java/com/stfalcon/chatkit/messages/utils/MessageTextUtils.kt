package com.stfalcon.chatkit.messages.utils

import android.support.annotation.ColorInt
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.widget.TextView
import com.github.ajalt.timberkt.w
import com.stfalcon.chatkit.messages.markdown.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.regex.Pattern

/**
 * @author Grzegorz Pawełczuk <grzegorz.pawelczuk@ftlearning.com>
 * Nikkei FT Learning Limited
 * @since 04.01.2018
 */

class MessageTextUtils {

    companion object {
        private val LINE_DELIMITER by lazy { System.lineSeparator() }
        private val SUPPORTED_MARKDOWNS: List<MarkDown>   by lazy { listOf(Bold, Italic, Strike, Link, Quote, Bullet, Number) }
        private val SINGLE_LINE_MARKDOWNS: List<MarkDown> by lazy { SUPPORTED_MARKDOWNS.filter { it.isFullLine() } }
        private val SUBSEQUENT_MARKDOWNS: List<MarkDown>  by lazy { SUPPORTED_MARKDOWNS.filter { !it.isFullLine() } }

        private const val INSET_WIDTH = 32

        private val mEntityMap: Map<String, String> = mapOf(
                "&gt;" to ">",
                "&lt;" to "<",
                "&amp;" to "&",
                "\u2029" to "\n"
        )

        private fun fromEntities(text: String): String {
            val x: MutableList<MarkDown> = mutableListOf(Bold, Quote)
            var data = text
            mEntityMap.forEach {
                data = data.replace(it.key, it.value)
            }
            return data
        }

        fun applyTextTransformations(view: TextView, rawText: String, @ColorInt linkColor: Int) {
            Single.fromCallable {
                val text = EmojiTextUtils.transform(fromEntities(rawText))
                MessageTextUtils.transform(text, linkColor)
            }
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ text ->
                        view.text = text
                        view.movementMethod = LinkMovementMethod.getInstance()
                    }, { e -> w { "${e.message}" } })
        }

        fun transform(text: String, @ColorInt linkColor: Int): CharSequence {
            val data = fromEntities(text)
            separator()
            log(data, "input")
            separator()
            return toSpannableText(fromEntities(text), linkColor)
        }

        private fun separator() = log("----------------------------------------------------------")
        private fun paragraph() = log("-------------")

        private fun log(text: String?, suffix: String? = null) {
            var logData = ""
            suffix?.run {
                logData += "$suffix: "
            }
            logData += text
            System.out.println(logData)
        }

        private fun toSpannableText(text: String, @ColorInt linkColor: Int): CharSequence {
            val textLines = text.split(LINE_DELIMITER)
            val output: Array<CharSequence?> = arrayOfNulls(textLines.size)

            log(textLines.toString(), "lines")
            separator()

            textLines.forEachIndexed { index, line ->
                separator()
                log(line, "process index $index")
                output[index] = getLineSpan(line)
            }

            return ""//TextUtils.concat(*output)
        }

        private fun getLineSpan(singleLine: String): SpannableString {
            val spans: MutableList<MarkDownPattern> = mutableListOf()
            val sequentSpans: MutableList<MarkDownPattern> = mutableListOf()
            val sequentSpansOrder: MutableList<MarkDownPatternIndexer> = mutableListOf()
            val lineOutput: String = singleLine.stripMarkdown(SUPPORTED_MARKDOWNS)
            var output: String = singleLine

            SINGLE_LINE_MARKDOWNS.forEach {
                val matches = it.matches(singleLine)
                if (matches) {
                    output = it.getLabel(output)
                    spans.add(
                            MarkDownPattern(
                                    singleLine,
                                    0,
                                    lineOutput.length - 1,
                                    output,
                                    0,
                                    output.length - 1,
                                    it
                            )
                    )
                    return@forEach // there is only one single line markdown
                }
            }

            SUBSEQUENT_MARKDOWNS.forEach {
                var matches = it.find(output)

                while (matches != null) {
                    log("$it MATCHES ${matches.value} ${matches.range}")
                    sequentSpansOrder.add(
                            MarkDownPatternIndexer(matches.range.first, it)
                    )
                    matches = matches.next()
                }
            }

            val orderedMarkdowns: MutableList<MarkDownPatternIndexer> = sequentSpansOrder.sortedWith(compareBy { it.startIndex }).toMutableList()
            log("$orderedMarkdowns")

            while(orderedMarkdowns.size > 0){
                val pattern = orderedMarkdowns.removeAt(0)
                val find = pattern.markDown.find(output)
                find?.run{
                    paragraph()
                    val newValue = pattern.markDown.getLabel(this.value)
                    log(this.value + " -> "+newValue, "replace in output")
                    output = output.replaceFirst(this.value, newValue)
                    val strippedText = newValue.stripMarkdown(SUPPORTED_MARKDOWNS)
                    sequentSpans.add(
                            MarkDownPattern(
                                    value,
                                    range.first,
                                    range.last,
                                    strippedText,
                                    range.first,
                                    range.first + strippedText.length,
                                    pattern.markDown
                            )
                    )
                }
            }

            log("", "all spans")
            sequentSpans.forEach{  pattern ->
                with(pattern) {
                    log("$afterText $afterStartIndex..$afterEndIndex - $markDown")
                }
            }
            separator()
            log(output,"output")

            return SpannableString(lineOutput)
        }

        private fun String.stripMarkdown(markDowns: List<MarkDown>): String {
            separator()
            var value = this
            var group: String
            markDowns.forEach {
                val matcher = Pattern.compile(it.getRegex()).matcher(value)
                while (matcher.find()) {
                    group = matcher.group()
                    value = value.replace(group, it.getLabel(group))
                    log(value, "after value")
                    paragraph()
                }
            }
            return value
        }

        data class MarkDownPatternIndexer(val startIndex: Int, val markDown: MarkDown)

        data class MarkDownPattern(
                val beforeText: String,
                val beforeStartIndex: Int,
                val beforeEndIndex: Int,
                val afterText: String,
                val afterStartIndex: Int,
                val afterEndIndex: Int,
                val markDown: MarkDown
        )
    }

/*
        fun getTextPatterns(text: String): MutableList<PatternDescriptor> {
            val list: MutableList<PatternDescriptor> = mutableListOf()
            val pattern = Pattern.compile("(>)(.+)|(-)(.+)|(\\d+)\\..*|<([a-zA-Z]{2,10}:(.*?))>|(?<!(([\\p{Alnum}])|\\*))\\*([^*\\n]+)\\*(?!(([\\p{Alnum}])|\\*))|(?<!(([\\p{Alnum}])|_))_([^_\\n]+)_(?!(([\\p{Alnum}])|_))|(?<!(([\\p{Alnum}])|~))~([^~\\n]+)~(?!(([\\p{Alnum}])|~))")
            val matcher = pattern.matcher(text)

            val numberedRegexPattern = "^(\\d+)\\..*"
            val numberedPattern = Pattern.compile(numberedRegexPattern)


            while (matcher.find()) {
                var group = matcher.group()
                val numberedMatcher = numberedPattern.matcher(group)
                var isLink = false
                var isBold = false
                var isItalic = false
                var isStroke = false
                var isQuote = false
                var isBullet = false
                var isNumbered = false
                var surrounding = MarkDownType.NONE

                when {
                    group.startsWith(">") -> {
                        isQuote = true
                        surrounding = MarkDownType.QUOTE
                    }
                    group.startsWith("-") -> {
                        isBullet = true
                        surrounding = MarkDownType.BULLET
                    }
                    numberedMatcher.find() -> {
                        w{"isNumbered"}
                        isNumbered = true
                        surrounding = MarkDownType.NUMBERED
                    }
                    group.startsWith(">") -> {
                        isQuote = true
                        surrounding = MarkDownType.QUOTE
                    }
                    group.startsWith("<") -> {
                        isLink = true
                        surrounding = MarkDownType.LINK
                    }
                    group.startsWith("*") -> {
                        isBold = true
                        surrounding = MarkDownType.BOLD
                    }
                    group.startsWith("_") -> {
                        isItalic = true
                        surrounding = MarkDownType.ITALIC
                    }
                    group.startsWith("~") -> {
                        isStroke = true
                        surrounding = MarkDownType.STROKE
                    }
                }

                val numberLabel = if(isNumbered) getNumber(group) else ""

                group = group.substring(
                        if(!isNumbered) 1 else numberLabel.length+1,
                        group.length - if (!isQuote && !isBullet && !isNumbered) 1 else 0
                )

                w{"group $group"}

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
                                isBullet -> unit.isBullet = true
                                isNumbered -> unit.isNumbered = true
                            }
                            list.add(unit)
                        }
                    }
                }
                var url: PatternDescriptor? = null
                if (group.contains("|") && isLink) {
                    val split = group.split("|")
                    if (split.isNotEmpty()) {
                        url = PatternDescriptor(removeMarkDowns(split[0]), removeMarkDowns(split[1]), true, isBold, isItalic, isStroke, isQuote, isBullet, isNumbered, surrounding = surrounding)
                    }
                } else {
                    w{"----------"}
                    val content = removeMarkDowns(group)
                    w{"content $content"}
                    url = PatternDescriptor(content, if(isNumbered) numberLabel else null, isLink, isBold, isItalic, isStroke, isQuote, isBullet, isNumbered, surrounding = surrounding)
                }
                url?.run {
                    w{this.toString()}
                    list.add(this)
                }
            }
            return list
        }

        private fun getNumber(content: String): String {
            val split = content.split(".")
            return if(split.isNotEmpty()) split[0] else ""
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

        private fun isMarkDown(text: String): Boolean{
            return when{
                text.startsWith("*") && text.endsWith("*") -> true
                text.startsWith("_") && text.endsWith("_") -> true
                text.startsWith("~") && text.endsWith("~") -> true
                text.startsWith("<") && text.endsWith(">") -> true
                else -> false
            }
        }

        private fun removeMarkDowns(markDownText: String): String {
            val pattern = Pattern.compile("<([a-zA-Z]{2,10}:(.*?))>|(?<!(([\\p{Alnum}])|\\*))\\*([^*\\n]+)\\*(?!(([\\p{Alnum}])|\\*))|(?<!(([\\p{Alnum}])|_))_([^_\\n]+)_(?!(([\\p{Alnum}])|_))|(?<!(([\\p{Alnum}])|~))~([^~\\n]+)~(?!(([\\p{Alnum}])|~))")
            val matcher = pattern.matcher(markDownText)
            var text: String
            var toReturn = markDownText

            if (toReturn.startsWith(">")) {
                toReturn = toReturn.substring(">".length, toReturn.length)
            }

            if (toReturn.startsWith("-")) {
                toReturn = toReturn.substring("-".length, toReturn.length)
            }

            while (matcher.find()) {
                text = matcher.group()
                toReturn = toReturn.replace(text,text.substring(1, text.length - 1))
                while(isMarkDown(toReturn)){
                    toReturn = removeMarkDowns(toReturn)
                }
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
                i += 1
            }
            if (url.isBullet) {
                i += 1
            }
            if (url.isNumbered) {
                i += 2
            }
            return i
        }

        private fun calculateOffset(urls: MutableList<PatternDescriptor>): MutableList<PatternDescriptor> {
            var thisUrl: PatternDescriptor
            for (i in 0 until urls.size) {
                thisUrl = urls[i]
                if (thisUrl.isLink && thisUrl.surrounding != MarkDownType.LINK) {
                    var j = 0
                    var nextUrl: PatternDescriptor
                    while (i + j < urls.size) {
                        nextUrl = urls[i + j]
                        if (nextUrl.surrounding == MarkDownType.LINK) {
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
                        d{"$url"}
                        d{"x: $textToCheck"}
                        textToCheck = parts[0] + url.getLabelToDisplay() + parts.drop(1).joinToString(separator = urlText)
                        d{"y: $textToCheck"}
                        w{"parts ${parts[0]}"}
                        var howManyLevelsIn = howManyLevelsIn(url)
                        with(url) {
                            if(isNumbered  && !isBold && !isItalic && !isStroke && !isQuote && !isBullet){
                                howManyLevelsIn -= 1
                            }
                        }
                        w{"start ${parts[0].length} - $howManyLevelsIn - ${url.offset}"}
                        w{"end ${parts[0].length} + ${url.getLabelToDisplay().length} - $howManyLevelsIn - ${url.offset}"}

                        val startIndex = parts[0].length - howManyLevelsIn - url.offset

                        val element = PatternSpanDescriptor(
                                startIndex,
                                parts[0].length + url.getLabelToDisplay().length - howManyLevelsIn - url.offset,
                                url.content,
                                url.label,
                                url.isLink,
                                url.isBold,
                                url.isItalic,
                                url.isStroke,
                                url.isQuote,
                                url.isBullet,
                                url.isNumbered
                        )
                        descriptors.add(element)
                    }
                }
            }

            val spannableString = SpannableString(textToCheck)

            w{"$descriptors"}

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
                    spannableString.setSpan(LeadingMarginSpan.Standard(INSET_WIDTH, INSET_WIDTH), content.startIndex, content.endIndex, 0)
                }
                if (content.isBullet) {
                    spannableString.setSpan(BulletSpanWithRadius(INSET_WIDTH / 6, INSET_WIDTH), content.startIndex, content.endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                if (content.isNumbered) {
                    spannableString.setSpan(BulletSpanWithRadius(INSET_WIDTH / 6, INSET_WIDTH), content.startIndex, content.endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
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
                                     val isQuote: Boolean = false,
                                     val isBullet: Boolean = false,
                                     val isNumbered: Boolean = false)

    data class PatternDescriptor(var content: String, val label: String? = null,
                                 var isLink: Boolean = true, var isBold: Boolean = false,
                                 var isItalic: Boolean = false, var isStroke: Boolean = false,
                                 var isQuote: Boolean = false,
                                 var isBullet: Boolean = false,
                                 var isNumbered: Boolean = false, var offset: Int = 0, @MarkDownType.MarkDowns var surrounding: Long = MarkDownType.NONE, var beginIndex: Int = 0, var endIndex: Int = 0) {
        fun toTag(): String {
            if (content.contains("|")) {
                content = content.split("|")[1]
            }
            return when (surrounding) {
                MarkDownType.LINK -> {
                    var swapData = "<$content"
                    label?.run {
                        swapData += "|$label"
                    }
                    "$swapData>"
                }
                MarkDownType.BOLD -> "*$content*"
                MarkDownType.ITALIC -> "_${content}_"
                MarkDownType.STROKE -> "~$content~"
                MarkDownType.QUOTE -> ">$content"
                MarkDownType.BULLET -> "-$content"
                MarkDownType.NUMBERED -> "$label.$content"
                else -> content
            }
        }

        fun getLabelToDisplay(): String {
            return if(isNumbered) content else label ?: content
        }
    }*/
}


