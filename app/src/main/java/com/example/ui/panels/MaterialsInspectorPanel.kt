package com.example.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.StudioViewModel

data class MaterialPresetItem(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val previewColor: Color
)

data class ColorSwatchItem(
    val nameAr: String,
    val nameEn: String,
    val hex: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialsInspectorPanel(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val selectedMat by viewModel.selectedMaterial.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()
    val customColorHex by viewModel.customColorHex.collectAsState()
    val metalness by viewModel.metalness.collectAsState()
    val roughness by viewModel.roughness.collectAsState()
    val emissive by viewModel.customEmissive.collectAsState()

    var hexInputText by remember(customColorHex) { mutableStateOf(customColorHex) }

    val presets = remember {
        listOf(
            MaterialPresetItem("gold", "Gold", "ذهب", Color(0xFFFFD700)),
            MaterialPresetItem("silver", "Silver", "فضة", Color(0xFFC0C0C0)),
            MaterialPresetItem("chrome", "Chrome", "كروم", Color(0xFFE2E8F0)),
            MaterialPresetItem("glass", "Glass", "زجاج", Color(0x99BAE6FD)),
            MaterialPresetItem("neon", "Neon Glow", "نيون", Color(0xFF10B981)),
            MaterialPresetItem("diamond", "Diamond", "الماس", Color(0xFFE0F2FE)),
            MaterialPresetItem("wood", "Wood PBR", "خشب", Color(0xFF92400E)),
            MaterialPresetItem("plastic", "Gloss Plastic", "بلاستيك", Color(0xFFEF4444))
        )
    }

    val colorSwatches = remember {
        listOf(
            ColorSwatchItem("ذهب ملكي", "Gold", "#FFD700", Color(0xFFFFD700)),
            ColorSwatchItem("ياقوت أحمر", "Ruby Red", "#EF4444", Color(0xFFEF4444)),
            ColorSwatchItem("ياقوت أزرق", "Sapphire Blue", "#3B82F6", Color(0xFF3B82F6)),
            ColorSwatchItem("زمرد أخضر", "Emerald", "#10B981", Color(0xFF10B981)),
            ColorSwatchItem("برتقالي غروب", "Sunset Orange", "#F97316", Color(0xFFF97316)),
            ColorSwatchItem("بنفسجي ملوكي", "Royal Purple", "#8B5CF6", Color(0xFF8B5CF6)),
            ColorSwatchItem("وردي وردة", "Magenta Pink", "#EC4899", Color(0xFFEC4899)),
            ColorSwatchItem("فيروزي كوا", "Cyan Turquoise", "#06B6D4", Color(0xFF06B6D4)),
            ColorSwatchItem("أبيض ناصع", "Pure White", "#F8FAFC", Color(0xFFF8FAFC)),
            ColorSwatchItem("تيتانيوم فضي", "Silver Titanium", "#9CA3AF", Color(0xFF9CA3AF)),
            ColorSwatchItem("أسود فاحم", "Onyx Black", "#18181B", Color(0xFF18181B)),
            ColorSwatchItem("ليموني مضيء", "Lime Neon", "#84CC16", Color(0xFF84CC16))
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isArabic) "مكتبة المواد ومنتقي الألوان الشامل" else "Materials & Full Color Palette",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFFFD700)
        )

        // Preset PBR Shaders
        Text(
            text = if (isArabic) "خامات جاهزة (Presets)" else "PBR Material Presets",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().height(160.dp)
        ) {
            items(presets) { mat ->
                val isSelected = selectedMat == mat.id
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFF27272A) else Color(0xFF18181B))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFFFFD700) else Color(0xFF3F3F46),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.setMaterialPreset(mat.id) }
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(mat.previewColor)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isArabic) mat.nameAr else mat.nameEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
        }

        Divider(color = Color(0xFF27272A))

        // Full Spectrum Color Swatches (منتقي جميع الألوان)
        Text(
            text = if (isArabic) "منتقي جميع الألوان (Color Palette)" else "Full Color Palette Selector",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(colorSwatches) { swatch ->
                val isSel = customColorHex.equals(swatch.hex, ignoreCase = true)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) Color(0xFF27272A) else Color(0xFF18181B))
                        .border(
                            width = if (isSel) 2.dp else 1.dp,
                            color = if (isSel) Color(0xFFFFD700) else Color(0xFF3F3F46),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.setCustomMaterialColor(swatch.hex) }
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(swatch.color)
                            .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isArabic) swatch.nameAr else swatch.nameEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
        }

        // Custom Hex Color Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = hexInputText,
                onValueChange = {
                    hexInputText = it
                    if (it.length == 7 && it.startsWith("#")) {
                        viewModel.setCustomMaterialColor(it)
                    }
                },
                label = { Text(if (isArabic) "رمز اللون الهيكس (Hex Code)" else "Custom Hex Code") },
                leadingIcon = { Icon(Icons.Default.Palette, contentDescription = null, tint = Color(0xFFFFD700)) },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFFD700),
                    unfocusedBorderColor = Color(0xFF3F3F46)
                )
            )

            Button(
                onClick = { viewModel.setCustomMaterialColor(hexInputText) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(56.dp)
            ) {
                Icon(Icons.Default.ColorLens, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isArabic) "تطبيق اللون" else "Apply Color", color = Color.Black)
            }
        }

        // Custom PBR Properties Sliders
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isArabic) "اللمعان والنعكاس المعدني (Metalness)" else "Metalness", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Text(String.format("%.2f", metalness), style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
            }
            Slider(
                value = metalness,
                onValueChange = { viewModel.setCustomMaterialColor(metal = it) },
                valueRange = 0.0f..1.0f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD700), activeTrackColor = Color(0xFFFFD700))
            )
        }

        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isArabic) "الخشونة وتشتيت الضوء (Roughness)" else "Roughness", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Text(String.format("%.2f", roughness), style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
            }
            Slider(
                value = roughness,
                onValueChange = { viewModel.setCustomMaterialColor(rough = it) },
                valueRange = 0.0f..1.0f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD700), activeTrackColor = Color(0xFFFFD700))
            )
        }

        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isArabic) "شدة التوهج والإضاءة (Glow / Emissive)" else "Glow Intensity", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Text(String.format("%.2f", emissive), style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
            }
            Slider(
                value = emissive,
                onValueChange = { viewModel.setCustomMaterialColor(emissive = it) },
                valueRange = 0.0f..2.0f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD700), activeTrackColor = Color(0xFFFFD700))
            )
        }
    }
}
