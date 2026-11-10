package com.example.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
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
fun TimelineInspectorPanel(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val isArabic by viewModel.isArabic.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()
    val durationSec by viewModel.durationSec.collectAsState()
    val fpsVal by viewModel.fps.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isArabic) "محرك التحريك والشريط الزمني (Keyframe Timeline)" else "Keyframe Timeline & Animation",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFFFD700)
        )

        // Playback Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.togglePlayPause() },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFFFD700), shape = RoundedCornerShape(24.dp))
                    .testTag("btn_play_pause")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = {
                    if (isPlaying) viewModel.togglePlayPause()
                    viewModel.seekTimeline(0f)
                },
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFF27272A), shape = RoundedCornerShape(22.dp))
                    .testTag("btn_stop_timeline")
            ) {
                Icon(Icons.Default.Stop, contentDescription = null, tint = Color.White)
            }
        }

        // Timeline Scrub Bar
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = String.format("%.1fs / %.1fs", currentTime, durationSec),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFFD700)
                )
                Text(
                    text = "$fpsVal FPS",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
            Slider(
                value = currentTime,
                onValueChange = { viewModel.seekTimeline(it) },
                valueRange = 0f..durationSec,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFFFD700),
                    activeTrackColor = Color(0xFFFFD700)
                ),
                modifier = Modifier.testTag("timeline_slider")
            )
        }

        // Duration & FPS selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(if (isArabic) "مدة المشهد (ثواني)" else "Duration (sec)", style = MaterialTheme.typography.bodySmall, color = Color.White)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(3.0f, 5.0f, 10.0f).forEach { dur ->
                        val isSel = durationSec == dur
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) Color(0xFFFFD700) else Color(0xFF18181B))
                                .clickable { viewModel.durationSec.value = dur }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${dur.toInt()}s", color = if (isSel) Color.Black else Color.White, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(if (isArabic) "معدل الإطارات (FPS)" else "Target FPS", style = MaterialTheme.typography.bodySmall, color = Color.White)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(24, 30, 60).forEach { f ->
                        val isSel = fpsVal == f
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) Color(0xFFFFD700) else Color(0xFF18181B))
                                .clickable { viewModel.fps.value = f }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${f}", color = if (isSel) Color.Black else Color.White, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
