package com.hisham.fdroidstore.model

data class AppUpdate(
    val installed: InstalledApp,
    val latest: PackageVersion
)
