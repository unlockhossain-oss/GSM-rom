package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

enum class PlayerState {
    IDLE, PLAYING, PAUSED, COMPLETED, ERROR
}

class AudioPlayer(
    private val scope: CoroutineScope
) {
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null

    private val _playerState = MutableStateFlow(PlayerState.IDLE)
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _currentPlayingPath = MutableStateFlow<String?>(null)
    val currentPlayingPath: StateFlow<String?> = _currentPlayingPath.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0)
    val currentPositionMs: StateFlow<Int> = _currentPositionMs.asStateFlow()

    private val _totalDurationMs = MutableStateFlow(0)
    val totalDurationMs: StateFlow<Int> = _totalDurationMs.asStateFlow()

    fun play(context: Context, filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            _playerState.value = PlayerState.ERROR
            return
        }

        // If already playing this file, toggle pause/play
        if (_currentPlayingPath.value == filePath && mediaPlayer != null) {
            if (_playerState.value == PlayerState.PLAYING) {
                pause()
                return
            } else if (_playerState.value == PlayerState.PAUSED) {
                resume()
                return
            }
        }

        stop()

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(context, Uri.fromFile(file))
                prepare()
                start()
            }

            mediaPlayer = player
            _currentPlayingPath.value = filePath
            _totalDurationMs.value = player.duration
            _currentPositionMs.value = 0
            _playerState.value = PlayerState.PLAYING

            player.setOnCompletionListener {
                _playerState.value = PlayerState.COMPLETED
                _currentPositionMs.value = _totalDurationMs.value
                stopProgressJob()
            }

            player.setOnErrorListener { _, _, _ ->
                _playerState.value = PlayerState.ERROR
                stop()
                true
            }

            startProgressJob()
        } catch (e: Exception) {
            e.printStackTrace()
            _playerState.value = PlayerState.ERROR
            stop()
        }
    }

    fun pause() {
        if (_playerState.value == PlayerState.PLAYING) {
            try {
                mediaPlayer?.pause()
                _playerState.value = PlayerState.PAUSED
                stopProgressJob()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resume() {
        if (_playerState.value == PlayerState.PAUSED) {
            try {
                mediaPlayer?.start()
                _playerState.value = PlayerState.PLAYING
                startProgressJob()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun seekTo(positionMs: Int) {
        try {
            mediaPlayer?.seekTo(positionMs)
            _currentPositionMs.value = positionMs
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        stopProgressJob()
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
        }
        _playerState.value = PlayerState.IDLE
        _currentPlayingPath.value = null
        _currentPositionMs.value = 0
    }

    private fun startProgressJob() {
        stopProgressJob()
        progressJob = scope.launch(Dispatchers.Main) {
            while (isActive && _playerState.value == PlayerState.PLAYING) {
                try {
                    mediaPlayer?.let { player ->
                        if (player.isPlaying) {
                            _currentPositionMs.value = player.currentPosition
                        }
                    }
                } catch (e: Exception) {
                    // Ignore transient exceptions during seek
                }
                delay(100)
            }
        }
    }

    private fun stopProgressJob() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stop()
    }
}
