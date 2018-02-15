package com.stfalcon.chatkit.messages

import android.support.annotation.IntDef
import android.view.View
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.regex.Pattern

/**
 * @author Mikołaj Kowal <mikolaj.kowal@nftlearning.com>
 * Nikkei FT Learning Limited
 * @since 14.02.2018
 * 14-02-2018 - Mikołaj Kowal - added support for basic emojis
 */
class EmojisDetect {
    val emojiPairs: MutableList<Pair<String, Int>> = mutableListOf((
            Pair("slightly_smiling_face", 0x1F600)),
            Pair("disappointed", 0x1F61E),
            Pair("smile", 0x1F603),
            Pair("stuck_out_tongue", 0x1F60B),
            Pair("kissing_heart", 0x1F618),
            Pair("confused", 0x1F615),
            Pair("heart", 0x2764),
            Pair("heart_eyes", 0x1F60D)
    )

    fun detectEmojis(text: String): MutableList<EmojiDescriptor> {
        val list: MutableList<EmojiDescriptor> = mutableListOf()
        val pattern = Pattern.compile(":(.*?):")
        val matcher = pattern.matcher(text)
        var thisEmojiDescriptor: EmojiDescriptor
        while (matcher.find()) {
            var group = matcher.group().replace(":", "")
            if (whichEmoji(group) != View.NO_ID) {
                thisEmojiDescriptor = EmojiDescriptor(group)
                if (!list.contains(thisEmojiDescriptor)) {
                    list.add(thisEmojiDescriptor)
                }
            }
        }
        return list
    }

    fun transformText(text: String, descriptors: MutableList<EmojiDescriptor>): String {
        var transformedText = text
        var code: Int
        descriptors.forEach { descriptor: EmojiDescriptor ->
            code = whichEmoji(descriptor.content)
            transformedText = transformedText.replace(descriptor.toTag(), String(Character.toChars(code)))
        }
        return transformedText
    }

    fun whichEmoji(text: String): Int {

        for (pair in emojiPairs) {
            if (pair.first == text){
                return pair.second
            }
        }
        return View.NO_ID
    }


    data class EmojiDescriptor(val content: String) {
        fun toTag(): String {
            return ":${content}:"
        }
    }
}