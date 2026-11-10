package com.example.ui.panels

import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.SceneAssetType
import com.example.ui.StudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInspectorPanel(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isArabic by viewModel.isArabic.collectAsState()
    val isTextAnimating by viewModel.isText3DAnimating.collectAsState()
    val sceneAssetsList by viewModel.sceneAssetsList.collectAsState()
    val selectedAssetId by viewModel.selectedAssetId.collectAsState()

    val text by viewModel.currentText.collectAsState()
    val size by viewModel.textSize.collectAsState()
    val depth by viewModel.textDepth.collectAsState()
    val bevel by viewModel.bevelDepth.collectAsState()
    val spacing by viewModel.letterSpacing.collectAsState()

    // 3D Transform states (X, Y, Z)
    val posX by viewModel.assetPosX.collectAsState()
    val posY by viewModel.assetPosY.collectAsState()
    val posZ by viewModel.assetPosZ.collectAsState()

    val rotX by viewModel.assetRotX.collectAsState()
    val rotY by viewModel.assetRotY.collectAsState()
    val rotZ by viewModel.assetRotZ.collectAsState()

    val scaleX by viewModel.assetScaleX.collectAsState()
    val scaleY by viewModel.assetScaleY.collectAsState()
    val scaleZ by viewModel.assetScaleZ.collectAsState()

    val selectedAsset = remember(sceneAssetsList, selectedAssetId) {
        sceneAssetsList.find { it.id == selectedAssetId } ?: sceneAssetsList.firstOrNull()
    }

    var showModelPickerDialog by remember { mutableStateOf(false) }

    // Launcher for picking images from phone gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bytes = inputStream?.readBytes()
                if (bytes != null) {
                    val base64Str = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    val mimeType = context.contentResolver.getType(it) ?: "image/png"
                    val dataUrl = "data:$mimeType;base64,$base64Str"
                    viewModel.addNewImageAsset(base64Data = dataUrl, label = "صورة المعرض")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = if (isArabic) "محرر العناصر والتحريك الشامل 3D (X, Y, Z)" else "Comprehensive 3D Asset & Transform Studio",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFFFD700)
        )

        // Text & Object Auto Rotation Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.MotionPhotosAuto, contentDescription = null, tint = Color(0xFFFFD700))
                Text(
                    text = if (isArabic) "دوران وتدوير العناصر تلقائياً" else "Auto-Rotate 3D Objects",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Switch(
                checked = isTextAnimating,
                onCheckedChange = { viewModel.toggleText3DAnimation(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFFFD700),
                    checkedTrackColor = Color(0xFF3F3F46)
                )
            )
        }

        Divider(color = Color(0xFF27272A))

        // Asset Manager Toolbar (إضافة نص، صورة، نموذج 3D)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isArabic) "العناصر المضافة في المشهد (${sceneAssetsList.size})" else "Scene Assets (${sceneAssetsList.size})",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Add 3D Text Button
                Button(
                    onClick = { viewModel.addNewTextAsset() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.TextFields, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(if (isArabic) "+ نص" else "+ Text", color = Color.Black, style = MaterialTheme.typography.bodySmall)
                }

                // Add 3D Image Button
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(if (isArabic) "+ صورة" else "+ Image", color = Color.Black, style = MaterialTheme.typography.bodySmall)
                }

                // Add 3D Primitive Model Button
                Button(
                    onClick = { showModelPickerDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.ViewInAr, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(if (isArabic) "+ مجسم" else "+ Model", color = Color.Black, style = MaterialTheme.typography.bodySmall)
                }

                // Clone Button
                IconButton(
                    onClick = { viewModel.duplicateSelectedAsset() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFFFFD700))
                }

                // Delete Button
                if (sceneAssetsList.size > 1) {
                    IconButton(
                        onClick = { viewModel.deleteSelectedAsset() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444))
                    }
                }
            }
        }

        // Active Scene Assets Chips Bar
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(sceneAssetsList) { asset ->
                val isSel = asset.id == selectedAssetId
                val icon = when (asset.type) {
                    SceneAssetType.TEXT -> Icons.Default.TextFields
                    SceneAssetType.IMAGE -> Icons.Default.Image
                    SceneAssetType.MODEL -> Icons.Default.ViewInAr
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) Color(0xFF27272A) else Color(0xFF18181B))
                        .border(
                            width = if (isSel) 2.dp else 1.dp,
                            color = if (isSel) Color(0xFFFFD700) else Color(0xFF3F3F46),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.selectAsset(asset.id) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSel) Color(0xFFFFD700) else Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${asset.label}: ${if (asset.text.isNotBlank()) asset.text.take(8) else asset.modelType}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSel) Color(0xFFFFD700) else Color.White
                        )
                    }
                }
            }
        }

        // Selected Asset Specific Configuration Card
        selectedAsset?.let { asset ->
            if (asset.type == SceneAssetType.TEXT) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isArabic) "تعديل النص المحدد: [ ${asset.label} ]" else "Editing Selected Text: [ ${asset.label} ]",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFD700)
                        )

                        OutlinedTextField(
                            value = text,
                            onValueChange = { viewModel.updateTextConfig(text = it) },
                            label = { Text(if (isArabic) "اكتب النص المراد عرضه" else "Type Text Here") },
                            leadingIcon = { Icon(Icons.Default.TextFields, contentDescription = null, tint = Color(0xFFFFD700)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("text_input_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color(0xFF3F3F46),
                                focusedLabelColor = Color(0xFFFFD700)
                            )
                        )
                    }
                }

                // Famous Arabic Phrase Presets
                Text(
                    text = if (isArabic) "عبارات ونصوص عربية جاهزة" else "Famous Arabic Titles",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )

                val famousPhrases = remember {
                    listOf(
                        "بسم الله الرحمن الرحيم",
                        "خالد للإنتاج السينمائي",
                        "استوديو خالد 3D",
                        "رمضان كريم",
                        "عيد مبارك سعيد",
                        "الحمد لله رب العالمين",
                        "افتتاحية سينمائية 3D",
                        "الإبداع والتميز",
                        "KHALID SUPER 3D STUDIO"
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(famousPhrases.size) { index ->
                        val phrase = famousPhrases[index]
                        val isSel = text == phrase
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) Color(0xFF27272A) else Color(0xFF18181B))
                                .border(
                                    width = if (isSel) 2.dp else 1.dp,
                                    color = if (isSel) Color(0xFFFFD700) else Color(0xFF3F3F46),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    val targetSize = if (phrase.length > 20) 1.0f else if (phrase.length > 12) 1.2f else 1.4f
                                    viewModel.updateTextConfig(text = phrase, size = targetSize)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = phrase,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSel) Color(0xFFFFD700) else Color.White
                            )
                        }
                    }
                }
            } else if (asset.type == SceneAssetType.IMAGE) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isArabic) "تعديل صورة 3D [ ${asset.label} ]" else "3D Image Plane [ ${asset.label} ]",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFFFD700)
                            )
                            Text(
                                text = if (isArabic) "يمكنك تغيير الصورة بانتخاب صورة أخرى من الاستوديو" else "Pick a new photo from phone gallery",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isArabic) "اختر صورة" else "Select", color = Color.Black)
                        }
                    }
                }
            } else if (asset.type == SceneAssetType.MODEL) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isArabic) "نوع المجسم 3D الحالي: ${asset.modelType.uppercase()}" else "Current 3D Model: ${asset.modelType.uppercase()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFFFD700)
                        )
                        Text(
                            text = if (isArabic) "اختر خامة المادة من تبويب 'المواد' لإعطاء المجسم مظهراً ذهبياً أو زجاجياً" else "Switch material presets in Materials tab to customize surface look",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Divider(color = Color(0xFF27272A))

        // COMPREHENSIVE 3D TRANSFORM CONTROLS (X, Y, Z POSITION, ROTATION, SCALE)
        Text(
            text = if (isArabic) "أبعاد ومحاور التحكم 3D الشاملة (Position, Rotation, Scale)" else "Full 3D Precision Transforms (X, Y, Z)",
            style = MaterialTheme.typography.titleSmall,
            color = Color(0xFFFFD700)
        )

        // 1. POSITION SLIDERS (X, Y, Z)
        Text(
            text = if (isArabic) "📍 إحداثيات الموقع (Position X, Y, Z):" else "📍 Position Controls (X, Y, Z):",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )

        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isArabic) "الموقع الأفقي (Position X)" else "Position X", style = MaterialTheme.typography.bodySmall, color = Color.White)
                Text(String.format("%.2f", posX), style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
            }
            Slider(
                value = posX,
                onValueChange = { viewModel.updateSelectedAssetTransform(posX = it) },
                valueRange = -8.0f..8.0f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD700), activeTrackColor = Color(0xFFFFD700))
            )
        }

        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isArabic) "الموقع العمودي (Position Y)" else "Position Y", style = MaterialTheme.typography.bodySmall, color = Color.White)
                Text(String.format("%.2f", posY), style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
            }
            Slider(
                value = posY,
                onValueChange = { viewModel.updateSelectedAssetTransform(posY = it) },
                valueRange = -6.0f..6.0f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD700), activeTrackColor = Color(0xFFFFD700))
            )
        }

        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isArabic) "الموقع في العمق (Position Z)" else "Position Z", style = MaterialTheme.typography.bodySmall, color = Color.White)
                Text(String.format("%.2f", posZ), style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
            }
            Slider(
                value = posZ,
                onValueChange = { viewModel.updateSelectedAssetTransform(posZ = it) },
                valueRange = -8.0f..8.0f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD700), activeTrackColor = Color(0xFFFFD700))
            )
        }

        // 2. ROTATION SLIDERS (X, Y, Z IN DEGREES)
        Text(
            text = if (isArabic) "🔄 زوايا الدوران والمالان (Rotation X, Y, Z):" else "🔄 Rotation Angles (X, Y, Z Degrees):",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )

        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isArabic) "الدوران حول X (Tilt Pitch)" else "Rotation X (Pitch)", style = MaterialTheme.typography.bodySmall, color = Color.White)
                Text(String.format("%.0f°", rotX), style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
            }
            Slider(
                value = rotX,
                onValueChange = { viewModel.updateSelectedAssetTransform(rotX = it) },
                valueRange = -180.0f..180.0f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFF38BDF8), activeTrackColor = Color(0xFF38BDF8))
            )
        }

        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isArabic) "الدوران حول Y (Pan Yaw)" else "Rotation Y (Yaw)", style = MaterialTheme.typography.bodySmall, color = Color.White)
                Text(String.format("%.0f°", rotY), style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
            }
            Slider(
                value = rotY,
                onValueChange = { viewModel.updateSelectedAssetTransform(rotY = it) },
                valueRange = -180.0f..180.0f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFF38BDF8), activeTrackColor = Color(0xFF38BDF8))
            )
        }

        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isArabic) "الدوران حول Z (Roll)" else "Rotation Z (Roll)", style = MaterialTheme.typography.bodySmall, color = Color.White)
                Text(String.format("%.0f°", rotZ), style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
            }
            Slider(
                value = rotZ,
                onValueChange = { viewModel.updateSelectedAssetTransform(rotZ = it) },
                valueRange = -180.0f..180.0f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFF38BDF8), activeTrackColor = Color(0xFF38BDF8))
            )
        }

        // 3. SCALE SLIDERS (UNIFORM & PER-AXIS)
        Text(
            text = if (isArabic) "📐 أبعاد التكبير والتصغير (Scale X, Y, Z):" else "📐 Scale & Dimensions (X, Y, Z):",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )

        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isArabic) "التكبير الشامل (Uniform Scale)" else "Uniform Scale", style = MaterialTheme.typography.bodySmall, color = Color.White)
                Text(String.format("%.2fx", scaleX), style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
            }
            Slider(
                value = scaleX,
                onValueChange = { viewModel.updateSelectedAssetTransform(scaleX = it, scaleY = it, scaleZ = it) },
                valueRange = 0.1f..4.0f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFF10B981), activeTrackColor = Color(0xFF10B981))
            )
        }

        if (selectedAsset?.type == SceneAssetType.TEXT) {
            // Text Extrusion Depth
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(if (isArabic) "عمق تجسيم النص (Extrusion Depth)" else "Extrusion Depth", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    Text(String.format("%.2f", depth), style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
                }
                Slider(
                    value = depth,
                    onValueChange = { viewModel.updateTextConfig(depth = it) },
                    valueRange = 0.1f..3.0f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD700), activeTrackColor = Color(0xFFFFD700))
                )
            }

            // Letter Spacing
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(if (isArabic) "تباعد الحروف (Letter Spacing)" else "Letter Spacing", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    Text(String.format("%.2f", spacing), style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
                }
                Slider(
                    value = spacing,
                    onValueChange = { viewModel.updateTextConfig(spacing = it) },
                    valueRange = 0.5f..3.0f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD700), activeTrackColor = Color(0xFFFFD700))
                )
            }
        }
    }

    // Modal Dialog to Select 3D Primitive Model Shape
    if (showModelPickerDialog) {
        val primitives = remember {
            listOf(
                Pair("cube", if (isArabic) "مكعب 3D" else "Cube"),
                Pair("sphere", if (isArabic) "كرة 3D" else "Sphere"),
                Pair("cylinder", if (isArabic) "أسطوانة 3D" else "Cylinder"),
                Pair("cone", if (isArabic) "مخروط 3D" else "Cone"),
                Pair("torus", if (isArabic) "حلقة دائرية (Torus)" else "Torus Ring"),
                Pair("star", if (isArabic) "نجمة / ماسة 3D" else "Star Shield"),
                Pair("pyramid", if (isArabic) "هرم 3D" else "Pyramid")
            )
        }

        AlertDialog(
            onDismissRequest = { showModelPickerDialog = false },
            title = {
                Text(
                    text = if (isArabic) "اختر مجسم 3D لإضافته للمسرح" else "Select 3D Model Shape",
                    color = Color(0xFFFFD700)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    primitives.forEach { (type, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF27272A))
                                .clickable {
                                    viewModel.addNewModelAsset(modelType = type, label = label)
                                    showModelPickerDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ViewInAr, contentDescription = null, tint = Color(0xFFFFD700))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(label, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showModelPickerDialog = false }) {
                    Text(if (isArabic) "إلغاء" else "Cancel", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF18181B)
        )
    }
}
