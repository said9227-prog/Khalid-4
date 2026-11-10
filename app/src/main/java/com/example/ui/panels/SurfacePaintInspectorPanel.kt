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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.StudioViewModel

data class BrushTypeItem(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val iconEmoji: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurfacePaintInspectorPanel(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val isArabic by viewModel.isArabic.collectAsState()
    val isPaintEnabled by viewModel.isPaintModeEnabled.collectAsState()
    val activeBrushType by viewModel.activeBrushType.collectAsState()
    val activeBrushTool by viewModel.activeBrushTool.collectAsState()
    val brushRadius by viewModel.brushRadius.collectAsState()
    val brushDensity by viewModel.brushDensity.collectAsState()
    val totalCount by viewModel.totalPaintedCount.collectAsState()

    val brushTypes = remember {
        listOf(
            BrushTypeItem("grass", "Grass Blades", "عشب أخضر 3D", "🌱", Color(0xFF22C55E)),
            BrushTypeItem("trees", "Pine Trees", "أشجار وغابات 3D", "🌲", Color(0xFF15803D)),
            BrushTypeItem("flowers", "Flowers", "زهور وحدائق 3D", "🌸", Color(0xFFEC4899)),
            BrushTypeItem("rocks", "Rocks & Boulders", "صخور وأحجار 3D", "🪨", Color(0xFF78716C)),
            BrushTypeItem("stars", "Glowing Stars", "نجوم ومجسمات مضيئة", "✨", Color(0xFFFBBF24))
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isArabic) "3D Surface Painting Studio (الرسم بالفرشاة)" else "3D Surface Painting Studio (Object Paint)",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFFFD700)
            )

            Text(
                text = if (isArabic) "إجمالي الأجسام: $totalCount" else "Total Instances: $totalCount",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF10B981)
            )
        }

        // Toggle Switch to Enable/Disable 3D Surface Painting Mode
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Brush, contentDescription = null, tint = Color(0xFFFFD700))
                Column {
                    Text(
                        text = if (isArabic) "تشغيل وضع الرسم بالفرشاة 3D (Object Paint)" else "Enable 3D Surface Object Painting",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (isArabic) "اسحب إصبعك على الأرضية لغرس وتوزيع العشب والأشجار فورياً" else "Drag finger across ground floor to paint 3D objects live",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Switch(
                checked = isPaintEnabled,
                onCheckedChange = { viewModel.togglePaintMode(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFFFD700),
                    checkedTrackColor = Color(0xFF3F3F46)
                )
            )
        }

        Divider(color = Color(0xFF27272A))

        // Tool Selector (Paint Brush 🖌️ vs Eraser 🧹)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val isPaint = activeBrushTool == "paint"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isPaint) Color(0xFFFFD700) else Color(0xFF18181B))
                    .clickable { viewModel.setBrushTool("paint") }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Brush, contentDescription = null, tint = if (isPaint) Color.Black else Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isArabic) "فرشاة الرسم 🖌️" else "Paint Brush 🖌️", color = if (isPaint) Color.Black else Color.White, style = MaterialTheme.typography.bodySmall)
                }
            }

            val isErase = activeBrushTool == "erase"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isErase) Color(0xFFEF4444) else Color(0xFF18181B))
                    .clickable { viewModel.setBrushTool("erase") }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoFixNormal, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isArabic) "ممحاة المسح 🧹" else "Eraser Tool 🧹", color = Color.White, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Brush Objects Selection Palette (نوع المجسمات 3D)
        Text(
            text = if (isArabic) "نوع مجسمات الفرشاة (Object Brush Palette):" else "Object Brush Type:",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(brushTypes) { b ->
                val isSel = activeBrushType == b.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) Color(0xFF27272A) else Color(0xFF18181B))
                        .border(
                            width = if (isSel) 2.dp else 1.dp,
                            color = if (isSel) Color(0xFFFFD700) else Color(0xFF3F3F46),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.setBrushType(b.id) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(b.iconEmoji, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) b.nameAr else b.nameEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSel) Color(0xFFFFD700) else Color.White
                        )
                    }
                }
            }
        }

        // Brush Size / Radius Slider (حجم وقطر الفرشاة)
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isArabic) "حجم وقطر الفرشاة (Brush Radius)" else "Brush Radius Size", style = MaterialTheme.typography.bodySmall, color = Color.White)
                Text(String.format("%.1fm", brushRadius), style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
            }
            Slider(
                value = brushRadius,
                onValueChange = { viewModel.setBrushRadius(it) },
                valueRange = 0.5f..8.0f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD700), activeTrackColor = Color(0xFFFFD700))
            )
        }

        // Density Slider (كثافة التوزيع بالفرشاة)
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isArabic) "كثافة التوزيع بالفرشاة (Density)" else "Brush Density Rate", style = MaterialTheme.typography.bodySmall, color = Color.White)
                Text("$brushDensity", style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
            }
            Slider(
                value = brushDensity.toFloat(),
                onValueChange = { viewModel.setBrushDensity(it.toInt()) },
                valueRange = 1.0f..20.0f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD700), activeTrackColor = Color(0xFFFFD700))
            )
        }

        // Instant Scatter & Populate Buttons (توزيع عشوائي آلي 3ds Max Scatter)
        Text(
            text = if (isArabic) "أداة التوزيع العشوائي الشامل (Scatter Generator):" else "Automated Scatter Generator:",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(100, 300, 800, 1500).forEach { count ->
                Button(
                    onClick = { viewModel.scatterSelectedBrush(count) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27272A)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+$count", color = Color(0xFFFFD700), style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Clear Surface Button
        Button(
            onClick = { viewModel.clearPaintedSurface() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isArabic) "مسح وتنظيف السطح بالكامل" else "Clear All Painted Surface Objects")
        }
    }
}
