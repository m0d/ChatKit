package com.stfalcon.chatkit.messages

import android.support.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.regex.Pattern

/**
 * Created by Mikołaj on 2/15/2018.
 */
class EmojisDetect {
    companion object {

        fun detectEmojis(text: String): MutableList<EmojiDescriptor> {
            val list: MutableList<EmojiDescriptor> = mutableListOf()
            val pattern = Pattern.compile(":(.*?):")
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                var group = matcher.group().replace(":","")
                if (whichEmoji(group) != -1) {
                    val thisEmojiDescriptor = EmojiDescriptor(group)
                    if (!list.contains(thisEmojiDescriptor)) {
                        list.add(thisEmojiDescriptor)
                    }
                }
            }
            return list
        }

        fun transformText(text: String, descriptors: MutableList<EmojiDescriptor>): String {
            var transformedText = text
            descriptors.forEach { descriptor: EmojiDescriptor->
                var code: Int = whichEmoji(descriptor.content)
                transformedText = transformedText.replace(descriptor.toTag(), String(Character.toChars(code)))
            }
            return transformedText
        }

        fun whichEmoji(text: String): Int {
            val emojiPairs: MutableList<Pair<String, Int>> = mutableListOf((Pair("slightly_smiling_face", 0x1F600)),
                    Pair("disappointed", 0x1F641), Pair("smile", 0x1F603), Pair("stuck_out_tongue", 0x1F60B), Pair("kissing_heart", 0x1F618),
                    Pair("confused", 0x1F615), Pair("heart", 0x2764), Pair("heart_eyes", 0x1F60D)
            )
            for (pair in emojiPairs) {
                if (pair.first == text)
                    return pair.second
            }
            return -1
        }

    }


    data class EmojiDescriptor(val content: String) {
        fun toTag(): String {
            return ":${content}:"
        }
    }
}