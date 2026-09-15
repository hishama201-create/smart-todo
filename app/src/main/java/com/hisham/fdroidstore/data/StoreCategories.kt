package com.hisham.fdroidstore.data

/**
 * فئات مختارة يدويًا لتنظيم شاشة الاستعراض الرئيسية، كل فئة مرتبطة باستعلام
 * بحث يُستخدم لاستدعاء search_apps الرسمي في F-Droid لجلب تطبيقات ممثلة لها.
 * هذا بديل خفيف عن تحميل index-v2.json الكامل (يتجاوز حجمه عشرات الميجابايت).
 */
data class StoreCategory(
    val title: String,
    val emoji: String,
    val searchQuery: String
)

object StoreCategories {
    val all = listOf(
        StoreCategory("الإنترنت والتواصل", "🌐", "browser"),
        StoreCategory("الأمان والخصوصية", "🔒", "privacy"),
        StoreCategory("الوسائط المتعددة", "🎵", "music player"),
        StoreCategory("الأدوات", "🛠️", "tools"),
        StoreCategory("القراءة والمكتبات", "📚", "reader"),
        StoreCategory("الألعاب", "🎮", "game")
    )
}
