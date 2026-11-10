package com.example.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.StudioViewModel

data class TerrainPresetItem(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val emoji: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerrainStudioInspectorPanel(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val isArabic by viewModel.isArabic.collectAsState()
    val activePreset by viewModel.terrainPreset.collectAsState()
    val displacementHeight by viewModel.terrainDisplacementHeight.collectAsState()
    val noiseFreq by viewModel.terrainNoiseFreq.collectAsState()
    val waveSpeed by viewModel.waterWaveSpeed.collectAsState()
    val qualityTier by viewModel.terrainQualityTier.collectAsState()
    val stageTheme by viewModel.stageTheme.collectAsState()

    val presets = remember {
        listOf(
            TerrainPresetItem("grass_meadow", "Grass Meadow", "مرج عشبي طبيعي", "🌱", Color(0xFF22C55E)),
            TerrainPresetItem("desert_dunes", "Desert Dunes", "كثبان صحراوية", "🏜️", Color(0xFFF59E0B)),
            TerrainPresetItem("glacial_snow", "Glacial Snow", "قمم جليدية", "❄️", Color(0xFF38BDF8)),
            TerrainPresetItem("rocky_terrain", "Rocky Mountains", "جبال صخرية", "🪨", Color(0xFF78716C)),
            TerrainPresetItem("forest_ground", "Forest Ground", "أرضية غابة", "🌲", Color(0xFF15803D)),
            TerrainPresetItem("animated_water", "Ocean Water 3D", "سطح مائي متحرك", "🌊", Color(0xFF0284C7)),
            TerrainPresetItem("concrete_platform", "Studio Platform", "منصة استوديو مسطحة", "🏢", Color(0xFF94A3B8))
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Terrain,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isArabic) "استوديو التضاريس والبيئات 3D" else "3D Terrain & Environment Studio",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = if (isArabic) "توليد التضاريس الإجرائية، الكثبان، والمحيطات" else "Procedural Heightmap & Surface Generator",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }

        // 1. Terrain Presets Selection Grid
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF27272A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isArabic) "نوع البيئة والتضاريس 3D" else "Terrain Environment Preset",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFFFD700)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    presets.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { item ->
                                val isSelected = activePreset == item.id
                                Surface(
                                    onClick = { viewModel.setTerrainPreset(item.id) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Color(0xFF3F3F46) else Color(0xFF1E1E22),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, item.color) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(54.dp)
                                        .testTag("preset_${item.id}")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = item.emoji, style = MaterialTheme.typography.titleMedium)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = if (isArabic) item.nameAr else item.nameEn,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // 2. Procedural Heightmap Relief Controls
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF27272A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isArabic) "معايير التضاريس الإجرائية" else "Procedural Heightmap Parameters",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFFFD700)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Terrain Relief Height Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isArabic) "ارتفاع وبروز التضاريس" else "Terrain Displacement Height",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                        Text(
                            text = String.format("%.1fx", displacementHeight),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFD700)
                        )
                    }
                    Slider(
                        value = displacementHeight,
                        onValueChange = { viewModel.setTerrainDisplacementHeight(it) },
                        valueRange = 0.0f..3.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFFD700),
                            activeTrackColor = Color(0xFFFFD700)
                        ),
                        modifier = Modifier.testTag("slider_terrain_height")
                    )
                }

                // Noise Frequency Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isArabic) "تكرار التلال والتعرجات" else "Noise Wave Frequency",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                        Text(
                            text = String.format("%.1fx", noiseFreq),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFD700)
                        )
                    }
                    Slider(
                        value = noiseFreq,
                        onValueChange = { viewModel.setTerrainNoiseFrequency(it) },
                        valueRange = 0.2f..2.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFFD700),
                            activeTrackColor = Color(0xFFFFD700)
                        ),
                        modifier = Modifier.testTag("slider_terrain_noise")
                    )
                }

                // Water Wave Speed (Show only if animated water selected)
                if (activePreset == "animated_water") {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isArabic) "سرعة أمواج المحيط" else "Water Wave Speed",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                            Text(
                                text = String.format("%.1fx", waveSpeed),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF38BDF8)
                            )
                        }
                        Slider(
                            value = waveSpeed,
                            onValueChange = { viewModel.setWaterWaveSpeed(it) },
                            valueRange = 0.1f..3.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF38BDF8),
                                activeTrackColor = Color(0xFF38BDF8)
                            ),
                            modifier = Modifier.testTag("slider_water_speed")
                        )
                    }
                }
            }
        }

        // 3. Performance & Thermal Quality Tier Selector
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF27272A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isArabic) "جودة العرض والأداء (الحفاظ على بطارية الهاتف)" else "Rendering Performance Tier",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFFFD700)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tiers = listOf(
                        Triple("economy", "اقتصادي", "Draft"),
                        Triple("balanced", "متوازن", "Balanced"),
                        Triple("high", "عالي 60fps", "High FPS"),
                        Triple("ultra", "فائق 4K", "Ultra 4K")
                    )

                    tiers.forEach { (tierKey, labelAr, labelEn) ->
                        val isSelected = qualityTier == tierKey
                        Surface(
                            onClick = { viewModel.setTerrainQualityTier(tierKey) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFFFFD700) else Color(0xFF1E1E22),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("tier_$tierKey")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isArabic) labelAr else labelEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Celestial Sky & Space Environment (الفضاء والنجوم والسماء 3D)
        val skyPreset by viewModel.skyPreset.collectAsState()
        val moonPhase by viewModel.moonPhase.collectAsState()
        val starDensity by viewModel.starDensity.collectAsState()
        val cloudSpeed by viewModel.cloudSpeed.collectAsState()
        val starTwinkleSpeed by viewModel.starTwinkleSpeed.collectAsState()

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF27272A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NightsStay,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "الفضاء والسماء ثلاثية الأبعاد (نجوم، قمر، شمس وغيوم)" else "3D Celestial Sky & Space Environment",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFFFFD700)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Sky & Space Preset Selection Cards
                Text(
                    text = if (isArabic) "نمط السماء والبيئة الفضائية" else "Sky & Space Mode",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                val skyModes = listOf(
                    Triple("deep_space", "فضاء مع نجوم ✨", "Deep Space Stars"),
                    Triple("blue_sky_sun", "سماء زرقاء وشمس وغيوم ☀️", "Blue Sky & Sun"),
                    Triple("sunset_golden", "غروب ذهبي سينمائي 🌅", "Golden Sunset"),
                    Triple("night_nebula", "سديم كوني داكن 🔮", "Cosmic Nebula")
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    skyModes.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { (modeKey, labelAr, labelEn) ->
                                val isSelected = skyPreset == modeKey
                                Surface(
                                    onClick = { viewModel.setSkyPreset(modeKey) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0xFFFFD700) else Color(0xFF1E1E22),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .testTag("sky_$modeKey")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (isArabic) labelAr else labelEn,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isSelected) Color.Black else Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Moon Phase Selector (Visible in Space or Night modes)
                if (skyPreset == "deep_space" || skyPreset == "night_nebula") {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (isArabic) "طَوْر وحجم القمر (بدر كامل أو هلال)" else "Moon Phase Option",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val moonPhases = listOf(
                            Triple("full_moon", "قمر كامل بدر 🌕", "Full Moon"),
                            Triple("crescent", "هلال 🌙", "Crescent Moon"),
                            Triple("none", "بدون قمر 🚫", "No Moon")
                        )

                        moonPhases.forEach { (phaseKey, labelAr, labelEn) ->
                            val isSelected = moonPhase == phaseKey
                            Surface(
                                onClick = { viewModel.setMoonPhase(phaseKey) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E1E22),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("moon_$phaseKey")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (isArabic) labelAr else labelEn,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Twinkling Star Density Slider
                    Spacer(modifier = Modifier.height(12.dp))
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isArabic) "كثافة وتلألؤ النجوم ✨" else "Twinkling Stars Density",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                            Text(
                                text = "$starDensity نجمة",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFFD700)
                            )
                        }
                        Slider(
                            value = starDensity.toFloat(),
                            onValueChange = { viewModel.setStarDensity(it.toInt()) },
                            valueRange = 200f..2500f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFFD700),
                                activeTrackColor = Color(0xFFFFD700)
                            ),
                            modifier = Modifier.testTag("slider_star_density")
                        )
                    }
                }

                // Cloud Drift Speed Slider (Visible in Blue Sky or Sunset modes)
                if (skyPreset == "blue_sky_sun" || skyPreset == "sunset_golden") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isArabic) "سرعة حركة وانزلاق الغيوم ☁️" else "3D Cloud Drift Speed",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                            Text(
                                text = String.format("%.2fx", cloudSpeed),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF38BDF8)
                            )
                        }
                        Slider(
                            value = cloudSpeed,
                            onValueChange = { viewModel.setCloudSpeed(it) },
                            valueRange = 0.01f..0.2f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF38BDF8),
                                activeTrackColor = Color(0xFF38BDF8)
                            ),
                            modifier = Modifier.testTag("slider_cloud_speed")
                        )
                    }
                }
            }
        }

        // 5. Studio Background Theme Selector
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF27272A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isArabic) "خلفية ومحيط الاستوديو" else "Studio Stage Background",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFFFD700)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val themes = listOf(
                        Triple("3dsmax_white", "3ds Max فاتح", "Studio White"),
                        Triple("dark_cyber", "داكن سينمائي", "Dark Cyber"),
                        Triple("transparent", "شفاف (بدون خلفية)", "Transparent PNG")
                    )

                    themes.forEach { (themeKey, labelAr, labelEn) ->
                        val isSelected = stageTheme == themeKey
                        Surface(
                            onClick = { viewModel.setStageTheme(themeKey) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFFFFD700) else Color(0xFF1E1E22),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("theme_$themeKey")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isArabic) labelAr else labelEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
