package com.stfalcon.chatkit.messages.utils

import android.view.View
import java.util.regex.Pattern

/**
 * @author Mikołaj Kowal <mikolaj.kowal@nftlearning.com>
 * Nikkei FT Learning Limited
 * @since 14.02.2018
 * 14-02-2018 - Mikołaj Kowal - added support for basic emojis
 * 16-02-2018 - Grzegorz Pawełczuk - optimization
 * 19-02-2019 - Mikołaj Kowal - added suport for most, if not all emojis(650 emojis)
 */

class EmojiTextUtils {
    companion object {
        private val emojiPairs: MutableList<Pair<String, Int>> = mutableListOf(
                // nature
                Pair("see_no_evil", 0x1F648),
                Pair("hear_no_evil", 0x1F649),
                Pair("speak_no_evil", 0x1F64A),
                Pair("boom", 0x1F4A5),
                Pair("sweat_drops", 0x1F4A6),
                Pair("dash", 0x1F4A8),
                Pair("dizzy", 0x1F4AB),
                Pair("monkey_face", 0x1F435),
                Pair("monkey", 0x1F412),
                Pair("dog", 0x1F436),
                Pair("dog2", 0x1F415),
                Pair("poodle", 0x1F429),
                Pair("wolf", 0x1F43A),
                Pair("cat", 0x1F431),
                Pair("cat2", 0x1F408),
                Pair("tiger", 0x1F42F),
                Pair("tiger2", 0x1F405),
                Pair("leopard", 0x1F406),
                Pair("horse", 0x1F434),
                Pair("racehorse", 0x1F40E),
                Pair("cow", 0x1F42E),
                Pair("ox", 0x1F402),
                Pair("water_buffalo", 0x1F403),
                Pair("cow2", 0x1F404),
                Pair("pig", 0x1F437),
                Pair("pig2", 0x1F416),
                Pair("boar", 0x1F417),
                Pair("pig_nose", 0x1F43D),
                Pair("ram", 0x1F40F),
                Pair("sheep", 0x1F411),
                Pair("goat", 0x1F410),
                Pair("dromedary_camel", 0x1F42A),
                Pair("camel", 0x1F42B),
                Pair("elephant", 0x1F418),
                Pair("mouse", 0x1F42D),
                Pair("mouse2", 0x1F401),
                Pair("rat", 0x1F400),
                Pair("hamster", 0x1F439),
                Pair("rabbit", 0x1F430),
                Pair("rabbit2", 0x1F407),
                Pair("bear", 0x1F43B),
                Pair("koala", 0x1F428),
                Pair("panda_face", 0x1F43C),
                Pair("feet", 0x1F43E),
                Pair("chicken", 0x1F414),
                Pair("rooster", 0x1F413),
                Pair("hatching_chick", 0x1F423),
                Pair("baby_chick", 0x1F424),
                Pair("hatched_chick", 0x1F425),
                Pair("bird", 0x1F426),
                Pair("penguin", 0x1F427),
                Pair("frog", 0x1F438),
                Pair("crocodile", 0x1F40A),
                Pair("turtle", 0x1F422),
                Pair("snake", 0x1F40D),
                Pair("dragon_face", 0x1F432),
                Pair("dragon", 0x1F409),
                Pair("whale", 0x1F433),
                Pair("whale2", 0x1F40B),
                Pair("dolphin", 0x1F42C),
                Pair("fish", 0x1F41F),
                Pair("tropical_fish", 0x1F420),
                Pair("blowfish", 0x1F421),
                Pair("octopus", 0x1F419),
                Pair("shell", 0x1F41A),
                Pair("snail", 0x1F40C),
                Pair("bug", 0x1F41B),
                Pair("ant", 0x1F41C),
                Pair("bee", 0x1F41D),
                Pair("beetle", 0x1F41E),
                Pair("bouquet", 0x1F490),
                Pair("cherry_blossom", 0x1F338),
                Pair("white_flower", 0x1F4AE),
                Pair("rose", 0x1F339),
                Pair("hibiscus", 0x1F33A),
                Pair("sunflower", 0x1F33B),
                Pair("blossom", 0x1F33C),
                Pair("tulip", 0x1F337),
                Pair("seedling", 0x1F331),
                Pair("evergreen_tree", 0x1F332),
                Pair("deciduous_tree", 0x1F333),
                Pair("palm_tree", 0x1F334),
                Pair("cactus", 0x1F335),
                Pair("ear_of_rice", 0x1F33E),
                Pair("herb", 0x1F33F),
                Pair("four_leaf_clover", 0x1F340),
                Pair("maple_leaf", 0x1F341),
                Pair("fallen_leaf", 0x1F342),
                Pair("leaves", 0x1F343),
                Pair("mushroom", 0x1F344),
                Pair("chestnut", 0x1F330),
                Pair("earth_africa", 0x1F30D),
                Pair("earth_americas", 0x1F30E),
                Pair("earth_asia", 0x1F30F),
                Pair("globe_with_meridians", 0x1F310),
                Pair("new_moon", 0x1F311),
                Pair("waxing_crescent_moon", 0x1F312),
                Pair("first_quarter_moon", 0x1F313),
                Pair("moon", 0x1F314),
                Pair("full_moon", 0x1F315),
                Pair("waning_gibbous_moon", 0x1F316),
                Pair("last_quarter_moon", 0x1F317),
                Pair("waning_crescent_moon", 0x1F318),
                Pair("crescent_moon", 0x1F319),
                Pair("new_moon_with_face", 0x1F31A),
                Pair("first_quarter_moon_with_face", 0x1F31B),
                Pair("last_quarter_moon_with_face", 0x1F31C),
                Pair("sunny", 0x2600),
                Pair("full_moon_with_face", 0x1F31D),
                Pair("sun_with_face", 0x1F31E),
                Pair("star", 0x2B50),
                Pair("star2", 0x1F31F),
                Pair("stars", 0x1F320),
                Pair("cloud", 0x2601),
                Pair("partly_sunny", 0x26C5),
                Pair("rainbow", 0x1F308),
                Pair("umbrella", 0x2614),
                Pair("zap", 0x26A1),
                Pair("snowflake", 0x2744),
                Pair("snowman", 0x26C4),
                Pair("fire", 0x1F525),
                Pair("droplet", 0x1F4A7),
                Pair("ocean", 0x1F30A),
                Pair("christmas_tree", 0x1F384),
                Pair("sparkles", 0x2728),
                Pair("tanabata_tree", 0x1F38B),
                Pair("bamboo", 0x1F38D),
                // symbols
                Pair("cupid", 0x1F498),
                Pair("heart", 0x2764),
                Pair("heartbeat", 0x1F493),
                Pair("broken_heart", 0x1F494),
                Pair("two_hearts", 0x1F495),
                Pair("sparkling_heart", 0x1F496),
                Pair("heartpulse", 0x1F497),
                Pair("blue_heart", 0x1F499),
                Pair("green_heart", 0x1F49A),
                Pair("yellow_heart", 0x1F49B),
                Pair("purple_heart", 0x1F49C),
                Pair("gift_heart", 0x1F49D),
                Pair("revolving_hearts", 0x1F49E),
                Pair("heart_decoration", 0x1F49F),
                Pair("zzz", 0x1F4A4),
                Pair("anger", 0x1F4A2),
                Pair("speech_balloon", 0x1F4AC),
                Pair("thought_balloon", 0x1F4AD),
                Pair("white_flower", 0x1F4AE),
                Pair("hotsprings", 0x2668),
                Pair("barber", 0x1F488),
                Pair("clock12", 0x1F55B),
                Pair("clock1230", 0x1F567),
                Pair("clock1", 0x1F550),
                Pair("clock130", 0x1F55C),
                Pair("clock2", 0x1F551),
                Pair("clock230", 0x1F55D),
                Pair("clock3", 0x1F552),
                Pair("clock330", 0x1F55E),
                Pair("clock4", 0x1F553),
                Pair("clock430", 0x1F55F),
                Pair("clock5", 0x1F554),
                Pair("clock530", 0x1F560),
                Pair("clock6", 0x1F555),
                Pair("clock630", 0x1F561),
                Pair("clock7", 0x1F556),
                Pair("clock730", 0x1F562),
                Pair("clock8", 0x1F557),
                Pair("clock830", 0x1F563),
                Pair("clock9", 0x1F558),
                Pair("clock930", 0x1F564),
                Pair("clock10", 0x1F559),
                Pair("clock1030", 0x1F565),
                Pair("clock11", 0x1F55A),
                Pair("clock1130", 0x1F566),
                Pair("cyclone", 0x1F300),
                Pair("spades", 0x2660),
                Pair("hearts", 0x2665),
                Pair("diamonds", 0x2666),
                Pair("clubs", 0x2663),
                Pair("black_joker", 0x1F0CF),
                Pair("mahjong", 0x1F004),
                Pair("flower_playing_cards", 0x1F3B4),
                Pair("mute", 0x1F507),
                Pair("speaker", 0x1F508),
                Pair("sound", 0x1F509),
                Pair("loud_sound", 0x1F50A),
                Pair("loudspeaker", 0x1F4E2),
                Pair("mega", 0x1F4E3),
                Pair("postal_horn", 0x1F4EF),
                Pair("bell", 0x1F514),
                Pair("no_bell", 0x1F515),
                Pair("musical_note", 0x1F3B5),
                Pair("notes", 0x1F3B6),
                Pair("atm", 0x1F3E7),
                Pair("put_litter_in_its_place", 0x1F6AE),
                Pair("potable_water", 0x1F6B0),
                Pair("wheelchair", 0x267F),
                Pair("mens", 0x1F6B9),
                Pair("womens", 0x1F6BA),
                Pair("restroom", 0x1F6BB),
                Pair("baby_symbol", 0x1F6BC),
                Pair("wc", 0x1F6BE),
                Pair("warning", 0x26A0),
                Pair("children_crossing", 0x1F6B8),
                Pair("no_entry", 0x26D4),
                Pair("no_entry_sign", 0x1F6AB),
                Pair("no_bicycles", 0x1F6B3),
                Pair("no_smoking", 0x1F6AD),
                Pair("do_not_litter", 0x1F6AF),
                Pair("non-potable_water", 0x1F6B1),
                Pair("no_pedestrians", 0x1F6B7),
                Pair("underage", 0x1F51E),
                Pair("arrow_up", 0x2B06),
                Pair("arrow_upper_right", 0x2197),
                Pair("arrow_right", 0x27A1),
                Pair("arrow_lower_right", 0x2198),
                Pair("arrow_down", 0x2B07),
                Pair("arrow_lower_left", 0x2199),
                Pair("arrow_left", 0x2B05),
                Pair("arrow_upper_left", 0x2196),
                Pair("arrow_up_down", 0x2195),
                Pair("left_right_arrow", 0x2194),
                Pair("leftwards_arrow_with_hook", 0x21A9),
                Pair("arrow_right_hook", 0x21AA),
                Pair("arrow_heading_up", 0x2934),
                Pair("arrow_heading_down", 0x2935),
                Pair("arrows_clockwise", 0x1F503),
                Pair("arrows_counterclockwise", 0x1F504),
                Pair("back", 0x1F519),
                Pair("end", 0x1F51A),
                Pair("on", 0x1F51B),
                Pair("soon", 0x1F51C),
                Pair("top", 0x1F51D),
                Pair("six_pointed_star", 0x1F52F),
                Pair("aries", 0x2648),
                Pair("taurus", 0x2649),
                Pair("gemini", 0x264A),
                Pair("cancer", 0x264B),
                Pair("leo", 0x264C),
                Pair("virgo", 0x264D),
                Pair("libra", 0x264E),
                Pair("scorpius", 0x264F),
                Pair("sagittarius", 0x2650),
                Pair("capricorn", 0x2651),
                Pair("aquarius", 0x2652),
                Pair("pisces", 0x2653),
                Pair("ophiuchus", 0x26CE),
                Pair("twisted_rightwards_arrows", 0x1F500),
                Pair("repeat", 0x1F501),
                Pair("repeat_one", 0x1F502),
                Pair("arrow_forward", 0x25B6),
                Pair("fast_forward", 0x23E9),
                Pair("arrow_backward", 0x25C0),
                Pair("rewind", 0x23EA),
                Pair("arrow_up_small", 0x1F53C),
                Pair("arrow_double_up", 0x23EB),
                Pair("arrow_down_small", 0x1F53D),
                Pair("arrow_double_down", 0x23EC),
                Pair("cinema", 0x1F3A6),
                Pair("low_brightness", 0x1F505),
                Pair("high_brightness", 0x1F506),
                Pair("signal_strength", 0x1F4F6),
                Pair("vibration_mode", 0x1F4F3),
                Pair("mobile_phone_off", 0x1F4F4),
                Pair("recycle", 0x267B),
                Pair("trident", 0x1F531),
                Pair("name_badge", 0x1F4DB),
                Pair("beginner", 0x1F530),
                Pair("o", 0x2B55),
                Pair("white_check_mark", 0x2705),
                Pair("ballot_box_with_check", 0x2611),
                Pair("heavy_check_mark", 0x2714),
                Pair("heavy_multiplication_x", 0x2716),
                Pair("x", 0x274C),
                Pair("negative_squared_cross_mark", 0x274E),
                Pair("heavy_plus_sign", 0x2795),
                Pair("heavy_minus_sign", 0x2796),
                Pair("heavy_division_sign", 0x2797),
                Pair("curly_loop", 0x27B0),
                Pair("loop", 0x27BF),
                Pair("part_alternation_mark", 0x303D),
                Pair("eight_spoked_asterisk", 0x2733),
                Pair("eight_pointed_black_star", 0x2734),
                Pair("sparkle", 0x2747),
                Pair("bangbang", 0x203C),
                Pair("interrobang", 0x2049),
                Pair("question", 0x2753),
                Pair("grey_question", 0x2754),
                Pair("grey_exclamation", 0x2755),
                Pair("exclamation", 0x2757),
                Pair("copyright", 0xA9),
                Pair("registered", 0xAE),
                Pair("tm", 0x2122),
                Pair("hash", 0x23),
                Pair("zero", 0xFE0F),
                Pair("one", 0xFE0F),
                Pair("two", 0xFE0F),
                Pair("three", 0xFE0F),
                Pair("four", 0xFE0F),
                Pair("five", 0xFE0F),
                Pair("six", 0xFE0F),
                Pair("seven", 0xFE0F),
                Pair("eight", 0xFE0F),
                Pair("nine", 0xFE0F),
                Pair("keycap_ten", 0x1F51F),
                Pair("100", 0x1F4AF),
                Pair("capital_abcd", 0x1F520),
                Pair("abcd", 0x1F521),
                Pair("1234", 0x1F522),
                Pair("symbols", 0x1F523),
                Pair("abc", 0x1F524),
                Pair("a", 0x1F170),
                Pair("ab", 0x1F18E),
                Pair("b", 0x1F171),
                Pair("cl", 0x1F191),
                Pair("cool", 0x1F192),
                Pair("free", 0x1F193),
                Pair("information_source", 0x2139),
                Pair("id", 0x1F194),
                Pair("m", 0x24C2),
                Pair("new", 0x1F195),
                Pair("ng", 0x1F196),
                Pair("o2", 0x1F17E),
                Pair("ok", 0x1F197),
                Pair("parking", 0x1F17F),
                Pair("sos", 0x1F198),
                Pair("up", 0x1F199),
                Pair("vs", 0x1F19A),
                Pair("koko", 0x1F201),
                Pair("sa", 0x1F202),
                Pair("u6708", 0x1F237),
                Pair("u6709", 0x1F236),
                Pair("u6307", 0x1F22F),
                Pair("ideograph_advantage", 0x1F250),
                Pair("u5272", 0x1F239),
                Pair("u7121", 0x1F21A),
                Pair("u7981", 0x1F232),
                Pair("accept", 0x1F251),
                Pair("u7533", 0x1F238),
                Pair("u5408", 0x1F234),
                Pair("u7a7a", 0x1F233),
                Pair("congratulations", 0x3297),
                Pair("secret", 0x3299),
                Pair("u55b6", 0x1F23A),
                Pair("u6e80", 0x1F235),
                Pair("black_small_square", 0x25AA),
                Pair("white_small_square", 0x25AB),
                Pair("white_medium_square", 0x25FB),
                Pair("black_medium_square", 0x25FC),
                Pair("white_medium_small_square", 0x25FD),
                Pair("black_medium_small_square", 0x25FE),
                Pair("black_large_square", 0x2B1B),
                Pair("white_large_square", 0x2B1C),
                Pair("large_orange_diamond", 0x1F536),
                Pair("large_blue_diamond", 0x1F537),
                Pair("small_orange_diamond", 0x1F538),
                Pair("small_blue_diamond", 0x1F539),
                Pair("small_red_triangle", 0x1F53A),
                Pair("small_red_triangle_down", 0x1F53B),
                Pair("diamond_shape_with_a_dot_inside", 0x1F4A0),
                Pair("black_square_button", 0x1F532),
                Pair("white_square_button", 0x1F533),
                Pair("white_circle", 0x26AA),
                Pair("black_circle", 0x26AB),
                Pair("red_circle", 0x1F534),
                Pair("large_blue_circle", 0x1F535),
                // flags
                // people
                Pair("grinning", 0x1F600),
                Pair("grin", 0x1F601),
                Pair("joy", 0x1F602),
                Pair("smiley", 0x1F603),
                Pair("smile", 0x1F604),
                Pair("sweat_smile", 0x1F605),
                Pair("laughing", 0x1F606),
                Pair("wink", 0x1F609),
                Pair("blush", 0x1F60A),
                Pair("yum", 0x1F60B),
                Pair("sunglasses", 0x1F60E),
                Pair("heart_eyes", 0x1F60D),
                Pair("kissing_heart", 0x1F618),
                Pair("kissing", 0x1F617),
                Pair("kissing_smiling_eyes", 0x1F619),
                Pair("kissing_closed_eyes", 0x1F61A),
                Pair("relaxed", 0x263A),
                Pair("neutral_face", 0x1F610),
                Pair("expressionless", 0x1F611),
                Pair("no_mouth", 0x1F636),
                Pair("smirk", 0x1F60F),
                Pair("persevere", 0x1F623),
                Pair("disappointed_relieved", 0x1F625),
                Pair("open_mouth", 0x1F62E),
                Pair("hushed", 0x1F62F),
                Pair("sleepy", 0x1F62A),
                Pair("tired_face", 0x1F62B),
                Pair("sleeping", 0x1F634),
                Pair("relieved", 0x1F60C),
                Pair("stuck_out_tongue", 0x1F61B),
                Pair("stuck_out_tongue_winking_eye", 0x1F61C),
                Pair("stuck_out_tongue_closed_eyes", 0x1F61D),
                Pair("unamused", 0x1F612),
                Pair("sweat", 0x1F613),
                Pair("pensive", 0x1F614),
                Pair("confused", 0x1F615),
                Pair("astonished", 0x1F632),
                Pair("confounded", 0x1F616),
                Pair("disappointed", 0x1F61E),
                Pair("worried", 0x1F61F),
                Pair("triumph", 0x1F624),
                Pair("cry", 0x1F622),
                Pair("sob", 0x1F62D),
                Pair("frowning", 0x1F626),
                Pair("anguished", 0x1F627),
                Pair("fearful", 0x1F628),
                Pair("weary", 0x1F629),
                Pair("grimacing", 0x1F62C),
                Pair("cold_sweat", 0x1F630),
                Pair("scream", 0x1F631),
                Pair("flushed", 0x1F633),
                Pair("dizzy_face", 0x1F635),
                Pair("rage", 0x1F621),
                Pair("angry", 0x1F620),
                Pair("mask", 0x1F637),
                Pair("innocent", 0x1F607),
                Pair("smiling_imp", 0x1F608),
                Pair("imp", 0x1F47F),
                Pair("japanese_ogre", 0x1F479),
                Pair("japanese_goblin", 0x1F47A),
                Pair("skull", 0x1F480),
                Pair("ghost", 0x1F47B),
                Pair("alien", 0x1F47D),
                Pair("hankey", 0x1F4A9),
                Pair("smiley_cat", 0x1F63A),
                Pair("smile_cat", 0x1F638),
                Pair("joy_cat", 0x1F639),
                Pair("heart_eyes_cat", 0x1F63B),
                Pair("smirk_cat", 0x1F63C),
                Pair("kissing_cat", 0x1F63D),
                Pair("scream_cat", 0x1F640),
                Pair("crying_cat_face", 0x1F63F),
                Pair("pouting_cat", 0x1F63E),
                Pair("baby", 0x1F476),
                Pair("boy", 0x1F466),
                Pair("girl", 0x1F467),
                Pair("man", 0x1F468),
                Pair("woman", 0x1F469),
                Pair("older_man", 0x1F474),
                Pair("older_woman", 0x1F475),
                Pair("cop", 0x1F46E),
                Pair("guardsman", 0x1F482),
                Pair("construction_worker", 0x1F477),
                Pair("princess", 0x1F478),
                Pair("man_with_turban", 0x1F473),
                Pair("man_with_gua_pi_mao", 0x1F472),
                Pair("person_with_blond_hair", 0x1F471),
                Pair("bride_with_veil", 0x1F470),
                Pair("angel", 0x1F47C),
                Pair("santa", 0x1F385),
                Pair("person_frowning", 0x1F64D),
                Pair("person_with_pouting_face", 0x1F64E),
                Pair("no_good", 0x1F645),
                Pair("ok_woman", 0x1F646),
                Pair("information_desk_person", 0x1F481),
                Pair("raising_hand", 0x1F64B),
                Pair("bow", 0x1F647),
                Pair("massage", 0x1F486),
                Pair("haircut", 0x1F487),
                Pair("walking", 0x1F6B6),
                Pair("runner", 0x1F3C3),
                Pair("dancer", 0x1F483),
                Pair("dancers", 0x1F46F),
                Pair("bust_in_silhouette", 0x1F464),
                Pair("busts_in_silhouette", 0x1F465),
                Pair("couple", 0x1F46B),
                Pair("two_men_holding_hands", 0x1F46C),
                Pair("two_women_holding_hands", 0x1F46D),
                Pair("couplekiss", 0x1F48F),
                Pair("couple_with_heart", 0x1F491),
                Pair("family", 0x1F46A),
                Pair("muscle", 0x1F4AA),
                Pair("point_left", 0x1F448),
                Pair("point_right", 0x1F449),
                Pair("point_up", 0x261D),
                Pair("point_up_2", 0x1F446),
                Pair("point_down", 0x1F447),
                Pair("v", 0x270C),
                Pair("hand", 0x270B),
                Pair("ok_hand", 0x1F44C),
                Pair("+1", 0x1F44D),
                Pair("-1", 0x1F44E),
                Pair("fist", 0x270A),
                Pair("facepunch", 0x1F44A),
                Pair("wave", 0x1F44B),
                Pair("clap", 0x1F44F),
                Pair("open_hands", 0x1F450),
                Pair("raised_hands", 0x1F64C),
                Pair("pray", 0x1F64F),
                Pair("nail_care", 0x1F485),
                Pair("ear", 0x1F442),
                Pair("nose", 0x1F443),
                Pair("footprints", 0x1F463),
                Pair("eyes", 0x1F440),
                Pair("tongue", 0x1F445),
                Pair("lips", 0x1F444),
                Pair("kiss", 0x1F48B),
                Pair("eyeglasses", 0x1F453),
                Pair("necktie", 0x1F454),
                Pair("shirt", 0x1F455),
                Pair("jeans", 0x1F456),
                Pair("dress", 0x1F457),
                Pair("kimono", 0x1F458),
                Pair("bikini", 0x1F459),
                Pair("womans_clothes", 0x1F45A),
                Pair("purse", 0x1F45B),
                Pair("handbag", 0x1F45C),
                Pair("pouch", 0x1F45D),
                Pair("school_satchel", 0x1F392),
                Pair("mans_shoe", 0x1F45E),
                Pair("athletic_shoe", 0x1F45F),
                Pair("high_heel", 0x1F460),
                Pair("sandal", 0x1F461),
                Pair("boot", 0x1F462),
                Pair("crown", 0x1F451),
                Pair("womans_hat", 0x1F452),
                Pair("tophat", 0x1F3A9),
                Pair("mortar_board", 0x1F393),
                Pair("lipstick", 0x1F484),
                Pair("ring", 0x1F48D),
                Pair("closed_umbrella", 0x1F302),
                Pair("briefcase", 0x1F4BC),
                // objects
                Pair("bath", 0x1F6C0),
                Pair("love_letter", 0x1F48C),
                Pair("bomb", 0x1F4A3),
                Pair("gem", 0x1F48E),
                Pair("hocho", 0x1F52A),
                Pair("barber", 0x1F488),
                Pair("hourglass", 0x231B),
                Pair("hourglass_flowing_sand", 0x23F3),
                Pair("watch", 0x231A),
                Pair("alarm_clock", 0x23F0),
                Pair("balloon", 0x1F388),
                Pair("tada", 0x1F389),
                Pair("confetti_ball", 0x1F38A),
                Pair("dolls", 0x1F38E),
                Pair("flags", 0x1F38F),
                Pair("wind_chime", 0x1F390),
                Pair("ribbon", 0x1F380),
                Pair("gift", 0x1F381),
                Pair("crystal_ball", 0x1F52E),
                Pair("postal_horn", 0x1F4EF),
                Pair("radio", 0x1F4FB),
                Pair("iphone", 0x1F4F1),
                Pair("calling", 0x1F4F2),
                Pair("phone", 0x260E),
                Pair("telephone_receiver", 0x1F4DE),
                Pair("pager", 0x1F4DF),
                Pair("fax", 0x1F4E0),
                Pair("battery", 0x1F50B),
                Pair("electric_plug", 0x1F50C),
                Pair("computer", 0x1F4BB),
                Pair("minidisc", 0x1F4BD),
                Pair("floppy_disk", 0x1F4BE),
                Pair("cd", 0x1F4BF),
                Pair("dvd", 0x1F4C0),
                Pair("movie_camera", 0x1F3A5),
                Pair("tv", 0x1F4FA),
                Pair("camera", 0x1F4F7),
                Pair("video_camera", 0x1F4F9),
                Pair("vhs", 0x1F4FC),
                Pair("mag", 0x1F50D),
                Pair("mag_right", 0x1F50E),
                Pair("bulb", 0x1F4A1),
                Pair("flashlight", 0x1F526),
                Pair("izakaya_lantern", 0x1F3EE),
                Pair("notebook_with_decorative_cover", 0x1F4D4),
                Pair("closed_book", 0x1F4D5),
                Pair("book", 0x1F4D6),
                Pair("green_book", 0x1F4D7),
                Pair("blue_book", 0x1F4D8),
                Pair("orange_book", 0x1F4D9),
                Pair("books", 0x1F4DA),
                Pair("notebook", 0x1F4D3),
                Pair("page_with_curl", 0x1F4C3),
                Pair("scroll", 0x1F4DC),
                Pair("page_facing_up", 0x1F4C4),
                Pair("newspaper", 0x1F4F0),
                Pair("bookmark_tabs", 0x1F4D1),
                Pair("bookmark", 0x1F516),
                Pair("moneybag", 0x1F4B0),
                Pair("yen", 0x1F4B4),
                Pair("dollar", 0x1F4B5),
                Pair("euro", 0x1F4B6),
                Pair("pound", 0x1F4B7),
                Pair("money_with_wings", 0x1F4B8),
                Pair("credit_card", 0x1F4B3),
                Pair("email", 0x2709),
                Pair("e-mail", 0x1F4E7),
                Pair("incoming_envelope", 0x1F4E8),
                Pair("envelope_with_arrow", 0x1F4E9),
                Pair("outbox_tray", 0x1F4E4),
                Pair("inbox_tray", 0x1F4E5),
                Pair("package", 0x1F4E6),
                Pair("mailbox", 0x1F4EB),
                Pair("mailbox_closed", 0x1F4EA),
                Pair("mailbox_with_mail", 0x1F4EC),
                Pair("mailbox_with_no_mail", 0x1F4ED),
                Pair("postbox", 0x1F4EE),
                Pair("pencil2", 0x270F),
                Pair("black_nib", 0x2712),
                Pair("memo", 0x1F4DD),
                Pair("file_folder", 0x1F4C1),
                Pair("open_file_folder", 0x1F4C2),
                Pair("date", 0x1F4C5),
                Pair("calendar", 0x1F4C6),
                Pair("card_index", 0x1F4C7),
                Pair("chart_with_upwards_trend", 0x1F4C8),
                Pair("chart_with_downwards_trend", 0x1F4C9),
                Pair("bar_chart", 0x1F4CA),
                Pair("clipboard", 0x1F4CB),
                Pair("pushpin", 0x1F4CC),
                Pair("round_pushpin", 0x1F4CD),
                Pair("paperclip", 0x1F4CE),
                Pair("straight_ruler", 0x1F4CF),
                Pair("triangular_ruler", 0x1F4D0),
                Pair("scissors", 0x2702),
                Pair("lock", 0x1F512),
                Pair("unlock", 0x1F513),
                Pair("lock_with_ink_pen", 0x1F50F),
                Pair("closed_lock_with_key", 0x1F510),
                Pair("key", 0x1F511),
                Pair("hammer", 0x1F528),
                Pair("gun", 0x1F52B),
                Pair("wrench", 0x1F527),
                Pair("nut_and_bolt", 0x1F529),
                Pair("link", 0x1F517),
                Pair("microscope", 0x1F52C),
                Pair("telescope", 0x1F52D),
                Pair("satellite", 0x1F4E1),
                Pair("syringe", 0x1F489),
                Pair("pill", 0x1F48A),
                Pair("door", 0x1F6AA),
                Pair("toilet", 0x1F6BD),
                Pair("shower", 0x1F6BF),
                Pair("bathtub", 0x1F6C1),
                Pair("smoking", 0x1F6AC),
                Pair("moyai", 0x1F5FF),
                Pair("potable_water", 0x1F6B0)
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
