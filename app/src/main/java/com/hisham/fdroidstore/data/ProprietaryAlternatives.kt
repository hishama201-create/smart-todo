package com.hisham.fdroidstore.data

/**
 * خريطة يدوية مختصرة: اسم تطبيق تجاري شائع (مغلق المصدر، غير موجود في F-Droid)
 * → بدائل مفتوحة المصدر متاحة فعليًا في مستودع F-Droid.
 *
 * القيم هي أسماء بحث (search terms) تُستخدم لاستدعاء search_apps API،
 * وليست packageName مباشرة، لأن التوفر قد يختلف بمرور الوقت.
 */
object ProprietaryAlternatives {

    private val map: Map<String, List<AlternativeSuggestion>> = mapOf(
        "whatsapp" to listOf(
            AlternativeSuggestion("Element", "دردشة مشفّرة لامركزية (بروتوكول Matrix)", "Element"),
            AlternativeSuggestion("SimpleX Chat", "دردشة بدون رقم هاتف أو معرّف دائم", "SimpleX Chat")
        ),
        "واتساب" to listOf(
            AlternativeSuggestion("Element", "دردشة مشفّرة لامركزية (بروتوكول Matrix)", "Element"),
            AlternativeSuggestion("SimpleX Chat", "دردشة بدون رقم هاتف أو معرّف دائم", "SimpleX Chat")
        ),
        "google maps" to listOf(
            AlternativeSuggestion("OsmAnd", "خرائط وملاحة تعمل بدون إنترنت (OpenStreetMap)", "OsmAnd"),
            AlternativeSuggestion("Organic Maps", "خرائط وملاحة خفيفة تعتمد OpenStreetMap", "Organic Maps")
        ),
        "خرائط جوجل" to listOf(
            AlternativeSuggestion("OsmAnd", "خرائط وملاحة تعمل بدون إنترنت (OpenStreetMap)", "OsmAnd"),
            AlternativeSuggestion("Organic Maps", "خرائط وملاحة خفيفة تعتمد OpenStreetMap", "Organic Maps")
        ),
        "instagram" to listOf(
            AlternativeSuggestion("Fedilab", "عميل لشبكات التواصل اللامركزية (Mastodon وغيرها)", "Fedilab")
        ),
        "facebook" to listOf(
            AlternativeSuggestion("Fedilab", "عميل لشبكات التواصل اللامركزية (Mastodon وغيرها)", "Fedilab")
        ),
        "spotify" to listOf(
            AlternativeSuggestion("Auxio", "مشغّل موسيقى محلي بواجهة حديثة", "Auxio")
        ),
        "youtube" to listOf(
            AlternativeSuggestion("NewPipe", "تصفح ومشاهدة فيديوهات بدون حساب أو إعلانات", "NewPipe")
        ),
        "chrome" to listOf(
            AlternativeSuggestion("Fennec", "نسخة فايرفوكس مبنية من F-Droid فقط", "Fennec")
        ),
        "gmail" to listOf(
            AlternativeSuggestion("FairEmail", "تطبيق بريد إلكتروني يحترم الخصوصية", "FairEmail")
        )
    )

    /**
     * يبحث عن مطابقة (كاملة أو جزئية) لاسم تطبيق تجاري معروف داخل نص البحث.
     * يرجع null إذا لم توجد مطابقة، حتى لا تظهر اقتراحات عشوائية على أي بحث عادي.
     */
    fun findFor(query: String): List<AlternativeSuggestion>? {
        val normalized = query.trim().lowercase()
        if (normalized.isBlank()) return null
        map.forEach { (key, suggestions) ->
            if (normalized == key.lowercase() || normalized.contains(key.lowercase())) {
                return suggestions
            }
        }
        return null
    }
}

data class AlternativeSuggestion(
    val name: String,
    val description: String,
    val searchQuery: String
)
