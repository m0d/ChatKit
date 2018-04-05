package com.stfalcon.chatkit.messages.markdown

import java.util.regex.Pattern

/**
 * @author Grzegorz Pawełczuk
 * @email grzegorz.pawelczuk@ftlearning.com
 * Nikkei FT Learning Limited
 * @since 04.04.2018
 */

abstract class MarkDown{
    abstract fun getStart() : Int
    abstract fun getEnd() : Int
    abstract fun isFullLine(): Boolean
    abstract fun getType(): @MarkDownType.MarkDowns Long
    abstract fun getRegex(): String
    open fun getPattern(): Pattern = Pattern.compile(getRegex())
    open fun matches(text: String): Boolean = getPattern().matcher(text).find()
    open fun find(text: String): MatchResult? = getRegex().toRegex().find(text)
    open fun getDynamicStart(text: String) : Int = 0
    open fun getDynamicEnd(text: String) : Int = 0
    open fun getLabel(text: String) : String = ""
    open fun getAttribute(text: String) : String = ""
}

object Bold : MarkDown() {
    override fun isFullLine(): Boolean = false
    override fun getType(): Long = MarkDownType.BOLD
    override fun getStart(): Int = 1
    override fun getEnd(): Int = 1
    override fun getRegex(): String = "(?<!(([\\p{Alnum}])|\\*))\\*([^*\\n]+)\\*(?!(([\\p{Alnum}])|\\*))"
    override fun getLabel(text: String): String {
        return text.substring(getStart(), text.length - getEnd())
    }
}

object Italic : MarkDown() {
    override fun isFullLine(): Boolean = false
    override fun getType(): Long = MarkDownType.ITALIC
    override fun getStart(): Int = 1
    override fun getEnd(): Int = 1
    override fun getRegex(): String = "(?<!(([\\p{Alnum}])|_))_([^_\\n]+)_(?!(([\\p{Alnum}])|_))"
    override fun getLabel(text: String): String {
        return text.substring(getStart(), text.length - getEnd())
    }
}

object Strike : MarkDown() {
    override fun isFullLine(): Boolean = false
    override fun getType(): Long = MarkDownType.STROKE
    override fun getStart(): Int = 1
    override fun getEnd(): Int = 1
    override fun getRegex(): String = "(?<!(([\\p{Alnum}])|~))~([^~\\n]+)~(?!(([\\p{Alnum}])|~))"
    override fun getLabel(text: String): String {
        return text.substring(getStart(), text.length - getEnd())
    }
}

object Link : MarkDown() {
    override fun isFullLine(): Boolean = false
    override fun getType(): Long = MarkDownType.LINK
    override fun getStart(): Int = 1
    override fun getEnd(): Int = 1
    override fun getRegex(): String = "<([a-zA-Z]{2,10}:(.*?))>"
    override fun getLabel(text: String): String {
        val data = text.substring(getStart(), text.length - getEnd())
        return if (data.contains("|")) {
            val split = data.split("|")
            split[1]
        } else {
            data
        }
    }
    override fun getAttribute(text: String): String {
        val data = text.substring(getStart(), text.length - getEnd())
        return if (data.contains("|")) {
            val split = data.split("|")
            split[0]
        } else {
            data
        }
    }
}

object Quote : MarkDown() {
    override fun isFullLine(): Boolean = true
    override fun getType(): Long = MarkDownType.QUOTE
    override fun getStart(): Int = 1
    override fun getEnd(): Int = 0
    override fun getRegex(): String = "^>(.+)"
    override fun getLabel(text: String): String {
        return Regex(getRegex()).replace(text){ match ->
            match.value.substring(getStart())
        }
    }
}

object Bullet : MarkDown() {
    override fun isFullLine(): Boolean = true
    override fun getType(): Long = MarkDownType.BULLET
    override fun getStart(): Int = 1
    override fun getEnd(): Int = 0
    override fun getRegex(): String = "(-)(.+)"
    override fun getLabel(text: String): String {
        return Regex(getRegex()).replace(text){ match ->
            match.value.substring(getStart())
        }
    }
}

object Number : MarkDown() {
    override fun isFullLine(): Boolean = true
    override fun getType(): Long = MarkDownType.NUMBERED
    override fun getStart(): Int = -1
    override fun getEnd(): Int = 0
    override fun getRegex(): String = "^(\\d+).(.+)"
    override fun getDynamicStart(text: String): Int {
        return getAttribute(text).length + 1
    }
    override fun getLabel(text: String): String {
        return Regex(getRegex()).replace(text){ match ->
            match.value.substring(getDynamicStart(text))
        }
    }
    override fun getAttribute(text: String): String {
        val split = text.split(".")
        return if(split.size > 1) split[0] else ""
    }
}