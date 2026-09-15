package com.hisham.fdroidstore.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hisham.fdroidstore.model.AppUpdate

@Composable
fun UpdatesScreen(
    updates: List<AppUpdate>,
    onRefresh: () -> Unit,
    onUpdate: (AppUpdate) -> Unit,
    onUpdateAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.padding(3.dp))
                    Text("فحص")
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "التحديثات",
                        color = AuroraColors.Mint,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "تحديثات متاحة",
                        color = AuroraColors.Paper,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (updates.isEmpty()) {
            item {
                Text(
                    "لا توجد تحديثات متاحة حاليًا",
                    color = AuroraColors.MutedPaper,
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            }
        } else {
            item {
                Text(
                    "${updates.size} تحديث متاح",
                    color = AuroraColors.MutedPaper,
                    fontSize = 13.sp
                )
            }
            items(updates, key = { it.installed.packageName }) { update ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AuroraColors.DeepSurface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            update.installed.name,
                            color = AuroraColors.Paper,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "المثبت: ${update.installed.versionName}",
                            color = AuroraColors.MutedPaper,
                            fontSize = 12.sp
                        )
                        Text(
                            "الجديد: ${update.latest.versionName}",
                            color = AuroraColors.Mint,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
