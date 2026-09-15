package com.hisham.fdroidstore.ui
import android.net.Uri
import android.content.Intent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hisham.fdroidstore.data.StoreCategory
import com.hisham.fdroidstore.download.DownloadState
import com.hisham.fdroidstore.model.SearchApp

object AuroraColors {
    val DeepInk = Color(0xFF07191B)
    val DeepSurface = Color(0xFF122B2D)
    val Mint = Color(0xFF55D7C1)
    val MintSoft = Color(0xFF173F40)
    val Paper = Color(0xFFF2FBF8)
    val MutedPaper = Color(0xFFA9C7C1)
    val Border = Color(0xFF2A4A4A)
    val Coral = Color(0xFFE17B82)
}

@Composable
fun StoreHomeScreen(viewModel: StoreViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedApp by viewModel.selectedApp.collectAsState()
    val browseSections by viewModel.browseSections.collectAsState()
    val browseLoading by viewModel.browseLoading.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val updates by viewModel.updates.collectAsState()
    var query by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }

    androidx.compose.runtime.CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = AuroraColors.DeepInk,
            bottomBar = {
                NavigationBar(containerColor = AuroraColors.DeepSurface) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Search, contentDescription = null) },
                        label = { Text("الرئيسية") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.GridView, contentDescription = null) },
                        label = { Text("التصنيفات") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                        label = { Text("المحفوظات") }
                    )
                }
            }
        ) { padding ->
            when (selectedTab) {
                1 -> CategoriesScreen(
                    sections = browseSections,
                    loading = browseLoading,
                    onCategoryClick = { viewModel.openCategory(it) },
                    onAppClick = { viewModel.openApp(it) },
                    modifier = Modifier.padding(padding)
                )
                2 -> FavoritesScreen(
                    favorites = favorites,
                    onAppClick = { viewModel.openApp(it) },
                    onRemove = { viewModel.toggleFavorite(it) },
                    modifier = Modifier.padding(padding)
                )
                else -> HomeContent(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { viewModel.search(query) },
                    uiState = uiState,
                    sections = browseSections,
                    loading = browseLoading,
                    onCategoryClick = { viewModel.openCategory(it) },
                    onAppClick = { viewModel.openApp(it) },
                    onFavorite = { viewModel.toggleFavorite(it) },
                    favorites = favorites,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }

    if (selectedApp != null) {
        AppDetailSheet(viewModel = viewModel, isFavorite = favorites.any { it.packageName == selectedApp?.packageName })
    }
}

@Composable
private fun HomeContent(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    uiState: UiState,
    sections: Map<StoreCategory, List<SearchApp>>,
    loading: Boolean,
    onCategoryClick: (StoreCategory) -> Unit,
    onAppClick: (SearchApp) -> Unit,
    onFavorite: (SearchApp) -> Unit,
    favorites: List<SearchApp>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("AURORA", color = AuroraColors.Paper, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text("متجر التطبيقات الحرة", color = AuroraColors.MutedPaper, fontSize = 10.sp)
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AuroraColors.MintSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✦", color = AuroraColors.Mint, fontSize = 22.sp)
                }
            }
        }
        item {
            Text("مرحبًا بك", color = AuroraColors.Paper, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("ما الذي تريد تثبيته اليوم؟", color = AuroraColors.MutedPaper, fontSize = 13.sp)
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("ابحث عن تطبيق أو ميزة", color = AuroraColors.MutedPaper) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AuroraColors.MutedPaper) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AuroraColors.Mint,
                    unfocusedBorderColor = AuroraColors.Border,
                    focusedTextColor = AuroraColors.Paper,
                    unfocusedTextColor = AuroraColors.Paper
                )
            )
        }
        when (val state = uiState) {
            is UiState.Loading -> item { LoadingBlock() }
            is UiState.Error -> item { ErrorBlock(state.message) }
            is UiState.Results -> {
                item { SectionHeader("نتائج البحث", null) }
                if (state.apps.isEmpty()) item { EmptyBlock("لا توجد نتائج لهذا البحث", query) }
                items(state.apps) { app ->
                    AppRow(app, onClick = { onAppClick(app) }, onFavorite = { onFavorite(app) }, isFavorite = favorites.any { it.packageName == app.packageName })
                }
            }
            UiState.Idle -> {
                item { HeroCard(onBrowse = { sections.keys.firstOrNull()?.let(onCategoryClick) }) }
                item { SectionHeader("التصنيفات", "عرض الكل") }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        items(sections.keys.take(4).toList()) { category ->
                            CategoryPill(category, onClick = { onCategoryClick(category) })
                        }
                    }
                }
                if (loading && sections.isEmpty()) {
                    item { LoadingBlock() }
                } else {
                    sections.entries.take(3).forEach { (category, apps) ->
                        if (apps.isNotEmpty()) {
                            item { SectionHeader(category.title, "عرض الكل", { onCategoryClick(category) }) }
                            items(apps.take(3)) { app ->
                                AppRow(app, onClick = { onAppClick(app) }, onFavorite = { onFavorite(app) }, isFavorite = favorites.any { it.packageName == app.packageName })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroCard(onBrowse: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(listOf(AuroraColors.DeepSurface, Color(0xFF135052))))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text("متجر مفتوح وموثوق", color = AuroraColors.MutedPaper, fontSize = 11.sp)
                Spacer(Modifier.height(9.dp))
                Text("اكتشف تطبيقات\nتحترمك", color = AuroraColors.Paper, fontSize = 27.sp, fontWeight = FontWeight.Bold)
                Text("تطبيقات حرة، بلا تتبع، ومصدرها واضح.", color = AuroraColors.MutedPaper, fontSize = 13.sp)
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = onBrowse,
                    colors = ButtonDefaults.buttonColors(containerColor = AuroraColors.Mint),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("استكشف الآن", color = AuroraColors.DeepInk, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CategoryPill(category: StoreCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(145.dp)
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AuroraColors.DeepSurface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            Icon(Icons.Default.GridView, contentDescription = null, tint = AuroraColors.Mint, modifier = Modifier.size(20.dp))
            Text(category.title, color = AuroraColors.Paper, fontSize = 12.sp, maxLines = 2)
        }
    }
}

@Composable
private fun CategoriesScreen(
    sections: Map<StoreCategory, List<SearchApp>>,
    loading: Boolean,
    onCategoryClick: (StoreCategory) -> Unit,
    onAppClick: (SearchApp) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("استكشف", color = AuroraColors.Mint, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text("التصنيفات", color = AuroraColors.Paper, fontSize = 29.sp, fontWeight = FontWeight.Bold)
            Text("اعثر على تطبيقات حرة مرتبة حسب ما تحتاجه.", color = AuroraColors.MutedPaper, fontSize = 13.sp)
        }
        items(sections.keys.toList()) { category ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onCategoryClick(category) },
                shape = RoundedCornerShape(17.dp),
                colors = CardDefaults.cardColors(containerColor = AuroraColors.DeepSurface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${sections[category]?.size ?: 0} تطبيقات", color = AuroraColors.MutedPaper, fontSize = 11.sp)
                    Text(category.title, color = AuroraColors.Paper, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        if (loading) item { LoadingBlock() }
        sections.values.flatten().takeIf { it.isNotEmpty() }?.let { apps ->
            item { SectionHeader("تطبيقات مقترحة", null) }
            items(apps.take(8)) { app -> AppRow(app, onClick = { onAppClick(app) }) }
        }
    }
}

@Composable
private fun FavoritesScreen(
    favorites: List<SearchApp>,
    onAppClick: (SearchApp) -> Unit,
    onRemove: (SearchApp) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        item {
            Text("مكتبتك", color = AuroraColors.Mint, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text("المحفوظات", color = AuroraColors.Paper, fontSize = 29.sp, fontWeight = FontWeight.Bold)
            Text("التطبيقات التي تريد الرجوع إليها لاحقًا.", color = AuroraColors.MutedPaper, fontSize = 13.sp)
        }
        if (favorites.isEmpty()) {
            item { EmptyBlock("مكتبتك فارغة") }
        } else {
            item { Text("${favorites.size} تطبيق محفوظ", color = AuroraColors.MutedPaper, fontSize = 12.sp) }
            items(favorites) { app ->
                AppRow(app, onClick = { onAppClick(app) }, onFavorite = { onRemove(app) }, isFavorite = true)
            }
        }
    }
}

@Composable
private fun AppRow(
    app: SearchApp,
    onClick: () -> Unit,
    onFavorite: (SearchApp) -> Unit = {},
    isFavorite: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AuroraColors.DeepSurface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(13.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onFavorite(app) }) {
                Icon(
                    if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "حفظ التطبيق",
                    tint = if (isFavorite) AuroraColors.Mint else AuroraColors.MutedPaper
                )
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(app.name, color = AuroraColors.Paper, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(app.summary, color = AuroraColors.MutedPaper, fontSize = 12.sp, maxLines = 2)
                Text("مفتوح المصدر", color = AuroraColors.Mint, fontSize = 10.sp)
            }
            AsyncImage(
                model = app.icon,
                contentDescription = app.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(58.dp).clip(RoundedCornerShape(16.dp))
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AppDetailSheet(viewModel: StoreViewModel, isFavorite: Boolean) {
    val app by viewModel.selectedApp.collectAsState()
    val details by viewModel.packageDetails.collectAsState()
    val downloadState by viewModel.downloadState.collectAsState()
    val current = app ?: return
    val version = details?.suggestedVersion
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = { viewModel.closeDetails() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = AuroraColors.DeepSurface
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.toggleFavorite(current) }) {
                    Icon(if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, contentDescription = null, tint = AuroraColors.Mint)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(current.name, color = AuroraColors.Paper, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                    Text(current.packageName, color = AuroraColors.MutedPaper, fontSize = 11.sp)
                }
                AsyncImage(model = current.icon, contentDescription = current.name, modifier = Modifier.size(78.dp).clip(RoundedCornerShape(22.dp)))
            }
            Spacer(Modifier.height(18.dp))
            Text(current.summary, color = AuroraColors.MutedPaper, fontSize = 15.sp)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://f-droid.org/packages/${current.packageName}/")
                            )
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("الصفحة الرسمية")
                }
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "https://f-droid.org/packages/${current.packageName}/")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "مشاركة التطبيق"))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("مشاركة")
                }
            }
            Spacer(Modifier.height(19.dp))
            if (details == null) {
                CircularProgressIndicator(color = AuroraColors.Mint)
            } else if (version == null) {
                Text("لا توجد إصدارات متاحة", color = AuroraColors.Coral)
            } else {
                Text("أحدث إصدار: ${version.versionName}", color = AuroraColors.Paper, fontWeight = FontWeight.SemiBold)
                if (details?.packages.orEmpty().size > 1) {
                    Spacer(Modifier.height(12.dp))
                    Text("الإصدارات المتاحة", color = AuroraColors.Paper, fontWeight = FontWeight.SemiBold)
                    details?.packages.orEmpty().sortedByDescending { it.versionCode }.take(5).forEach { release ->
                        Text("${release.versionName} (${release.versionCode})", color = AuroraColors.MutedPaper, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                when (val state = downloadState) {
                    is DownloadState.Progress -> {
                        LinearProgressIndicator(progress = state.percent / 100f, color = AuroraColors.Mint, modifier = Modifier.fillMaxWidth())
                        Text("${state.percent}%", color = AuroraColors.MutedPaper)
                    }
                    is DownloadState.Error -> Text("خطأ: ${state.message}", color = AuroraColors.Coral)
                    is DownloadState.Done -> Text("اكتمل التحميل، جارٍ فتح شاشة التثبيت...", color = AuroraColors.Mint)
                    else -> Button(
                        onClick = { viewModel.downloadAndInstall() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AuroraColors.Mint),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = AuroraColors.DeepInk)
                        Spacer(Modifier.width(8.dp))
                        Text("تحميل وتثبيت", color = AuroraColors.DeepInk, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String, action: String?, onAction: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (action != null) {
            TextButton(onClick = { onAction?.invoke() }) { Text(action, color = AuroraColors.Mint, fontSize = 12.sp) }
        } else {
            Spacer(Modifier.width(1.dp))
        }
        Text(title, color = AuroraColors.Paper, fontSize = 19.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LoadingBlock() {
    Box(modifier = Modifier.fillMaxWidth().padding(28.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = AuroraColors.Mint)
    }
}

@Composable
private fun EmptyBlock(message: String, query: String? = null) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = AuroraColors.MutedPaper, fontSize = 14.sp)
        if (!query.isNullOrBlank()) {
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/search?q=${Uri.encode(query)}&c=apps")
                        )
                    )
                }
            ) {
                Text("فتح البحث الرسمي")
            }
        }
    }
}

@Composable
private fun ErrorBlock(message: String) {
    Card(colors = CardDefaults.cardColors(containerColor = AuroraColors.MintSoft), shape = RoundedCornerShape(16.dp)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Wifi, contentDescription = null, tint = AuroraColors.Mint)
            Spacer(Modifier.width(10.dp))
            Text(message, color = AuroraColors.Paper, fontSize = 13.sp)
        }
    }
}