package com.example.i18n

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flag: String
) {
    ENGLISH("en", "English", "English", "🇬🇧"),
    BENGALI("bn", "Bengali", "বাংলা", "🇧🇩"),
    HINDI("hi", "Hindi", "हिंदी", "🇮🇳");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }
    }
}
