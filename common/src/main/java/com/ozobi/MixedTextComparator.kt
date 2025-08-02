package com.ozobi

import java.text.Collator
import java.util.Locale

open class MixedTextComparator:Comparator<String> {
    private val chineseCollator = Collator.getInstance(Locale.CHINA).apply {
        strength = Collator.PRIMARY
    }

    override fun compare(a: String, b: String): Int {
        val isAChinese = a.all { it.isLetter() && it.isCJK() }
        val isBChinese = b.all { it.isLetter() && it.isCJK() }
        return when {
            isAChinese && !isBChinese -> 1
            !isAChinese && isBChinese -> -1
            isAChinese && isBChinese -> chineseCollator.compare(a, b)
            else -> a.compareTo(b, ignoreCase = true)
        }
    }
    private fun Char.isCJK(): Boolean {
        val unicodeBlock = Character.UnicodeBlock.of(this)
        return unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
                unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
                unicodeBlock == Character.UnicodeBlock.CJK_COMPATIBILITY
    }
}