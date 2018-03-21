package com.stfalcon.chatkit

import com.stfalcon.chatkit.messages.MarkDown
import com.stfalcon.chatkit.messages.utils.MessageTextUtils
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * @author Maciej Madetko
 * @email maciej.madetko@ftlearning.com
 * Nikkei FT Learning Limited
 * @since 20/02/2018.
 * 02-03-2018 - Grzegorz Pawełczuk - Quote test
 */
class PatternDescriptorTest {

    @Test
    fun boldTest() {
        val content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit"
        val expected = "*Lorem ipsum dolor sit amet, consectetur adipiscing elit*"

        val pattern = MessageTextUtils.PatternDescriptor(content, null, surrounding = MarkDown.BOLD)
        assertEquals(expected,pattern.toTag())
    }

    @Test
    fun italicTest() {
        val content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit"
        val expected = "_Lorem ipsum dolor sit amet, consectetur adipiscing elit_"

        val pattern = MessageTextUtils.PatternDescriptor(content, null, surrounding = MarkDown.ITALIC)
        assertEquals(expected,pattern.toTag())
    }

    @Test
    fun strokeTest() {
        val content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit"
        val expected = "~Lorem ipsum dolor sit amet, consectetur adipiscing elit~"

        val pattern = MessageTextUtils.PatternDescriptor(content, null, surrounding = MarkDown.STROKE)
        assertEquals(expected,pattern.toTag())
    }

    @Test
    fun linkTest() {
        val content = "http://lorem.ipsum.dolor.com"
        val expected = "<http://lorem.ipsum.dolor.com>"

        val pattern = MessageTextUtils.PatternDescriptor(content, null, surrounding = MarkDown.LINK)
        assertEquals(expected,pattern.toTag())
    }

    @Test
    fun linkLabelTest() {
        val content = "http://lorem.ipsum.dolor.com"
        val label = "Lorem ipsum"
        val expected = "<http://lorem.ipsum.dolor.com|Lorem ipsum>"

        val pattern = MessageTextUtils.PatternDescriptor(content, label = label, isQuote = false, surrounding = MarkDown.LINK)
        assertEquals(expected,pattern.toTag())
    }

    @Test
    fun linkLabelToDisplayTest() {
        val content = "http://lorem.ipsum.dolor.com"
        val label = "Lorem ipsum"
        val expected = "Lorem ipsum"

        val pattern = MessageTextUtils.PatternDescriptor(content, label = label, isQuote = false, surrounding = MarkDown.LINK)
        assertEquals(expected,pattern.getLabelToDisplay())
    }

    @Test
    fun labelToDisplayTest() {
        val content = "http://lorem.ipsum.dolor.com"
        val label = null
        val expected = "http://lorem.ipsum.dolor.com"

        val pattern = MessageTextUtils.PatternDescriptor(content, label = label, isQuote = false, surrounding = MarkDown.LINK)
        assertEquals(expected,pattern.getLabelToDisplay())
    }

    @Test
    fun unknownSurroundingTest() {
        val content = "http://lorem.ipsum.dolor.com"
        val expected = "http://lorem.ipsum.dolor.com"

        val pattern = MessageTextUtils.PatternDescriptor(content, null, surrounding = 12)
        assertEquals(expected,pattern.toTag())
    }

    @Test
    fun unknownSurroundingTestBoldFailure() {
        val content = "Lorem ipsum dolor sit amet"
        val expected = "*Lorem ipsum dolor sit amet*"

        val pattern = MessageTextUtils.PatternDescriptor(content, null, surrounding = 5)
        assertNotEquals(expected,pattern.toTag())
    }

    @Test
    fun unknownSurroundingTestStrokeFailure() {
        val content = "Lorem ipsum dolor sit amet"
        val expected = "~Lorem ipsum dolor sit amet~"

        val pattern = MessageTextUtils.PatternDescriptor(content, null, surrounding = MarkDown.ITALIC)
        assertNotEquals(expected,pattern.toTag())
    }

    @Test
    fun quoteTest() {
        val content = "Lorem ipsum dolor sit amet"
        val expected = "\\&gt;Lorem ipsum dolor sit amet"

        val pattern = MessageTextUtils.PatternDescriptor(content, null, surrounding = MarkDown.QUOTE)
        assertNotEquals(expected,pattern.toTag())
    }

    @Test
    fun linkSchemaLabelTest() {
        val content = "nftl://lorem.ipsum.dolor.com/lorem/ipsum"
        val label = "Lorem ipsum"
        val expected = "<nftl://lorem.ipsum.dolor.com/lorem/ipsum|Lorem ipsum>"

        val pattern = MessageTextUtils.PatternDescriptor(content, label = label, surrounding = MarkDown.LINK)
        assertEquals(expected,pattern.toTag())
    }

    @Test
    fun linkComplexLabelTest() {
        val content = "nftl://lorem.ipsum.dolor.com/lorem/ipsum"
        val label = "Lorem ipsum: lorem & ipsum"
        val expected = "<nftl://lorem.ipsum.dolor.com/lorem/ipsum|Lorem ipsum: lorem & ipsum>"

        val pattern = MessageTextUtils.PatternDescriptor(content, label = label, surrounding = MarkDown.LINK)
        assertEquals(expected,pattern.toTag())
    }

    @Test
    fun linkNftlLabelTest() {
        val content = "nftl://launcher/collaborate/CHANNELXXX/scenario_name/USERXXX/USERZZZ"
        val label = "Module 1: Test Name"
        val expected = "<nftl://launcher/collaborate/CHANNELXXX/scenario_name/USERXXX/USERZZZ|Module 1: Test Name>"

        val pattern = MessageTextUtils.PatternDescriptor(content, label = label, surrounding = MarkDown.LINK)
        assertEquals(expected,pattern.toTag())
    }

}