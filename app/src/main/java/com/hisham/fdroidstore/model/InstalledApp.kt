package com.hisham.fdroidstore.model

data class InstalledApp(
    val packageName: String,
    val name: String,
    val versionName: String,
    val versionCode: Long
)
