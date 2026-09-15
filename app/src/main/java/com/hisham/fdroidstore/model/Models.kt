package com.hisham.fdroidstore.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** نتيجة بحث واحدة من search.f-droid.org */
@Serializable
data class SearchApp(
    val name: String,
    val summary: String,
    val icon: String,
    val url: String
) {
    /** استخراج اسم الحزمة (packageName) من رابط صفحة التطبيق */
    val packageName: String
        get() = url.trimEnd('/').substringAfterLast('/')
}

@Serializable
data class SearchResponse(
    val apps: List<SearchApp> = emptyList()
)

/** إصدار واحد من إصدارات الحزمة، من /api/v1/packages/{id} */
@Serializable
data class PackageVersion(
    val versionName: String,
    val versionCode: Long
)

@Serializable
data class PackageDetails(
    val packageName: String,
    val suggestedVersionCode: Long,
    val packages: List<PackageVersion> = emptyList()
) {
    val suggestedVersion: PackageVersion?
        get() = packages.find { it.versionCode == suggestedVersionCode } ?: packages.firstOrNull()
}
