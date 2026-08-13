package com.laskarfkapp.zakathutang.model

import androidx.compose.ui.unit.LayoutDirection

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val layoutDirection: LayoutDirection
) {
    INDONESIAN("in", "Bahasa Indonesia", "Bahasa Indonesia", LayoutDirection.Ltr),
    ARABIC("ar", "العربية", "العربية", LayoutDirection.Rtl),
    ENGLISH("en", "English", "English", LayoutDirection.Ltr);

    companion object {
        fun fromCode(code: String): AppLanguage {
            return values().find { 
                it.code.equals(code, ignoreCase = true) || 
                (code.equals("id", ignoreCase = true) && it == INDONESIAN) 
            } ?: INDONESIAN
        }
    }
}
