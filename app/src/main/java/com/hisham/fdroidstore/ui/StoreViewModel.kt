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
    object Idle : UiState()
    object Loading : UiState()
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

    private val _alternatives = MutableStateFlow<List<AlternativeSuggestion>?>(null)
    val alternatives: StateFlow<List<AlternativeSuggestion>?> = _alternatives.asStateFlow()

    // أقسام الاستعراض الافتراضية (تُعرض عندما لا يوجد بحث نشط)، بأسلوب مشابه لمتجر Play
    private val _browseSections = MutableStateFlow<Map<StoreCategory, List<SearchApp>>>(emptyMap())
    val browseSections: StateFlow<Map<StoreCategory, List<SearchApp>>> = _browseSections.asStateFlow()

    private val _browseLoading = MutableStateFlow(false)
    val browseLoading: StateFlow<Boolean> = _browseLoading.asStateFlow()

    init {
        loadBrowseSections()
    }

    private fun loadBrowseSections() {
        _browseLoading.value = true
        viewModelScope.launch {
            try {
                val results = StoreCategories.all.map { category ->
                    async { category to repository.searchApps(category.searchQuery).take(8) }
                }.awaitAll()
                _browseSections.value = results.toMap()
            } catch (e: Exception) {
                // فشل تحميل الاستعراض الافتراضي ليس خطأ حرجًا؛ البحث اليدوي يبقى متاحًا
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
        // مطابقة محلية فورية مع خريطة البدائل، بدون أي طلب شبكة إضافي
        _alternatives.value = ProprietaryAlternatives.findFor(query)

        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val results = repository.searchApps(query)
                _uiState.value = UiState.Results(results)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "تعذّر الاتصال بمستودع F-Droid")
            }
        }
    }

    /** يبحث مباشرة باسم البديل المقترح عند الضغط عليه */
    fun searchAlternative(suggestion: AlternativeSuggestion) {
        search(suggestion.searchQuery)
    }

    /** يفتح "عرض الكل" لفئة معيّنة من الشاشة الرئيسية */
    fun openCategory(category: StoreCategory) {
        search(category.searchQuery)
    }

    fun openApp(app: SearchApp) {
        _selectedApp.value = app
        _packageDetails.value = null
        viewModelScope.launch {
            try {
                _packageDetails.value = repository.getPackageDetails(app.packageName)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "تعذّر جلب تفاصيل التطبيق")
            }
        }
    }

    fun closeDetails() {
        _selectedApp.value = null
        _packageDetails.value = null
        _downloadState.value = null
    }

    fun downloadAndInstall() {
        val app = _selectedApp.value ?: return
        val details = _packageDetails.value ?: return
        val version = details.suggestedVersion ?: return
        val url = repository.downloadUrlFor(app.packageName, version.versionCode)
        val fileName = "${app.packageName}_${version.versionCode}.apk"

        viewModelScope.launch {
            downloader.download(url, fileName).collect { state ->
                _downloadState.value = state
                if (state is DownloadState.Done) {
                    downloader.requestInstall(state.file)
                }
            }
        }
    }
}
