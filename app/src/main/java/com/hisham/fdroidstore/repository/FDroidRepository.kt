package com.hisham.fdroidstore.repository

import com.hisham.fdroidstore.model.PackageDetails
import com.hisham.fdroidstore.model.SearchApp
import com.hisham.fdroidstore.network.FDroidApiClient

class FDroidRepository {

    suspend fun searchApps(query: String): List<SearchApp> {
        if (query.isBlank()) return emptyList()
        return FDroidApiClient.searchApi.search(query).apps
    }

    suspend fun getPackageDetails(packageName: String): PackageDetails {
        return FDroidApiClient.packagesApi.getPackage(packageName)
    }

    fun downloadUrlFor(packageName: String, versionCode: Long): String =
        FDroidApiClient.apkDownloadUrl(packageName, versionCode)
}
