package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.bridge.WebAppInterface
import com.example.ui.components.StudioWebView
import com.example.ui.panels.*

data class StudioTabItem(
    val tab: StudioTab,
    val nameEn: String,
    val nameAr: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainStudioScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isArabic by viewModel.isArabic.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()

    val webAppInterface = remember {
        WebAppInterface(
            context = context,
            onProgress = { viewModel.onExportProgress(it) },
            onComplete = { viewModel.onExportComplete(it) },
            onTimelineUpdate = { viewModel.seekTimeline(it) },
            onCameraState = { status, count -> viewModel.onCameraRecordingState(status, count) }
        ).apply {
            onObjectSelectedListener = { id, x, y, z ->
                viewModel.selectAsset(id)
                viewModel.assetPosX.value = x
                viewModel.assetPosY.value = y
                viewModel.assetPosZ.value = z
            }
            onSurfacePaintUpdateListener = { count ->
                viewModel.totalPaintedCount.value = count
            }
        }
    }

    val tabs = remember {
        listOf(
            StudioTabItem(StudioTab.TEXT, "Text", "النص", Icons.Default.TextFields),
            StudioTabItem(StudioTab.TERRAIN, "3D Terrain", "البيئة والتضاريس 3D", Icons.Default.Terrain),
            StudioTabItem(StudioTab.SURFACE_PAINT, "3D Paint", "الرسم بالفرشاة 3D", Icons.Default.Brush),
            StudioTabItem(StudioTab.MATERIALS, "Materials", "المواد", Icons.Default.Palette),
            StudioTabItem(StudioTab.LIGHTING, "Lighting", "الإضاءة", Icons.Default.WbSunny),
            StudioTabItem(StudioTab.CAMERA, "Camera", "الكاميرا", Icons.Default.Videocam),
            StudioTabItem(StudioTab.ANIMATION, "Animation", "التحريك", Icons.Default.Movie),
            StudioTabItem(StudioTab.AUDIO, "Audio", "الصوت", Icons.Default.MusicNote),
            StudioTabItem(StudioTab.EXPORT, "Export", "التصدير", Icons.Default.IosShare),
            StudioTabItem(StudioTab.PROJECTS, "Projects", "المشاريع", Icons.Default.Folder)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "KHALID 3D STUDIO",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFFFD700)
                        )
                        Text(
                            text = if (isArabic) "استوديو النصوص والسينما ثلاثي الأبعاد" else "Cinematic 3D Typography Studio",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.toggleLanguage() },
                        modifier = Modifier.testTag("btn_toggle_lang")
                    ) {
                        Text(
                            text = if (isArabic) "EN" else "العربية",
                            color = Color(0xFFFFD700),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121214))
            )
        },
        containerColor = Color(0xFF121214),
        modifier = modifier.windowInsetsPadding(WindowInsets.safeDrawing)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 3D WebGL Viewport Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.1f)
                    .background(Color(0xFF121214))
            ) {
                StudioWebView(
                    modifier = Modifier.fillMaxSize(),
                    webAppInterface = webAppInterface,
                    onWebViewCreated = { webView ->
                        viewModel.webViewRef = webView
                    }
                )

                // Quick Viewport Overlay Controls
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.applyCameraPreset("orbit360") },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xCC1E1E22))
                    ) {
                        Icon(Icons.Default.Cameraswitch, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Studio Tool Selection Tab Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF18181B))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabs) { tabItem ->
                    val isSelected = activeTab == tabItem.tab
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color(0xFFFFD700) else Color(0xFF27272A))
                            .clickable { viewModel.activeTab.value = tabItem.tab }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("tab_${tabItem.tab.name.lowercase()}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = tabItem.icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.Black else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) tabItem.nameAr else tabItem.nameEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) Color.Black else Color.White
                        )
                    }
                }
            }

            // Bottom Inspector Content Area (Vertically Scrollable with Dynamic Weight)
            val panelScrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
                    .background(Color(0xFF1E1E22))
                    .verticalScroll(panelScrollState)
                    .padding(bottom = 24.dp)
            ) {
                when (activeTab) {
                    StudioTab.TEXT -> TextInspectorPanel(viewModel)
                    StudioTab.TERRAIN -> TerrainStudioInspectorPanel(viewModel)
                    StudioTab.SURFACE_PAINT -> SurfacePaintInspectorPanel(viewModel)
                    StudioTab.MATERIALS -> MaterialsInspectorPanel(viewModel)
                    StudioTab.LIGHTING -> LightingInspectorPanel(viewModel)
                    StudioTab.CAMERA -> CameraInspectorPanel(viewModel)
                    StudioTab.ANIMATION -> TimelineInspectorPanel(viewModel)
                    StudioTab.AUDIO -> AudioInspectorPanel(viewModel)
                    StudioTab.EXPORT -> ExportInspectorPanel(viewModel)
                    StudioTab.PROJECTS -> ProjectsInspectorPanel(viewModel)
                }
            }
        }
    }
}
