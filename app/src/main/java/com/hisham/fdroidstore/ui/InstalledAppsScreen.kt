package com.hisham.fdroidstore.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hisham.fdroidstore.model.InstalledApp

@Composable
fun InstalledAppsScreen(
    apps: List<InstalledApp>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        item {
            Text(
                "التطبيقات المثبتة",
                color = AuroraColors.Mint,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "تطبيقاتي",
                color = AuroraColors.Paper,
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${apps.size} تطبيق على جهازك",
                color = AuroraColors.MutedPaper,
                fontSize = 13.sp
            )
        }

        if (apps.isEmpty()) {
            item {
                Text(
                    "لم يتم العثور على تطبيقات مثبتة",
                    color = AuroraColors.MutedPaper,
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            }
        } else {
            items(apps, key = { it.packageName }) { app ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AuroraColors.DeepSurface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            app.versionName,
                            color = AuroraColors.MutedPaper,
                            fontSize = 12.sp
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                app.name,
                                color = AuroraColors.Paper,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                app.packageName,
                                color = AuroraColors.MutedPaper,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
