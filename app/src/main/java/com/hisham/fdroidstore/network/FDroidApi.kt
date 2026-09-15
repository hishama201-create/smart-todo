package com.hisham.fdroidstore.network

import com.hisham.fdroidstore.model.PackageDetails
import com.hisham.fdroidstore.model.SearchResponse
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * كل الاستدعاءات هنا موجهة حصريًا إلى واجهات F-Droid الرسمية والموثقة:
 * https://f-droid.org/en/docs/All_our_APIs/
 * - بحث: search.f-droid.org
 * - تفاصيل/إصدارات الحزمة: f-droid.org/api/v1/packages
 * لا يوجد أي اتصال بخوادم Google Play أو أي تحايل على مصادقة.
 */
interface FDroidSearchApi {
    @GET("api/search_apps")
    suspend fun search(@Query("q") query: String): SearchResponse
}

interface FDroidPackagesApi {
    @GET("api/v1/packages/{packageName}")
    suspend fun getPackage(@Path("packageName") packageName: String): PackageDetails
}

object FDroidApiClient {

    private val json = Json { ignoreUnknownKeys = true }

    private fun buildRetrofit(baseUrl: String): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val searchApi: FDroidSearchApi by lazy {
        buildRetrofit("https://search.f-droid.org/").create(FDroidSearchApi::class.java)
    }

    val packagesApi: FDroidPackagesApi by lazy {
        buildRetrofit("https://f-droid.org/").create(FDroidPackagesApi::class.java)
    }

    /** رابط تحميل ملف APK مباشرة من مستودع F-Droid الرسمي */
    fun apkDownloadUrl(packageName: String, versionCode: Long): String =
        "https://f-droid.org/repo/${packageName}_$versionCode.apk"
}
