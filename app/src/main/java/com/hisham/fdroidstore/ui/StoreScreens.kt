package com.hisham.fdroidstore.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hisham.fdroidstore.download.DownloadState
import com.hisham.fdroidstore.model.SearchApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreHomeScreen(viewModel: StoreViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedApp by viewModel.selectedApp.collectAsState()
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("متجر التطبيقات الحرة (F-Droid)") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                label = { Text("ابحث عن تطبيق مفتوح المصدر...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onSearch = { viewModel.search(query) }
                ),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Search
                )
            )
            Button(
                onClick = { viewModel.search(query) },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) { Text("بحث") }

            when (val state = uiState) {
                is UiState.Idle -> HintText("ابحث عن اسم تطبيق، مثل: أدوات، ملاحظات، متصفح")
                is UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                is UiState.Error -> HintText("خطأ: ${state.message}")
                is UiState.Results -> AppList(state.apps, onClick = { viewModel.openApp(it) })
            }
        }
    }

    if (selectedApp != null) {
        AppDetailSheet(viewModel = viewModel)
    }
}

@Composable
private fun HintText(text: String) {
    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun AppList(apps: List<SearchApp>, onClick: (SearchApp) -> Unit) {
    if (apps.isEmpty()) {
        HintText("لا توجد نتائج")
        return
    }
    LazyColumn {
        items(apps) { app ->
            ListItem(
                headlineContent = { Text(app.name) },
                supportingContent = { Text(app.summary, maxLines = 2) },
                leadingContent = {
                    AsyncImage(
                        model = app.icon,
                        contentDescription = app.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(48.dp)
                    )
                },
                modifier = Modifier.clickable { onClick(app) }
            )
            Divider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppDetailSheet(viewModel: StoreViewModel) {
    val app by viewModel.selectedApp.collectAsState()
    val details by viewModel.packageDetails.collectAsState()
    val downloadState by viewModel.downloadState.collectAsState()
    val current = app ?: return

    ModalBottomSheet(onDismissRequest = { viewModel.closeDetails() }) {
        Column(Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = current.icon,
                    contentDescription = current.name,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(current.name, style = MaterialTheme.typography.titleLarge)
                    Text(current.packageName, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(current.summary)
            Spacer(Modifier.height(16.dp))

            val version = details?.suggestedVersion
            if (details == null) {
                CircularProgressIndicator()
            } else if (version == null) {
                Text("لا توجد إصدارات متاحة")
            } else {
                Text("أحدث إصدار: ${version.versionName}")
                Spacer(Modifier.height(12.dp))

                when (val ds = downloadState) {
                    is DownloadState.Progress -> {
                        LinearProgressIndicator(
                            progress = ds.percent / 100f,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text("${ds.percent}%")
                    }
                    is DownloadState.Error -> Text("خطأ: ${ds.message}")
                    is DownloadState.Done -> Text("اكتمل التحميل، جارٍ فتح شاشة التثبيت...")
                    else -> {
                        Button(onClick = { viewModel.downloadAndInstall() }) {
                            Text("تحميل وتثبيت")
                        }
                    }
                }
            }
        }
    }
}
