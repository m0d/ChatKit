package com.stfalcon.chatkit.messages.utils

import android.graphics.Typeface
import android.support.annotation.ColorInt
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.widget.TextView
import com.github.ajalt.timberkt.w
import com.stfalcon.chatkit.commons.spans.BulletSpanWithRadius
import com.stfalcon.chatkit.commons.spans.CustomUrlSpan
import com.stfalcon.chatkit.commons.spans.NumberedSpan
import com.stfalcon.chatkit.commons.spans.QuoteSpan
import com.stfalcon.chatkit.messages.markdown.*
import com.stfalcon.chatkit.messages.markdown.Number
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.regex.Pattern

/**
 * @author Grzegorz Pawełczuk <grzegorz.pawelczuk@ftlearning.com>
 * Nikkei FT Learning Limited
 * @since 04.04.2018
 */

class MessageTextUtils {

    companion object {
        const val DEBUG_LOG = false

        internal val LINE_DELIMITER by lazy { System.lineSeparator() }
        val SUPPORTED_MARKDOWNS: List<MarkDown>   by lazy { listOf(Bold, Italic, Strike, Link, Quote, Bullet, Number) }
        private val SINGLE_LINE_MARKDOWNS: List<MarkDown> by lazy { SUPPORTED_MARKDOWNS.filter { it.isFullLine() } }
        private val SUBSEQUENT_MARKDOWNS: List<MarkDown>  by lazy { SUPPORTED_MARKDOWNS.filter { !it.isFullLine() } }

        private const val INSET_WIDTH = 90

        private fun separator() = log("----------------------------------------------------------")
        private fun paragraph() = log("-------------")

        private val mEntityMap: Map<String, String> = mapOf(
                "&gt;" to ">",
                "&lt;" to "<",
                "&amp;" to "&",
                "\u2029" to "\n"
        )

        fun fromEntities(text: String): String {
            var data = text
            mEntityMap.forEach {
                data = data.replace(it.key, it.value)
            }
            return data
        }

        fun applyTextTransformations(view: TextView, rawText: String, @ColorInt notifyColor: Int) {
            Single.fromCallable {
                val text = EmojiTextUtils.transform(fromEntities(rawText))
                MessageTextUtils.transform(text, notifyColor)
            }
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ text ->
                        view.text = text
                        view.movementMethod = LinkMovementMethod.getInstance()
                    }, { e -> w { "${e.message}" } })
        }

        private fun transform(text: String, @ColorInt notifyColor: Int): CharSequence {
            val data = fromEntities(text)
            separator()
            log(data, "input")
            separator()
            val lines = toLinePatterns(fromEntities(text))
            val output: Array<CharSequence?> = arrayOfNulls(lines.size)
            lines.forEachIndexed { index, line ->
                output[index] = addSpannables(line.text, line.patterns, notifyColor)
            }
            return TextUtils.concat(*output)
        }

        @Suppress("ConstantConditionIf")
        private fun log(text: String?, suffix: String? = null) {
            var logData = ""
            suffix?.run {
                logData += "$suffix: "
            }
            logData += text
            if(DEBUG_LOG) {
                System.out.println(logData)
            }
        }

        fun toLinePatterns(text: String): MutableList<SingleLinePattern> {
            val textLines = text.split(LINE_DELIMITER)
            var extraChar: String
            val singleLines: MutableList<SingleLinePattern> = mutableListOf()

            log(textLines.toString(), "lines")
            separator()

            textLines.forEachIndexed { index, line ->
                separator()
                log(line, "process index $index")
                extraChar = if (index < textLines.size - 1) {
                    LINE_DELIMITER
                } else {
                    ""
                }
                val lineSpan = getLineSpan(line)
                singleLines.add(SingleLinePattern(lineSpan.first + extraChar, lineSpan.second))
            }

            return singleLines
        }

        private fun addSpannables(text: String, patterns: MutableList<MarkDownPattern>, notifyColor: Int): CharSequence {
            val output = SpannableString(text)
            patterns.forEach {
                with(output) {
                    when (it.markDown) {
                        Bold -> setSpan(StyleSpan(Typeface.BOLD), it.afterStartIndex, it.afterEndIndex, 0)
                        Italic -> setSpan(StyleSpan(Typeface.ITALIC), it.afterStartIndex, it.afterEndIndex, 0)
                        Strike -> setSpan(StrikethroughSpan(), it.afterStartIndex, it.afterEndIndex, 0)
                        Quote -> setSpan(QuoteSpan(INSET_WIDTH * 2 / 3, notifyColor), it.afterStartIndex, it.afterEndIndex, 0)
                        Bullet -> setSpan(BulletSpanWithRadius(INSET_WIDTH), it.afterStartIndex, it.afterEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        Number -> {
                            val mRowNumber = it.markDown.getAttribute(it.beforeText)
                            if (mRowNumber.isNotEmpty()) {
                                setSpan(NumberedSpan("$mRowNumber.", INSET_WIDTH), it.afterStartIndex, it.afterEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                        }
                        Link -> {
                            setSpan(CustomUrlSpan(it.afterText), it.afterStartIndex, it.afterEndIndex, 0)
                            setSpan(ForegroundColorSpan(notifyColor), it.afterStartIndex, it.afterEndIndex, 0)
                        }
                    }
                }
            }
            return output
        }

        fun getLineSpan(singleLine: String): Pair<String, MutableList<MarkDownPattern>> {
            val spans: MutableList<MarkDownPattern> = mutableListOf()
            val sequentSpans: MutableList<MarkDownPattern> = mutableListOf()
            val sequentSpansOrder: MutableList<MarkDownPatternIndexer> = mutableListOf()
            val lineOutput: String = singleLine.stripMarkdown(SUPPORTED_MARKDOWNS)
            var output: String = singleLine

            SINGLE_LINE_MARKDOWNS.forEach {
                val matches = it.matches(singleLine)
                if (matches) {
                    val memoryText = output
                    output = it.getLabel(output)

                    val stripMarkdown = output.stripMarkdown(SUPPORTED_MARKDOWNS)
                    spans.add(MarkDownPattern(stripMarkdown, 0, stripMarkdown.length, it, memoryText))
                    return@forEach // there is only one single line markdown
                }
            }

            SUBSEQUENT_MARKDOWNS.forEach {
                var matches = it.find(output)
                while (matches != null) {
                    log("$it MATCHES ${matches.value} ${matches.range}")
                    sequentSpansOrder.add(MarkDownPatternIndexer(matches.range.first, it))
                    matches = matches.next()
                }
            }

            val orderedMarkdowns: MutableList<MarkDownPatternIndexer> = sequentSpansOrder.sortedWith(compareBy { it.startIndex }).toMutableList()

            log("$orderedMarkdowns", "ordered")
            while (orderedMarkdowns.size > 0) {
                val pattern = orderedMarkdowns.removeAt(0)
                val find = pattern.markDown.find(output)
                find?.run {
                    paragraph()
                    val newValue = pattern.markDown.getLabel(this.value)
                    log(this.value + " -> " + newValue, "replace in output")
                    output = output.replaceFirst(this.value, newValue)
                    val strippedText = newValue.stripMarkdown(SUPPORTED_MARKDOWNS)
                    sequentSpans.add(
                            MarkDownPattern(strippedText, range.first, range.first + strippedText.length, pattern.markDown, this.value)
                    )
                }
            }

            log("", "all spans")
            spans.addAll(sequentSpans)
            spans.forEach { pattern ->
                with(pattern) {
                    log("$afterText $afterStartIndex..$afterEndIndex - $markDown")
                }
            }
            separator()
            log(output, "output")
            return Pair(lineOutput, spans)
        }
    }
}

data class MarkDownPatternIndexer(val startIndex: Int, val markDown: MarkDown)
data class MarkDownPattern(val afterText: String, val afterStartIndex: Int, val afterEndIndex: Int, val markDown: MarkDown, val beforeText: String)
data class SingleLinePattern(val text: String, val patterns: MutableList<MarkDownPattern>)

internal fun String.stripMarkdown(markDowns: List<MarkDown>, isMultiLine: Boolean = false): String {
    var group: String
    var valueList: List<String> = listOf(this)
    val outputList: MutableList<String> = mutableListOf()

    if(isMultiLine && this.contains(MessageTextUtils.LINE_DELIMITER)){
        valueList = this.split(MessageTextUtils.LINE_DELIMITER)
    }

    valueList.forEach { lineValue ->
        var line = lineValue
        markDowns.forEach {
            val matcher = Pattern.compile(it.getRegex()).matcher(line)
            while (matcher.find()) {
                group = matcher.group()

                line = line.replace(group, it.getLabel(group))
            }
        }
        outputList.add(line)
    }
    return outputList.joinToString( MessageTextUtils.LINE_DELIMITER )
}


