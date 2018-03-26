package com.stfalcon.chatkit

import com.stfalcon.chatkit.messages.MarkDown
import com.stfalcon.chatkit.messages.utils.MessageTextUtils
import org.junit.Test
import kotlin.test.assertEquals

/**
 * @author Maciej Madetko
 * @email maciej.madetko@ftlearning.com
 * Nikkei FT Learning Limited
 * @since 20/02/2018.
 * 02-03-2018 - Grzegorz Pawełczuk - Quote test
 */
class MessageTextUtilsTest {

    @Test
    fun strokeTest() {
        val content = "~dajshdj~"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor("dajshdj",
                        null, false, false, false, true,
                        false,
                        surrounding = MarkDown.STROKE))

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun boldTest() {
        val content = "*Lorem ipsum dolor sit amet*"
        val contentWithoutMarkdown = "Lorem ipsum dolor sit amet"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor(contentWithoutMarkdown,
                        null, false, true, false, false,
                        false,
                        surrounding = MarkDown.BOLD))

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun italicTest() {
        val content = "_dasdasdasd_"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor("dasdasdasd",
                        null, false, false, true, false,
                        false,
                        surrounding = MarkDown.ITALIC))

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun linkTest() {
        val content = "<http://onet.pl>"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor("http://onet.pl",
                        null, true, false, false, false,
                        false,
                        surrounding = MarkDown.LINK))

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun boldWithLinkTest() {
        val content = "test *<http://onet.pl>* *bold*"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor("http://onet.pl",
                        null, true, true, false, false,
                        false,
                        surrounding = MarkDown.LINK),
                MessageTextUtils.PatternDescriptor("http://onet.pl",
                        null, false, true, false, false,
                        false,
                        surrounding = MarkDown.BOLD),
                MessageTextUtils.PatternDescriptor("bold",
                        null, false, true, false, false,
                        false,
                        surrounding = MarkDown.BOLD))

        val patterns = MessageTextUtils.getTextPatterns(content)
        assertEquals(expected, patterns)
    }

    @Test
    fun allMarkdownInlineTest() {
        val content = MessageTextUtils.fromEntities("~dajshdj~ *bdsajjh* _dasdasdasd_ <dasdasfdfdssdf>")
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor("dajshdj",
                        null, false, false, false, true,
                        false,
                        surrounding = MarkDown.STROKE),
                MessageTextUtils.PatternDescriptor("bdsajjh",
                        null, false, true, false, false,
                        false,
                        surrounding = MarkDown.BOLD),
                MessageTextUtils.PatternDescriptor("dasdasdasd",
                        null, false, false, true, false,
                        false,
                        surrounding = MarkDown.ITALIC))

        val patterns = MessageTextUtils.getTextPatterns(content)
        assertEquals(expected, patterns)
    }

    @Test
    fun sanitiseTest() {
        val content = "m**********r"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf()

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun sanitiseAndBoldTest() {
        val content = "**h*k* *word*"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor("word", null,
                        false, true, false, false, false, surrounding = MarkDown.BOLD))

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun sanitiseTrailingTest() {
        val content = "*dasadasa**"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf()

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun sanitiseLeadingTest() {
        val content = "***w"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf()

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun emptyBoldTest() {
        val content = "c * * t"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor(" ", null,
                        false, true, false, false, false, surrounding = MarkDown.BOLD))

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun nullBoldTest() {
        val content = "c ** t"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf()

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun welcomeBoldTest() {
        val content = "Hi, *endry04* and *endry05*!"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor("endry04", null,
                        false, true, false, false, false, surrounding = MarkDown.BOLD),
                MessageTextUtils.PatternDescriptor("endry05", null,
                        false, true, false, false, false, surrounding = MarkDown.BOLD)

        )

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun specialCharsBoldTest() {
        val content = "Hi,*endry04* and*endry05*!"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor("endry04", null,
                        false, true, false, false, false, surrounding = MarkDown.BOLD)

        )

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }


    @Test
    fun quotationTest() {
        val content = MessageTextUtils.fromEntities("Mark said before:\n&gt;I love cars\n and you know what? I am *OK* with that!")
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor(content = "I love cars", label = null,
                        isLink = false, isBold = false, isItalic = false, isStroke = false, isQuote = true, beginIndex = 0, endIndex = 0, offset = 0,
                        surrounding = MarkDown.QUOTE),
                MessageTextUtils.PatternDescriptor(content = "OK", label = null,
                        isLink = false, isBold = true, isItalic = false, isStroke = false, isQuote = false, beginIndex = 0, endIndex = 0, offset = 0,
                        surrounding = MarkDown.BOLD)
        )
        val actual = MessageTextUtils.getTextPatterns(content)
        assertEquals(expected, actual)
    }

    @Test
    fun deepLinkTest() {
        val content = MessageTextUtils.fromEntities("<somescheme://onet.pl/path1/path2|title: label with whitespaces>")
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor("somescheme://onet.pl/path1/path2",
                        "title: label with whitespaces", true, false, false, false,
                        surrounding = MarkDown.LINK))

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun deepLinkUnderscoreTest() {
        val content = MessageTextUtils.fromEntities("lead *<somescheme://onet.pl/path1/path2_underscore/path3|title: label with whitespaces>*")

        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor(content="somescheme://onet.pl/path1/path2_underscore/path3", label="title: label with whitespaces", isLink=true, isBold=true, isItalic=false, isStroke=false, isQuote=false, beginIndex=0, endIndex=0, offset=0, surrounding=MarkDown.LINK),
                MessageTextUtils.PatternDescriptor(content="somescheme://onet.pl/path1/path2_underscore/path3|title: label with whitespaces", label=null, isLink=false, isBold=true, isItalic=false, isStroke=false, isQuote=false, beginIndex=0, endIndex=0, offset=0, surrounding=MarkDown.BOLD)
        )

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun extraLongTest() {
        val content = MessageTextUtils.fromEntities("dear xxx,\n• some text and write *_some fraze_*.\n• or *_I like it!_*, some text again... yyy is OK.\n Do *<somescheme://onet.pl/path1/path2_underscore/path3|title: label with whitespaces>*")

        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor(content="some fraze", label=null, isLink=false, isBold=true, isItalic=true, isStroke=false, isQuote=false, beginIndex=0, endIndex=0, offset=0, surrounding=MarkDown.ITALIC),
                MessageTextUtils.PatternDescriptor(content="some fraze", label=null, isLink=false, isBold=true, isItalic=false, isStroke=false, isQuote=false, beginIndex=0, endIndex=0, offset=0, surrounding=MarkDown.BOLD),
                MessageTextUtils.PatternDescriptor(content="I like it!", label=null, isLink=false, isBold=true, isItalic=true, isStroke=false, isQuote=false, beginIndex=0, endIndex=0, offset=0, surrounding=MarkDown.ITALIC),
                MessageTextUtils.PatternDescriptor(content="I like it!", label=null, isLink=false, isBold=true, isItalic=false, isStroke=false, isQuote=false, beginIndex=0, endIndex=0, offset=0, surrounding=MarkDown.BOLD),
                MessageTextUtils.PatternDescriptor(content="somescheme://onet.pl/path1/path2_underscore/path3", label="title: label with whitespaces", isLink=true, isBold=true, isItalic=false, isStroke=false, isQuote=false, beginIndex=0, endIndex=0, offset=0, surrounding=MarkDown.LINK),
                MessageTextUtils.PatternDescriptor(content="somescheme://onet.pl/path1/path2_underscore/path3|title: label with whitespaces", label=null, isLink=false, isBold=true, isItalic=false, isStroke=false, isQuote=false, beginIndex=0, endIndex=0, offset=0, surrounding=MarkDown.BOLD)
        )

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun quoteTest() {
        val content = MessageTextUtils.fromEntities("lead\n&gt;quote")

        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor(content="quote", label=null, isLink=false, isBold=false, isItalic=false, isStroke=false, isQuote=true, beginIndex=0, endIndex=0, offset=0, surrounding=MarkDown.QUOTE)
        )

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun quoteFirstLineTest() {
        val content = MessageTextUtils.fromEntities("&gt;quote")

        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor(content="quote", label=null, isLink=false, isBold=false, isItalic=false, isStroke=false, isQuote=true, beginIndex=0, endIndex=0, offset=0, surrounding=MarkDown.QUOTE)
        )

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun quoteWithBoldTest() {
        val content = "&gt;*quote*"

        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor(content="quote", label=null, isLink=false, isBold=true, isItalic=false, isStroke=false, isQuote=true, beginIndex=0, endIndex=0, offset=0, surrounding=MarkDown.BOLD),
                MessageTextUtils.PatternDescriptor(content="quote", label=null, isLink=false, isBold=false, isItalic=false, isStroke=false, isQuote=true, beginIndex=0, endIndex=0, offset=0, surrounding=MarkDown.QUOTE)
        )

        assertEquals(expected, MessageTextUtils.getTextPatterns(MessageTextUtils.fromEntities(content)))
    }

    @Test
    fun quoteWithBoldItalicTest() {
        val content = "&gt;*_quote_*"

        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor(content="quote", label=null, isLink=false, isBold=true, isItalic=true, isStroke=false, isQuote=true, beginIndex=0, endIndex=0, offset=0, surrounding=MarkDown.ITALIC),
                MessageTextUtils.PatternDescriptor(content="quote", label=null, isLink=false, isBold=true, isItalic=false, isStroke=false, isQuote=true, beginIndex=0, endIndex=0, offset=0, surrounding=MarkDown.BOLD),
                MessageTextUtils.PatternDescriptor(content="quote", label=null, isLink=false, isBold=false, isItalic=false, isStroke=false, isQuote=true, beginIndex=0, endIndex=0, offset=0, surrounding=MarkDown.QUOTE)
        )

        assertEquals(expected, MessageTextUtils.getTextPatterns(MessageTextUtils.fromEntities(content)))
    }


    @Test
    fun quoteMultiTest() {
        val content = "&gt;_kursywa jak zywa_ *bold z lupy*"

        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor(content="kursywa jak zywa", label=null, isLink=false, isBold=false, isItalic=true, isStroke=false, isQuote=true, beginIndex=0, endIndex=0, offset=0, surrounding=MarkDown.ITALIC),
                MessageTextUtils.PatternDescriptor(content="bold z lupy", label=null, isLink=false, isBold=true, isItalic=false, isStroke=false, isQuote=true, beginIndex=0, endIndex=0, offset=0, surrounding=MarkDown.BOLD),
                MessageTextUtils.PatternDescriptor(content="kursywa jak zywa bold z lupy", label=null, isLink=false, isBold=false, isItalic=false, isStroke=false, isQuote=true, beginIndex=0, endIndex=0, offset=0, surrounding=MarkDown.QUOTE)
        )

        assertEquals(expected, MessageTextUtils.getTextPatterns(MessageTextUtils.fromEntities(content)))
    }
}

