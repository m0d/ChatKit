package com.stfalcon.chatkit

import com.stfalcon.chatkit.messages.markdown.*
import com.stfalcon.chatkit.messages.markdown.Number
import com.stfalcon.chatkit.messages.utils.MarkDownPattern
import com.stfalcon.chatkit.messages.utils.MessageTextUtils
import com.stfalcon.chatkit.messages.utils.MessageTextUtils.Companion.LINE_DELIMITER
import com.stfalcon.chatkit.messages.utils.MessageTextUtils.Companion.getLineSpan
import com.stfalcon.chatkit.messages.utils.MessageTextUtils.Companion.toLinePatterns
import com.stfalcon.chatkit.messages.utils.stripMarkdown
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * @author Grzegorz Pawełczuk
 * @email grzegorz.pawelczuk@ftlearning.com
 * Nikkei FT Learning Limited
 * @since 06/04/2018.
 */

class MessageTextUtilsTest {
    companion object {
        val INPUT: MutableList<String> = mutableListOf(
                "*test1*",
                " *test2*",
                "_test3_",
                " _test4_",
                "~test5~",
                " ~test6~",
                ">test7",
                "> test8",
                "> test9 test test test test test",
                "-test10",
                "- test11",
                "- test12 test test test test",
                "1.test13",
                "10.test14",
                "10. test15",
                "10. test16 test test test test test test test test test",
                "hmmmm 10. test16 test test test test test test test test test",
                "-~_*test fraze <https://www.google.com>*_~",
                "-~_test fraze *<https://www.google.com>*_~",
                ">test1 _*<http://example.com/test/path>*_\n-*after* <https://www.google.com|Terefere qq?>",
                ">test1 alibaba _*test2*_ _*test3*_ ~*_test4_*~\n~test5 *_test6_*~",
                "-test1\n-test2\n-test3\n>test <test>",
                "-test1\n-test2\n-test3\n>test >test",
                "1.test1 *<https://www.google.com>*\n2.test2 <https://www.google.com>\n-test3"
        )

        val OUTPUT: MutableList<Pair<String, MutableList<MarkDownPattern>>> = mutableListOf(
                Pair("test1", mutableListOf(MarkDownPattern(afterText = "test1", afterStartIndex = 0, afterEndIndex = 5, markDown = Bold, beforeText = "*test1*"))),
                Pair(" test2", mutableListOf(MarkDownPattern(afterText = "test2", afterStartIndex = 1, afterEndIndex = 6, markDown = Bold, beforeText = "*test2*"))),
                Pair("test3", mutableListOf(MarkDownPattern(afterText = "test3", afterStartIndex = 0, afterEndIndex = 5, markDown = Italic, beforeText = "_test3_"))),
                Pair(" test4", mutableListOf(MarkDownPattern(afterText = "test4", afterStartIndex = 1, afterEndIndex = 6, markDown = Italic, beforeText = "_test4_"))),
                Pair("test5", mutableListOf(MarkDownPattern(afterText = "test5", afterStartIndex = 0, afterEndIndex = 5, markDown = Strike, beforeText = "~test5~"))),
                Pair(" test6", mutableListOf(MarkDownPattern(afterText = "test6", afterStartIndex = 1, afterEndIndex = 6, markDown = Strike, beforeText = "~test6~"))),
                Pair("test7", mutableListOf(MarkDownPattern(afterText = "test7", afterStartIndex = 0, afterEndIndex = 5, markDown = Quote, beforeText = ">test7"))),
                Pair(" test8", mutableListOf(MarkDownPattern(afterText = " test8", afterStartIndex = 0, afterEndIndex = 6, markDown = Quote, beforeText = "> test8"))),
                Pair(" test9 test test test test test", mutableListOf(MarkDownPattern(afterText = " test9 test test test test test", afterStartIndex = 0, afterEndIndex = 31, markDown = Quote, beforeText = "> test9 test test test test test"))),
                Pair("test10", mutableListOf(MarkDownPattern(afterText = "test10", afterStartIndex = 0, afterEndIndex = 6, markDown = Bullet, beforeText = "-test10"))),
                Pair(" test11", mutableListOf(MarkDownPattern(afterText = " test11", afterStartIndex = 0, afterEndIndex = 7, markDown = Bullet, beforeText = "- test11"))),
                Pair(" test12 test test test test", mutableListOf(MarkDownPattern(afterText = " test12 test test test test", afterStartIndex = 0, afterEndIndex = 27, markDown = Bullet, beforeText = "- test12 test test test test"))),
                Pair("test13", mutableListOf(MarkDownPattern(afterText = "test13", afterStartIndex = 0, afterEndIndex = 6, markDown = Number, beforeText = "1.test13"))),
                Pair("test14", mutableListOf(MarkDownPattern(afterText = "test14", afterStartIndex = 0, afterEndIndex = 6, markDown = Number, beforeText = "10.test14"))),
                Pair(" test15", mutableListOf(MarkDownPattern(afterText = " test15", afterStartIndex = 0, afterEndIndex = 7, markDown = Number, beforeText = "10. test15"))),
                Pair(" test16 test test test test test test test test test", mutableListOf(MarkDownPattern(afterText = " test16 test test test test test test test test test", afterStartIndex = 0, afterEndIndex = 52, markDown = Number, beforeText = "10. test16 test test test test test test test test test"))),
                Pair("hmmmm 10. test16 test test test test test test test test test", mutableListOf()),
                Pair("test fraze https://www.google.com", mutableListOf(
                        MarkDownPattern(afterText = "test fraze https://www.google.com", afterStartIndex = 0, afterEndIndex = 33, markDown = Bullet, beforeText = "-~_*test fraze <https://www.google.com>*_~"),
                        MarkDownPattern(afterText = "test fraze https://www.google.com", afterStartIndex = 0, afterEndIndex = 33, markDown = Strike, beforeText = "~_*test fraze <https://www.google.com>*_~"),
                        MarkDownPattern(afterText = "test fraze https://www.google.com", afterStartIndex = 0, afterEndIndex = 33, markDown = Italic, beforeText = "_*test fraze <https://www.google.com>*_"),
                        MarkDownPattern(afterText = "test fraze https://www.google.com", afterStartIndex = 0, afterEndIndex = 33, markDown = Bold, beforeText = "*test fraze <https://www.google.com>*"),
                        MarkDownPattern(afterText = "https://www.google.com", afterStartIndex = 11, afterEndIndex = 33, markDown = Link, beforeText = "<https://www.google.com>")
                )),
                Pair("test fraze https://www.google.com", mutableListOf(
                        MarkDownPattern(afterText = "test fraze https://www.google.com", afterStartIndex = 0, afterEndIndex = 33, markDown = Bullet, beforeText = "-~_test fraze *<https://www.google.com>*_~"),
                        MarkDownPattern(afterText = "test fraze https://www.google.com", afterStartIndex = 0, afterEndIndex = 33, markDown = Strike, beforeText = "~_test fraze *<https://www.google.com>*_~"),
                        MarkDownPattern(afterText = "test fraze https://www.google.com", afterStartIndex = 0, afterEndIndex = 33, markDown = Italic, beforeText = "_test fraze *<https://www.google.com>*_"),
                        MarkDownPattern(afterText = "https://www.google.com", afterStartIndex = 11, afterEndIndex = 33, markDown = Bold, beforeText = "*<https://www.google.com>*"),
                        MarkDownPattern(afterText = "https://www.google.com", afterStartIndex = 11, afterEndIndex = 33, markDown = Link, beforeText = "<https://www.google.com>")
                )),
                Pair("test1 http://example.com/test/path\nafter Terefere qq?", mutableListOf(
                        MarkDownPattern(afterText="test1 http://example.com/test/path", afterStartIndex=0, afterEndIndex=34, markDown=Quote, beforeText=">test1 _*<http://example.com/test/path>*_"),
                        MarkDownPattern(afterText="http://example.com/test/path", afterStartIndex=6, afterEndIndex=34, markDown=Italic, beforeText="_*<http://example.com/test/path>*_"),
                        MarkDownPattern(afterText="http://example.com/test/path", afterStartIndex=6, afterEndIndex=34, markDown=Bold, beforeText="*<http://example.com/test/path>*"),
                        MarkDownPattern(afterText="http://example.com/test/path", afterStartIndex=6, afterEndIndex=34, markDown=Link, beforeText="<http://example.com/test/path>"),
                        MarkDownPattern(afterText="after Terefere qq?", afterStartIndex=0, afterEndIndex=18, markDown=Bullet, beforeText="-*after* <https://www.google.com|Terefere qq?>"),
                        MarkDownPattern(afterText="after", afterStartIndex=0, afterEndIndex=5, markDown=Bold, beforeText="*after*"),
                        MarkDownPattern(afterText="Terefere qq?", afterStartIndex=6, afterEndIndex=18, markDown=Link, beforeText="<https://www.google.com|Terefere qq?>")
                )),
                Pair("test1 alibaba test2 test3 test4\ntest5 test6", mutableListOf(
                        MarkDownPattern(afterText="test1 alibaba test2 test3 test4", afterStartIndex=0, afterEndIndex=31, markDown=Quote, beforeText=">test1 alibaba _*test2*_ _*test3*_ ~*_test4_*~"),
                        MarkDownPattern(afterText="test2", afterStartIndex=14, afterEndIndex=19, markDown=Italic, beforeText="_*test2*_"),
                        MarkDownPattern(afterText="test2", afterStartIndex=14, afterEndIndex=19, markDown=Bold, beforeText="*test2*"),
                        MarkDownPattern(afterText="test3", afterStartIndex=20, afterEndIndex=25, markDown=Italic, beforeText="_*test3*_"),
                        MarkDownPattern(afterText="test3", afterStartIndex=20, afterEndIndex=25, markDown=Bold, beforeText="*test3*"),
                        MarkDownPattern(afterText="test4", afterStartIndex=26, afterEndIndex=31, markDown=Strike, beforeText="~*_test4_*~"),
                        MarkDownPattern(afterText="test4", afterStartIndex=26, afterEndIndex=31, markDown=Bold, beforeText="*_test4_*"),
                        MarkDownPattern(afterText="test4", afterStartIndex=26, afterEndIndex=31, markDown=Italic, beforeText="_test4_"),
                        MarkDownPattern(afterText="test5 test6", afterStartIndex=0, afterEndIndex=11, markDown=Strike, beforeText="~test5 *_test6_*~"),
                        MarkDownPattern(afterText="test6", afterStartIndex=6, afterEndIndex=11, markDown=Bold, beforeText="*_test6_*"),
                        MarkDownPattern(afterText="test6", afterStartIndex=6, afterEndIndex=11, markDown=Italic, beforeText="_test6_")
                )),
                Pair("test1\ntest2\ntest3\ntest <test>", mutableListOf(
                        MarkDownPattern(afterText="test1", afterStartIndex=0, afterEndIndex=5, markDown=Bullet, beforeText="-test1"),
                        MarkDownPattern(afterText="test2", afterStartIndex=0, afterEndIndex=5, markDown=Bullet, beforeText="-test2"),
                        MarkDownPattern(afterText="test3", afterStartIndex=0, afterEndIndex=5, markDown=Bullet, beforeText="-test3"),
                        MarkDownPattern(afterText="test <test>", afterStartIndex=0, afterEndIndex=11, markDown=Quote, beforeText=">test <test>")
                )),
                Pair("test1\ntest2\ntest3\ntest >test", mutableListOf(
                        MarkDownPattern(afterText="test1", afterStartIndex=0, afterEndIndex=5, markDown=Bullet, beforeText="-test1"),
                        MarkDownPattern(afterText="test2", afterStartIndex=0, afterEndIndex=5, markDown=Bullet, beforeText="-test2"),
                        MarkDownPattern(afterText="test3", afterStartIndex=0, afterEndIndex=5, markDown=Bullet, beforeText="-test3"),
                        MarkDownPattern(afterText="test >test", afterStartIndex=0, afterEndIndex=10, markDown=Quote, beforeText=">test >test")

                )),
                Pair("test1 https://www.google.com\ntest2 https://www.google.com\ntest3", mutableListOf(
                        MarkDownPattern(afterText="test1 https://www.google.com", afterStartIndex=0, afterEndIndex=28, markDown=Number, beforeText="1.test1 *<https://www.google.com>*"),
                        MarkDownPattern(afterText="https://www.google.com", afterStartIndex=6, afterEndIndex=28, markDown=Bold, beforeText="*<https://www.google.com>*"),
                        MarkDownPattern(afterText="https://www.google.com", afterStartIndex=6, afterEndIndex=28, markDown=Link, beforeText="<https://www.google.com>"),
                        MarkDownPattern(afterText="test2 https://www.google.com", afterStartIndex=0, afterEndIndex=28, markDown=Number, beforeText="2.test2 <https://www.google.com>"),
                        MarkDownPattern(afterText="https://www.google.com", afterStartIndex=6, afterEndIndex=28, markDown=Link, beforeText="<https://www.google.com>"),
                        MarkDownPattern(afterText="test3", afterStartIndex=0, afterEndIndex=5, markDown=Bullet, beforeText="-test3")
                ))
        )
    }

    @Test
    fun stripMarkdown() {
        INPUT.forEachIndexed { index, phrase ->
            val expectedResult = OUTPUT[index].first
            val realResult = phrase.stripMarkdown(MessageTextUtils.SUPPORTED_MARKDOWNS, true)
            assertEquals(expectedResult, realResult)
        }
    }

    @Test
    fun spannableCountCheckTest() {
        INPUT.forEachIndexed { index, phrase ->
            val expectedResult = OUTPUT[index].second.size
            val realResult = toLinePatterns(phrase).toMutableList().sumBy { it.patterns.size }
            assertEquals(expectedResult, realResult)
        }
    }

    @Test
    fun lineSpanTest() {
        INPUT.forEachIndexed { index, phrase ->
            if (!phrase.contains(LINE_DELIMITER)) {
                assertEquals(OUTPUT[index], getLineSpan(phrase))
            } else {
                val split = phrase.split(LINE_DELIMITER)
                val patterns : MutableList<MarkDownPattern> = mutableListOf()
                val text : MutableList<String> = mutableListOf()
                split.forEach{
                    val element = getLineSpan(it)
                    text.add(element.first)
                    patterns.addAll(element.second)
                }
                val content = text.joinToString(LINE_DELIMITER)
                assertEquals(OUTPUT[index], Pair(content, patterns))
            }
        }
        assertTrue(true)
    }
}


