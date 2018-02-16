package com.stfalcon.chatkit.messages.utils

import android.view.View
import java.util.regex.Pattern

/**
 * @author Mikołaj Kowal <mikolaj.kowal@nftlearning.com>
 * Nikkei FT Learning Limited
 * @since 14.02.2018
 * 14-02-2018 - Mikołaj Kowal - added support for basic emojis
 * 16-02-2018 - Grzegorz Pawełczuk - optimization
 */

class EmojiTextUtils {
    companion object {
        private val emojiPairs: MutableList<Pair<String, Int>> = mutableListOf(
            Pair("slightly_smiling_face", 0x1F600),
            Pair("disappointed", 0x1F61E),
            Pair("smile", 0x1F603),
            Pair("stuck_out_tongue", 0x1F60B),
            Pair("kissing_heart", 0x1F618),
            Pair("confused", 0x1F615),
            Pair("heart", 0x2764),
            Pair("heart_eyes", 0x1F60D)
        )

        fun transform(text: String) : String{
            return EmojiTextUtils.transformText(text, EmojiTextUtils.detectEmojis(text))
        }

        fun detectEmojis(text: String): MutableList<EmojiDescriptor> {
            val list: MutableList<EmojiDescriptor> = mutableListOf()
            val pattern = Pattern.compile(":(.*?):")
            val matcher = pattern.matcher(text)
            var thisEmojiDescriptor: EmojiDescriptor
            var group: String
            while (matcher.find()) {
                group = matcher.group().replace(":", "")
                if (getEmoji(group) != View.NO_ID) {
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
                code = getEmoji(descriptor.content)
                transformedText = transformedText.replace(descriptor.toTag(), String(Character.toChars(code)))
            }
            return transformedText
        }

        private fun getEmoji(text: String): Int {
            return emojiPairs
                    .firstOrNull { it.first == text }
                    ?.second
                    ?: View.NO_ID
        }
    }

    data class EmojiDescriptor(val content: String) {
        fun toTag(): String {
            return ":$content:"
        }
    }
}