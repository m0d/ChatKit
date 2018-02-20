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
 */
class MessageTextUtilsTest {

    @Test
    fun strokeTest() {
        val content = "~dajshdj~"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor("dajshdj",
                        null, false, false, false, true,
                        MarkDown.STROKE))

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun boldTest() {
        val content = "*Lorem ipsum dolor sit amet*"
        val contentWithoutMarkdown = "Lorem ipsum dolor sit amet"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor(contentWithoutMarkdown,
                        null, false, true, false, false,
                        MarkDown.BOLD))

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun italicTest() {
        val content = "_dasdasdasd_"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor("dasdasdasd",
                        null, false, false, true, false,
                        MarkDown.ITALIC))

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun linkTest() {
        val content = "<http://onet.pl>"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor("http://onet.pl",
                        null, true, false, false, false,
                        MarkDown.LINK))

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun boldWithLinkTest() {
        val content = "test *<http://onet.pl>* *bold*"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor("http://onet.pl",
                        null, true, true, false, false,
                        MarkDown.LINK),
                MessageTextUtils.PatternDescriptor("http://onet.pl",
                        null, false, true, false, false,
                        MarkDown.BOLD),
                MessageTextUtils.PatternDescriptor("bold",
                        null, false, true, false, false,
                        MarkDown.BOLD))

        val patterns = MessageTextUtils.getTextPatterns(content)
        System.out.println("a" + expected.toString())
        System.out.println("b" + patterns.toString())
        assertEquals(expected, patterns)
    }

    @Test
    fun allMarkdownInlineTest() {
        val content = "~dajshdj~ *bdsajjh* _dasdasdasd_ <dasdasfdfdssdf>"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor("dajshdj",
                        null, false, false, false, true,
                        MarkDown.STROKE),
                MessageTextUtils.PatternDescriptor("bdsajjh",
                        null, false, true, false, false,
                        MarkDown.BOLD),
                MessageTextUtils.PatternDescriptor("dasdasdasd",
                        null, false, false, true, false,
                        MarkDown.ITALIC),
                MessageTextUtils.PatternDescriptor("dasdasfdfdssdf",
                        null, true, false, false, false,
                        MarkDown.LINK))

        val patterns = MessageTextUtils.getTextPatterns(content)
        assertEquals(expected, patterns)
    }

    @Test
    fun sanitizeTest() {
        val content = "m**********r"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf()

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun sanitizeAndBoldTest() {
        val content = "**h*k* *word*"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor("word",null,
                        false, true, false, false, MarkDown.BOLD))

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun sanitizeTrailingTest() {
        val content = "*dasadasa**"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf()

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun sanitizeLeadingTest() {
        val content = "***w"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf()

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun emptyBoldTest() {
        val content = "c * * t"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf(
                MessageTextUtils.PatternDescriptor(" ",null,
                        false, true, false, false, MarkDown.BOLD))

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

    @Test
    fun nullBoldTest() {
        val content = "c ** t"
        val expected: MutableList<MessageTextUtils.PatternDescriptor> = mutableListOf()

        assertEquals(expected, MessageTextUtils.getTextPatterns(content))
    }

}