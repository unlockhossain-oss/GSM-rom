package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlayer
import com.example.audio.PlayerState
import com.example.ui.theme.OrangePrimary
import java.util.Locale

@Composable
fun VoicePlayerBar(
    audioPlayer: AudioPlayer,
    voiceFilePath: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val playerState by audioPlayer.playerState.collectAsState()
    val currentPlayingPath by audioPlayer.currentPlayingPath.collectAsState()
    val currentPosMs by audioPlayer.currentPositionMs.collectAsState()
    val totalDurationMs by audioPlayer.totalDurationMs.collectAsState()

    val isThisPlaying = currentPlayingPath == voiceFilePath && playerState == PlayerState.PLAYING
    val isThisPaused = currentPlayingPath == voiceFilePath && playerState == PlayerState.PAUSED

    val progress = if (totalDurationMs > 0 && currentPlayingPath == voiceFilePath) {
        currentPosMs.toFloat() / totalDurationMs.toFloat()
    } else {
        0f
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(OrangePrimary.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(OrangePrimary)
                    .clickable {
                        audioPlayer.play(context, voiceFilePath)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isThisPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play Voice Note",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isThisPlaying) "Playing voice note..." else "Customer Voice Instruction",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (currentPlayingPath == voiceFilePath && totalDurationMs > 0) {
                        Text(
                            text = String.format(
                                Locale.US,
                                "%02d:%02d / %02d:%02d",
                                (currentPosMs / 1000) / 60,
                                (currentPosMs / 1000) % 60,
                                (totalDurationMs / 1000) / 60,
                                (totalDurationMs / 1000) % 60
                            ),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (currentPlayingPath == voiceFilePath) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = OrangePrimary,
                        trackColor = OrangePrimary.copy(alpha = 0.2f),
                    )
                }
            }
        }
    }
}
