package com.example.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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

@Composable
fun CameraInspectorPanel(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val isArabic by viewModel.isArabic.collectAsState()
    val isRecording by viewModel.isRecordingCamera.collectAsState()
    val sequence by viewModel.cameraSequence.collectAsState()
    val isCameraPathVisible by viewModel.isCameraPathVisible.collectAsState()

    var selectedAddPreset by remember { mutableStateOf("top") }
    var selectedAddDuration by remember { mutableStateOf(4.0f) }

    val presetNames = remember {
        mapOf(
            "top" to Pair("كاميرا علوية High Top", "Top High Angle"),
            "bottom" to Pair("كاميرا سفلية Low Bottom", "Bottom Low Angle"),
            "left_side" to Pair("جانبية يسار Left Side", "Left Side Angle"),
            "right_side" to Pair("جانبية يمين Right Side", "Right Side Angle"),
            "close_up" to Pair("لقطة قريبة Close-up", "Close-up Shot"),
            "hero_wide" to Pair("لقطة واسعة Hero Wide", "Hero Wide Shot"),
            "orbit360" to Pair("دوران كامل 360°", "360 Orbit Shot"),
            "descent" to Pair("هبوط وتنزيل كاميرا", "Camera Descent"),
            "zoomOut" to Pair("تراجع وتكبير للخارج", "Zoom Out"),
            "zoomIn" to Pair("اقتراب ودخول للداخل", "Zoom In"),
            "droneFlyby" to Pair("تحليق طائرة درون", "Drone Flyby"),
            "craneReveal" to Pair("كشف رافعة Crane", "Crane Reveal")
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
                text = if (isArabic) "مخرج وإخراج حركات الكاميرات المتعددة 3D" else "Multi-Camera Director & Choreographer",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFFFD700)
            )

            val totalDurationSec = sequence.sumOf { it.durationSec.toDouble() }.toFloat()
            Text(
                text = if (isArabic) "إجمالي زمن المشهد: ${totalDurationSec.toInt()}ث" else "Total Duration: ${totalDurationSec.toInt()}s",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF10B981)
            )
        }

        // Toggle Switch to Show/Draw Visual 3D Camera Path Curve in Viewport
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Timeline, contentDescription = null, tint = Color(0xFF38BDF8))
                Column {
                    Text(
                        text = if (isArabic) "رسم وإظهار مسار الكاميرا ثلاثي الأبعاد (3D Path Curve)" else "Draw Visual 3D Camera Path",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (isArabic) "يتم إخفاء مسار الكاميرا واللمبة تلقائياً عند بدء تسجيل الفيديو" else "Path line & light bulb auto-hide during video export",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Switch(
                checked = isCameraPathVisible,
                onCheckedChange = { viewModel.toggleCameraPathCurve(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF38BDF8),
                    checkedTrackColor = Color(0xFF3F3F46)
                )
            )
        }

        Divider(color = Color(0xFF27272A))

        Text(
            text = if (isArabic) "تسلسل المشاهد والكاميرات المضافة (${sequence.size})" else "Active Shot Sequence (${sequence.size})",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )

        // Active Camera Motion Steps Sequence Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(sequence) { index, step ->
                val name = presetNames[step.preset]?.let { if (isArabic) it.first else it.second } ?: step.preset
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E22)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, Color(0xFFFFD700), RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}. $name (${step.durationSec.toInt()}ث)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { viewModel.removeCameraSequenceStep(step.id) }
                        )
                    }
                }
            }
        }

        Text(
            text = if (isArabic) "إضافة زوايا كاميرا وتحديد وقت التنقل بينها:" else "Add Shot Angle & Set Transition Timing:",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )

        // Preset Camera Shots Selector (جهة علوية، جهة سفلية، جانبية، قريبة، واسعة...)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(presetNames.toList()) { p ->
                val isSel = selectedAddPreset == p.first
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSel) Color(0xFFFFD700) else Color(0xFF18181B))
                        .border(1.dp, if (isSel) Color(0xFFFFD700) else Color(0xFF3F3F46), RoundedCornerShape(6.dp))
                        .clickable {
                            selectedAddPreset = p.first
                            viewModel.applyCameraPreset(p.first)
                        }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (isArabic) p.second.first else p.second.second,
                        color = if (isSel) Color.Black else Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Transition Duration Selector (زمن التنقل بالثواني والادقائق)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isArabic) "وقت التنقل للبقاء في هذه الكاميرا:" else "Shot Duration Timing:",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                items(listOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 10.0f, 15.0f, 30.0f, 60.0f)) { dur ->
                    val isSel = selectedAddDuration == dur
                    val label = if (dur >= 60f) "${(dur / 60f).toInt()}m" else "${dur.toInt()}s"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) Color(0xFFFFD700) else Color(0xFF27272A))
                            .clickable { selectedAddDuration = dur }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(label, color = if (isSel) Color.Black else Color.White, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Button(
                onClick = { viewModel.addCameraSequenceStep(selectedAddPreset, selectedAddDuration) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isArabic) "إضافة لقطة" else "Add Shot", color = Color.Black, style = MaterialTheme.typography.bodySmall)
            }
        }

        // Preview & Record Sequence Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.previewCameraSequence() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27272A)),
                modifier = Modifier.weight(1f),
                enabled = !isRecording && sequence.isNotEmpty()
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFFFFD700))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isArabic) "تشغيل ومعاينة المشهد" else "Play Cinematic Scene", color = Color(0xFFFFD700))
            }

            Button(
                onClick = { viewModel.recordCameraSequence() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                modifier = Modifier.weight(1f),
                enabled = !isRecording && sequence.isNotEmpty()
            ) {
                Icon(Icons.Default.FiberManualRecord, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isArabic) "تسجيل المشهد فورا" else "Record Scene Video")
            }
        }
    }
}
