package com.hisham.fdroidstore.model

data class DownloadRecord(
    val fileName: String,
    val downloadedAt: Long = System.currentTimeMillis()
)
