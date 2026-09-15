package com.hisham.fdroidstore.data

data class StoreCategory(
    val title: String,
    val searchQuery: String
)

object StoreCategories {
    val all = listOf(
        StoreCategory("الإنترنت والتواصل", "browser"),
        StoreCategory("الأمان والخصوصية", "privacy"),
        StoreCategory("الوسائط المتعددة", "music player"),
        StoreCategory("الأدوات اليومية", "tools"),
        StoreCategory("القراءة والمكتبات", "reader"),
        StoreCategory("الألعاب", "game")
    )
}