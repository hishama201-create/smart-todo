package com.hisham.fdroidstore.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("الإعدادات", color = AuroraColors.Mint, fontSize = 29.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("FDroid Store V11.0", color = AuroraColors.MutedPaper)
        }
        item {
            Text("التثبيت", color = AuroraColors.Paper, fontWeight = FontWeight.Bold)
            Text("السماح بتثبيت التطبيقات التي يتم تنزيلها.", color = AuroraColors.MutedPaper)
            Button(
                onClick = {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            Uri.parse("package:${context.packageName}")
                        )
                    )
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("إعدادات السماح بالتثبيت")
            }
        }
        item {
            Text("مصدر التطبيقات", color = AuroraColors.Paper, fontWeight = FontWeight.Bold)
            Text("التطبيق يستخدم واجهات F-Droid الرسمية.", color = AuroraColors.MutedPaper)
            Button(
                onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://f-droid.org/"))
                    )
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("فتح موقع F-Droid")
            }
        }
    }
}
