package com.example.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.SystemClock
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

enum class RecordState {
    IDLE, RECORDING, PAUSED, STOPPED
}

class AudioRecorder(
    private val scope: CoroutineScope
) {
    private var recorder: MediaRecorder? = null
    private var currentOutputFile: File? = null
    private var startTime: Long = 0L
    private var accumulatedDurationMs: Long = 0L
    private var timerJob: Job? = null

    private val _recordState = MutableStateFlow(RecordState.IDLE)
    val recordState: StateFlow<RecordState> = _recordState.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private val _currentFilePath = MutableStateFlow<String?>(null)
    val currentFilePath: StateFlow<String?> = _currentFilePath.asStateFlow()

    fun startRecording(context: Context): String? {
        try {
            val voiceDir = File(context.filesDir, "voices")
            if (!voiceDir.exists()) {
                voiceDir.mkdirs()
            }

            val file = File(voiceDir, "voice_memo_${System.currentTimeMillis()}.m4a")
            currentOutputFile = file

            val mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }

            recorder = mediaRecorder
            startTime = SystemClock.elapsedRealtime()
            accumulatedDurationMs = 0L
            _recordState.value = RecordState.RECORDING
            _currentFilePath.value = file.absolutePath
            _elapsedSeconds.value = 0

            startTimer()
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            stopRecording()
            return null
        }
    }

    fun pauseRecording() {
        if (_recordState.value == RecordState.RECORDING) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    recorder?.pause()
                    accumulatedDurationMs += (SystemClock.elapsedRealtime() - startTime)
                    _recordState.value = RecordState.PAUSED
                    timerJob?.cancel()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resumeRecording() {
        if (_recordState.value == RecordState.PAUSED) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    recorder?.resume()
                    startTime = SystemClock.elapsedRealtime()
                    _recordState.value = RecordState.RECORDING
                    startTimer()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopRecording(): Pair<String?, Long> {
        var duration = accumulatedDurationMs
        if (_recordState.value == RecordState.RECORDING) {
            duration += (SystemClock.elapsedRealtime() - startTime)
        }

        timerJob?.cancel()
        timerJob = null

        try {
            recorder?.apply {
                try {
                    stop()
                } catch (e: Exception) {
                    // ignore if already stopped or no valid data
                }
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            recorder = null
        }

        _recordState.value = RecordState.STOPPED
        val finalPath = currentOutputFile?.absolutePath
        return Pair(finalPath, duration)
    }

    fun reset() {
        timerJob?.cancel()
        timerJob = null
        try {
            recorder?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        recorder = null
        _recordState.value = RecordState.IDLE
        _elapsedSeconds.value = 0
        _currentFilePath.value = null
        currentOutputFile = null
        accumulatedDurationMs = 0L
    }

    fun deleteCurrentRecording() {
        val path = _currentFilePath.value
        if (path != null) {
            try {
                val f = File(path)
                if (f.exists()) {
                    f.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        reset()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch(Dispatchers.Main) {
            while (isActive && _recordState.value == RecordState.RECORDING) {
                val currentTotalMs = accumulatedDurationMs + (SystemClock.elapsedRealtime() - startTime)
                _elapsedSeconds.value = (currentTotalMs / 1000).toInt()
                delay(200)
            }
        }
    }
}
