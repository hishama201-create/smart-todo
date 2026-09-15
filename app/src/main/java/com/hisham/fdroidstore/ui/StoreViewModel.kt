package com.hisham.fdroidstore.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hisham.fdroidstore.data.AlternativeSuggestion
import com.hisham.fdroidstore.data.ProprietaryAlternatives
import com.hisham.fdroidstore.data.StoreCategories
import com.hisham.fdroidstore.data.StoreCategory
import com.hisham.fdroidstore.download.ApkDownloader
import com.hisham.fdroidstore.download.DownloadState
import com.hisham.fdroidstore.model.PackageDetails
import com.hisham.fdroidstore.model.SearchApp
import com.hisham.fdroidstore.repository.FDroidRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    data object Idle : UiState()
    data object Loading : UiState()
    data class Results(val apps: List<SearchApp>) : UiState()
    data class Error(val message: String) : UiState()
}

class StoreViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = FDroidRepository()
    private val downloader = ApkDownloader(app)

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _selectedApp = MutableStateFlow<SearchApp?>(null)
    val selectedApp: StateFlow<SearchApp?> = _selectedApp.asStateFlow()

    private val _packageDetails = MutableStateFlow<PackageDetails?>(null)
    val packageDetails: StateFlow<PackageDetails?> = _packageDetails.asStateFlow()

    private val _downloadState = MutableStateFlow<DownloadState?>(null)
    val downloadState: StateFlow<DownloadState?> = _downloadState.asStateFlow()

    private val _browseSections = MutableStateFlow<Map<StoreCategory, List<SearchApp>>>(emptyMap())
    val browseSections: StateFlow<Map<StoreCategory, List<SearchApp>>> = _browseSections.asStateFlow()

    private val _browseLoading = MutableStateFlow(false)
    val browseLoading: StateFlow<Boolean> = _browseLoading.asStateFlow()

    private val _favorites = MutableStateFlow<List<SearchApp>>(emptyList())
    val favorites: StateFlow<List<SearchApp>> = _favorites.asStateFlow()

    private val _alternatives = MutableStateFlow<List<AlternativeSuggestion>?>(null)
    val alternatives: StateFlow<List<AlternativeSuggestion>?> = _alternatives.asStateFlow()

    init {
        loadBrowseSections()
    }

    private fun loadBrowseSections() {
        _browseLoading.value = true
        viewModelScope.launch {
            try {
                _browseSections.value = StoreCategories.all.map { category ->
                    async { category to repository.searchApps(category.searchQuery).take(8) }
                }.awaitAll().toMap()
            } finally {
                _browseLoading.value = false
            }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            _uiState.value = UiState.Idle
            _alternatives.value = null
            return
        }
        _alternatives.value = ProprietaryAlternatives.findFor(query)
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Results(repository.searchApps(query))
            } catch (error: Exception) {
                _uiState.value = UiState.Error(error.message ?: "تعذّر الاتصال بمستودع F-Droid")
            }
        }
    }

    fun openCategory(category: StoreCategory) = search(category.searchQuery)

    fun openApp(app: SearchApp) {
        _selectedApp.value = app
        _packageDetails.value = null
        viewModelScope.launch {
            try {
                _packageDetails.value = repository.getPackageDetails(app.packageName)
            } catch (error: Exception) {
                _uiState.value = UiState.Error(error.message ?: "تعذّر جلب تفاصيل التطبيق")
            }
        }
    }

    fun toggleFavorite(app: SearchApp) {
        _favorites.value = if (_favorites.value.any { it.packageName == app.packageName }) {
            _favorites.value.filterNot { it.packageName == app.packageName }
        } else {
            listOf(app) + _favorites.value
        }
    }

    fun closeDetails() {
        _selectedApp.value = null
        _packageDetails.value = null
        _downloadState.value = null
    }

    fun downloadAndInstall() {
        val app = _selectedApp.value ?: return
        val version = _packageDetails.value?.suggestedVersion ?: return
        viewModelScope.launch {
            downloader.download(
                repository.downloadUrlFor(app.packageName, version.versionCode),
                "${app.packageName}_${version.versionCode}.apk"
            ).collect { state ->
                _downloadState.value = state
                if (state is DownloadState.Done) downloader.requestInstall(state.file)
            }
        }
    }
}