package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.audio.AudioPlayer
import com.example.audio.AudioRecorder
import com.example.audio.PlayerState
import com.example.audio.RecordState
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.OrangePrimary
import java.io.File
import java.util.Locale

@Composable
fun VoiceRecorderCard(
    audioRecorder: AudioRecorder,
    audioPlayer: AudioPlayer,
    existingVoicePath: String?,
    onVoiceSaved: (String?, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val recordState by audioRecorder.recordState.collectAsState()
    val elapsedSeconds by audioRecorder.elapsedSeconds.collectAsState()
    val currentRecordedPath by audioRecorder.currentFilePath.collectAsState()

    val playerState by audioPlayer.playerState.collectAsState()
    val currentPlayingPath by audioPlayer.currentPlayingPath.collectAsState()
    val currentPosMs by audioPlayer.currentPositionMs.collectAsState()
    val totalDurationMs by audioPlayer.totalDurationMs.collectAsState()

    var activeVoicePath by remember(existingVoicePath) { mutableStateOf(existingVoicePath) }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            val path = audioRecorder.startRecording(context)
            if (path != null) {
                activeVoicePath = path
            }
        }
    }

    // Pulse animation for recording state
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Note",
                        tint = if (recordState == RecordState.RECORDING) CrimsonAccent else OrangePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Customer Voice Note",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (recordState == RecordState.RECORDING) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CrimsonAccent.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "REC",
                            color = CrimsonAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // State 1: IDLE and NO saved voice
            if (recordState == RecordState.IDLE && activeVoicePath == null) {
                Text(
                    text = "Record customer instructions, deadlines, or problem details directly into this job card.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = {
                        if (hasPermission) {
                            val path = audioRecorder.startRecording(context)
                            if (path != null) {
                                activeVoicePath = path
                            }
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Start Recording Voice", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // State 2: Actively RECORDING or PAUSED
            else if (recordState == RecordState.RECORDING || recordState == RecordState.PAUSED) {
                val formattedTime = String.format(
                    Locale.US,
                    "%02d:%02d",
                    elapsedSeconds / 60,
                    elapsedSeconds % 60
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .scale(if (recordState == RecordState.RECORDING) pulseScale else 1f)
                            .clip(CircleShape)
                            .background(if (recordState == RecordState.RECORDING) CrimsonAccent else OrangePrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formattedTime,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (recordState == RecordState.RECORDING) {
                        OutlinedButton(
                            onClick = { audioRecorder.pauseRecording() },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            modifier = Modifier.height(30.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(Icons.Default.Pause, contentDescription = "Pause", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pause", fontSize = 11.sp)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { audioRecorder.resumeRecording() },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            modifier = Modifier.height(30.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Resume", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Resume", fontSize = 11.sp)
                        }
                    }

                    Button(
                        onClick = {
                            val (savedPath, duration) = audioRecorder.stopRecording()
                            activeVoicePath = savedPath
                            onVoiceSaved(savedPath, duration)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        modifier = Modifier.height(30.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save Note", fontSize = 11.sp)
                    }

                    IconButton(
                        onClick = {
                            audioRecorder.deleteCurrentRecording()
                            activeVoicePath = null
                            onVoiceSaved(null, 0L)
                        },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Cancel & Delete",
                            tint = CrimsonAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // State 3: Voice has been recorded or existing voice present
            else if (activeVoicePath != null) {
                val isThisPlaying = currentPlayingPath == activeVoicePath && playerState == PlayerState.PLAYING

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    activeVoicePath?.let { path ->
                                        audioPlayer.play(context, path)
                                    }
                                },
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(OrangePrimary)
                            ) {
                                Icon(
                                    imageVector = if (isThisPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(
                                    text = if (isThisPlaying) "Playing recording..." else "Voice Note Recorded",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )

                                val durationSec = if (totalDurationMs > 0 && currentPlayingPath == activeVoicePath) {
                                    totalDurationMs / 1000
                                } else {
                                    val f = activeVoicePath?.let { File(it) }
                                    if (f != null && f.exists()) (f.length() / 16000).toInt().coerceAtLeast(2) else 0
                                }

                                Text(
                                    text = String.format(Locale.US, "Duration: ~%02d sec", durationSec),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Re-record
                            IconButton(
                                onClick = {
                                    audioPlayer.stop()
                                    audioRecorder.deleteCurrentRecording()
                                    activeVoicePath = null
                                    onVoiceSaved(null, 0L)
                                    if (hasPermission) {
                                        val path = audioRecorder.startRecording(context)
                                        if (path != null) {
                                            activeVoicePath = path
                                        }
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    Icons.Default.Replay,
                                    contentDescription = "Re-record",
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Delete
                            IconButton(
                                onClick = {
                                    audioPlayer.stop()
                                    audioRecorder.deleteCurrentRecording()
                                    activeVoicePath?.let { path ->
                                        try {
                                            val f = File(path)
                                            if (f.exists()) f.delete()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                    activeVoicePath = null
                                    onVoiceSaved(null, 0L)
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete Voice",
                                    tint = CrimsonAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
