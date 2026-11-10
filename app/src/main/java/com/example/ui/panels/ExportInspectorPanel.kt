package com.example.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.StudioViewModel

@Composable
fun ExportInspectorPanel(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val isArabic by viewModel.isArabic.collectAsState()
    val isExporting by viewModel.isExporting.collectAsState()
    val progress by viewModel.exportProgress.collectAsState()
    val exportedPath by viewModel.exportedFilePath.collectAsState()
    val selectedFormat by viewModel.exportFormat.collectAsState()
    val selectedRes by viewModel.exportResolution.collectAsState()

    val formats = remember {
        listOf(
            Pair("webm", "WebM Video"),
            Pair("mp4", "MP4 Video"),
            Pair("gif", "Animated GIF"),
            Pair("png", "PNG Sequence")
        )
    }

    val resolutions = remember {
        listOf(
            Pair("480p", if (isArabic) "480p (ضعيفة)" else "480p Low"),
            Pair("720p", if (isArabic) "720p (متوسطة)" else "720p Medium"),
            Pair("1080p", if (isArabic) "1080p (عالية)" else "1080p HD"),
            Pair("4K", if (isArabic) "4K (فائقة)" else "4K Ultra")
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isArabic) "استوديو التصدير والرندر السينمائي" else "Video Recording & Export Studio",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFFFD700)
        )

        // Export Format Selector
        Text(
            text = if (isArabic) "صيغة الملف" else "Export Format",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            formats.forEach { fmt ->
                val isSel = selectedFormat == fmt.first
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) Color(0xFF27272A) else Color(0xFF18181B))
                        .border(
                            width = if (isSel) 2.dp else 1.dp,
                            color = if (isSel) Color(0xFFFFD700) else Color(0xFF3F3F46),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.exportFormat.value = fmt.first }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = fmt.second,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSel) Color(0xFFFFD700) else Color.White
                    )
                }
            }
        }

        // Resolution Selector (دقة منخفضة ضعيفة، متوسطة، عالية، فائقة)
        Text(
            text = if (isArabic) "دقة الفيديو والتصدير" else "Video Export Resolution",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            resolutions.forEach { res ->
                val isSel = selectedRes == res.first
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) Color(0xFF27272A) else Color(0xFF18181B))
                        .border(
                            width = if (isSel) 2.dp else 1.dp,
                            color = if (isSel) Color(0xFFFFD700) else Color(0xFF3F3F46),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.exportResolution.value = res.first }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = res.second,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSel) Color(0xFFFFD700) else Color.White
                    )
                }
            }
        }

        // Render Action Card / Progress
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isExporting) {
                    Text(
                        text = if (isArabic) "جاري رندر الفيديو: $progress%" else "Rendering Video: $progress%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFFFD700)
                    )
                    LinearProgressIndicator(
                        progress = progress / 100f,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFFFD700),
                        trackColor = Color(0xFF3F3F46)
                    )
                } else {
                    Button(
                        onClick = { viewModel.startVideoExport() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_export_render")
                    ) {
                        Icon(Icons.Default.Movie, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "بدء التسجيل والتصدير" else "Start Rendering Video",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Black
                        )
                    }
                }

                if (!exportedPath.isNullOrEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "تم حفظ الفيديو بنجاح في الاستوديو!" else "Saved to Movies Gallery!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }
        }
    }
}
