package com.yugentech.sessions.localization

import androidx.annotation.StringRes
import com.yugentech.sessions.R

/**
 * The languages Sessions ships in. [tag] is the BCP 47 tag the app's locale is set to (and the
 * one listed in `res/xml/locales_config.xml`); [nativeName] is the language's name in itself,
 * which never changes with the app's language, so anyone can find their own in the list;
 * [displayName] is its name in the current app language, shown under it.
 */
enum class AppLanguage(
    val tag: String,
    val nativeName: String,
    @param:StringRes val displayName: Int
) {
    English("en", "English", R.string.language_name_english),
    Spanish("es", "Español", R.string.language_name_spanish),
    Portuguese("pt-BR", "Português (Brasil)", R.string.language_name_portuguese),
    French("fr", "Français", R.string.language_name_french),
    German("de", "Deutsch", R.string.language_name_german),
    Hindi("hi", "हिन्दी", R.string.language_name_hindi),
    Arabic("ar", "العربية", R.string.language_name_arabic),
    Japanese("ja", "日本語", R.string.language_name_japanese),
    Korean("ko", "한국어", R.string.language_name_korean),
    Chinese("zh-CN", "简体中文", R.string.language_name_chinese);

    companion object {
        /** The language a stored or system tag stands for, matching on the language when the region differs. */
        fun fromTag(tag: String?): AppLanguage? {
            if (tag.isNullOrBlank()) return null
            entries.firstOrNull { it.tag.equals(tag, ignoreCase = true) }?.let { return it }
            val language = tag.substringBefore('-').substringBefore('_')
            return entries.firstOrNull { it.tag.substringBefore('-').equals(language, ignoreCase = true) }
        }
    }
}
