package com.example.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.MusicNote
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
fun AudioInspectorPanel(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val isArabic by viewModel.isArabic.collectAsState()
    val selectedTrack by viewModel.selectedAudioTrackName.collectAsState()
    val volume by viewModel.audioVolume.collectAsState()

    val sampleTracks = remember {
        listOf(
            "Cinematic Gold Trailer",
            "Epic 3D Brass Opener",
            "Cyberpunk Synthwave Beats",
            "Arabian Majestic Oud",
            "Ambient Glass Reflections"
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isArabic) "استوديو الصوت والموسيقى التصويرية" else "Audio Production Studio",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFFFD700)
        )

        // Volume Control
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(if (isArabic) "مستوى الصوت" else "Audio Volume", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Text("${(volume * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = Color(0xFFFFD700))
            }
            Slider(
                value = volume,
                onValueChange = { viewModel.audioVolume.value = it },
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD700), activeTrackColor = Color(0xFFFFD700))
            )
        }

        Text(
            text = if (isArabic) "المقاطع الصوتية المتاحة" else "Select Background Track",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            sampleTracks.forEach { track ->
                val isSelected = selectedTrack == track
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFF27272A) else Color(0xFF18181B))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFFFFD700) else Color(0xFF3F3F46),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.selectedAudioTrackName.value = track }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = if (isSelected) Color(0xFFFFD700) else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = track,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) Color(0xFFFFD700) else Color.White
                    )
                }
            }
        }
    }
}
