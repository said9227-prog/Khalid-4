package com.example.ui.panels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ui.StudioViewModel
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LightingInspectorPanel(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isArabic by viewModel.isArabic.collectAsState()
    val groundPreset by viewModel.groundPreset.collectAsState()
    val backdropPreset by viewModel.backdropPreset.collectAsState()
    val stageTheme by viewModel.stageTheme.collectAsState()
    val isTexAnimating by viewModel.isTextureAnimating.collectAsState()
    val renderQuality by viewModel.renderQuality.collectAsState()

    // 3D Light Bulb State
    val lightPosX by viewModel.lightPosX.collectAsState()
    val lightPosY by viewModel.lightPosY.collectAsState()
    val lightPosZ by viewModel.lightPosZ.collectAsState()
    val lightIntensity by viewModel.lightIntensity.collectAsState()
    val lightColorHex by viewModel.lightColorHex.collectAsState()
    val isLightBulbVisible by viewModel.isLightBulbVisible.collectAsState()

    var activeUploadTarget by remember { mutableStateOf("ground") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos)
                val base64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
                val dataUrl = "data:image/jpeg;base64,$base64"
                viewModel.loadCustomTextureFromPhone(activeUploadTarget, dataUrl)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val lightColorSwatches = remember {
        listOf(
            Pair("ذهب شمس", "#FFD700"),
            Pair("أبيض نهار", "#FFFFFF"),
            Pair("برتقالي دافئ", "#F97316"),
            Pair("أزرق سماوي", "#38BDF8"),
            Pair("زمرد نيون", "#10B981"),
            Pair("وردي ملوكي", "#EC4899"),
            Pair("أحمر غروب", "#EF4444")
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = if (isArabic) "إعدادات الإضاءة التفاعلية ثلاثية الأبعاد (3D Light Studio)" else "Interactive 3D Lighting & Stage Setup",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFFFD700)
        )

        // 1. Light Bulb Mesh Toggle Switch (إظهار/إخفاء لمبة الإضاءة فوق العناصر)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFFD700))
                Column {
                    Text(
                        text = if (isArabic) "إظهار لمبة الإضاءة 3D في المسرح" else "Show Interactive 3D Light Bulb",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (isArabic) "تختفي اللمبة تلقائياً أثناء تسجيل الفيديو ويظهر تأثير ضوئها وظلالها فقط" else "Bulb auto-hides during video recording (only light effects exported)",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Switch(
                checked = isLightBulbVisible,
                onCheckedChange = { viewModel.toggleLightBulbMesh(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFFFD700),
                    checkedTrackColor = Color(0xFF3F3F46)
                )
            )
        }

        // 2. Light Position Sliders (موقع لمبة الإضاءة في المسرح X, Y, Z)
        Text(
            text = if (isArabic) "💡 موقع مصدر الإضاءة (Position X, Y, Z):" else "💡 Light Bulb 3D Position (X, Y, Z):",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )

        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isArabic) "موقع اللمبة الأفقي (Position X)" else "Light Position X", style = MaterialTheme.typography.bodySmall, color = Color.White)
                Text(String.format("%.2f", lightPosX), style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
            }
            Slider(
                value = lightPosX,
                onValueChange = { viewModel.updateLightPosition(x = it) },
                valueRange = -10.0f..10.0f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD700), activeTrackColor = Color(0xFFFFD700))
            )
        }

        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isArabic) "ارتفاع اللمبة العمودي (Position Y)" else "Light Height Position Y", style = MaterialTheme.typography.bodySmall, color = Color.White)
                Text(String.format("%.2f", lightPosY), style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
            }
            Slider(
                value = lightPosY,
                onValueChange = { viewModel.updateLightPosition(y = it) },
                valueRange = 0.0f..15.0f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD700), activeTrackColor = Color(0xFFFFD700))
            )
        }

        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isArabic) "موقع اللمبة في العمق (Position Z)" else "Light Depth Position Z", style = MaterialTheme.typography.bodySmall, color = Color.White)
                Text(String.format("%.2f", lightPosZ), style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
            }
            Slider(
                value = lightPosZ,
                onValueChange = { viewModel.updateLightPosition(z = it) },
                valueRange = -10.0f..10.0f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD700), activeTrackColor = Color(0xFFFFD700))
            )
        }

        // 3. Light Intensity Slider
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isArabic) "شدة وقوة الإضاءة (Light Intensity)" else "Light Intensity", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Text(String.format("%.1f", lightIntensity), style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
            }
            Slider(
                value = lightIntensity,
                onValueChange = { viewModel.updateLightIntensity(it) },
                valueRange = 0.0f..10.0f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD700), activeTrackColor = Color(0xFFFFD700))
            )
        }

        // 4. Light Color Palette Swatches
        Text(
            text = if (isArabic) "لون الإضاءة والتوهج (Light Color)" else "Light Emissive Color",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(lightColorSwatches) { swatch ->
                val isSel = lightColorHex.equals(swatch.second, ignoreCase = true)
                val parsedColor = try { Color(android.graphics.Color.parseColor(swatch.second)) } catch (e: Exception) { Color(0xFFFFD700) }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) Color(0xFF27272A) else Color(0xFF18181B))
                        .border(
                            width = if (isSel) 2.dp else 1.dp,
                            color = if (isSel) Color(0xFFFFD700) else Color(0xFF3F3F46),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.updateLightColor(swatch.second) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(parsedColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = swatch.first,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSel) Color(0xFFFFD700) else Color.White
                        )
                    }
                }
            }
        }

        Divider(color = Color(0xFF27272A))

        // Stage Theme Selector (3ds Max White, Dark Studio, Transparent Mode)
        Text(
            text = if (isArabic) "نمط خلفية شاشة العرض" else "Stage Background Mode",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val themes = listOf(
                Pair("3dsmax_white", if (isArabic) "مسرح أبيض" else "3ds Max White"),
                Pair("dark_studio", if (isArabic) "استوديو داكن" else "Dark Studio"),
                Pair("transparent", if (isArabic) "بدون خلفية (شفاف)" else "Transparent")
            )
            themes.forEach { t ->
                val isSel = stageTheme == t.first
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
                        .clickable { viewModel.setStageTheme(t.first) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = t.second,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSel) Color(0xFFFFD700) else Color.White
                    )
                }
            }
        }

        // Custom Phone Texture Upload Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    activeUploadTarget = "ground"
                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27272A)),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isArabic) "صورة أرضية من الهاتف" else "Ground Phone Image", color = Color(0xFFFFD700), style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = {
                    activeUploadTarget = "backdrop"
                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27272A)),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isArabic) "صورة خلفية من الهاتف" else "Backdrop Phone Image", color = Color(0xFFFFD700), style = MaterialTheme.typography.bodySmall)
            }
        }

        // Texture Panning Animation Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.MotionPhotosAuto, contentDescription = null, tint = Color(0xFFFFD700))
                Text(
                    text = if (isArabic) "تحريك الخامات والخلفية تلقائياً" else "Animate & Scroll Textures",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Switch(
                checked = isTexAnimating,
                onCheckedChange = { viewModel.toggleTextureAnimation(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD700), checkedTrackColor = Color(0xFF3F3F46))
            )
        }

        // Ground Texture Preset Selector
        Text(
            text = if (isArabic) "خامة الأرضية الأفقية" else "Ground Surface Preset",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val grounds = listOf(
                Pair("grass", if (isArabic) "عشب أخضر" else "Grass Lawn"),
                Pair("wood_deck", if (isArabic) "أرضية خشب" else "Wood Deck"),
                Pair("marble", if (isArabic) "رخام فاخر" else "Marble Floor"),
                Pair("sand", if (isArabic) "رمال ذهبية" else "Sand Dunes"),
                Pair("3dsmax_grid", if (isArabic) "شبكة 3ds Max" else "White Grid")
            )
            items(grounds) { g ->
                val isSel = groundPreset == g.first
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) Color(0xFF27272A) else Color(0xFF18181B))
                        .border(
                            width = if (isSel) 2.dp else 1.dp,
                            color = if (isSel) Color(0xFFFFD700) else Color(0xFF3F3F46),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.setGroundTexture(g.first) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = g.second,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSel) Color(0xFFFFD700) else Color.White
                    )
                }
            }
        }

        // Viewport Render Quality
        Text(
            text = if (isArabic) "دقة العرض المباشر (لحفظ الحرارة والرامات)" else "Live Viewport Quality",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val qualities = listOf(
                Pair("economy", if (isArabic) "ضعيفة (توفير)" else "Low Draft"),
                Pair("balanced", if (isArabic) "متوسطة" else "Medium"),
                Pair("high", if (isArabic) "عالية" else "High"),
                Pair("ultra", if (isArabic) "فائقة 4K" else "Ultra 4K")
            )
            qualities.forEach { q ->
                val isSel = renderQuality == q.first
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
                        .clickable { viewModel.setRenderQuality(q.first) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = q.second,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSel) Color(0xFFFFD700) else Color.White
                    )
                }
            }
        }
    }
}
