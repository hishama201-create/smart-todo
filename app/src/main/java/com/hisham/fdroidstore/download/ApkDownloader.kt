package com.hisham.fdroidstore.download

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

sealed class DownloadState {
    data class Progress(val percent: Int) : DownloadState()
    data class Done(val file: File) : DownloadState()
    data class Error(val message: String) : DownloadState()
}

/**
 * يحمّل ملف APK مباشرة من رابط f-droid.org/repo الرسمي إلى مجلد كاش التطبيق،
 * ثم يطلق نية تثبيت عادية عبر FileProvider (نفس آلية أي متصفح أو مدير تحميلات).
 */
class ApkDownloader(private val context: Context) {

    private val client = OkHttpClient()

    fun download(url: String, fileName: String): Flow<DownloadState> = flow {
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    emit(DownloadState.Error("فشل التحميل: ${response.code}"))
                    return@use
                }
                val body = response.body ?: run {
                    emit(DownloadState.Error("لا توجد بيانات في الاستجابة"))
                    return@use
                }
                val totalBytes = body.contentLength()
                val dir = File(context.cacheDir, "apk_downloads").apply { mkdirs() }
                val outFile = File(dir, fileName)

                body.byteStream().use { input ->
                    FileOutputStream(outFile).use { output ->
                        val buffer = ByteArray(8 * 1024)
                        var bytesRead: Int
                        var totalRead = 0L
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalRead += bytesRead
                            if (totalBytes > 0) {
                                emit(DownloadState.Progress(((totalRead * 100) / totalBytes).toInt()))
                            }
                        }
                    }
                }
                emit(DownloadState.Done(outFile))
            }
        } catch (e: Exception) {
            emit(DownloadState.Error(e.message ?: "خطأ غير معروف"))
        }
    }.flowOn(Dispatchers.IO)

    /** يفتح نية تثبيت الحزمة القياسية لأندرويد لملف APK محمّل */
    fun requestInstall(file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
