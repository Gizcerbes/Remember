package com.uogames.remembercards.utils

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.media.MediaDataSource
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ObservableMediaPlayer(private val player: MediaPlayer) {

    enum class Status() {
        PLAY, PREPARE, PAUSE, STOP, ERROR
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _progress = MutableStateFlow(0.0)
    val progress = _progress.asStateFlow()

    private val _status = MutableStateFlow(Status.STOP)
    private var _statListener: (Status) -> Unit = {}

    private var observeJob: Job? = null

    init {
        _status.onEach { _statListener(it) }.launchIn(scope)
    }

    fun play(context: Context, uri: Uri) = scope.launch(Dispatchers.IO) {
        play { it.setDataSource(context, uri) }
    }

    fun play(dataSource: MediaDataSource) {
        play { it.setDataSource(dataSource) }
    }

    fun play(context: Context, uri: Uri, animationDrawable: AnimationDrawable) {
        setStatListener {
            when (it) {
                Status.PLAY -> animationDrawable.start()
                else -> {
                    animationDrawable.stop()
                    animationDrawable.selectDrawable(0)
                }
            }
        }
        play(context, uri)
    }

    fun play(dataSource: MediaDataSource, animationDrawable: AnimationDrawable) {
        setStatListener {
            when (it) {
                Status.PLAY -> animationDrawable.start()
                else -> {
                    animationDrawable.stop()
                    animationDrawable.selectDrawable(0)
                }
            }
        }
        play(dataSource)
    }

    private inline fun play(setDataSource: (MediaPlayer) -> Unit) {
        observeJob?.cancel()
        stop()
        player.reset()
        setDataSource(player)
        prepare()
        _status.value = Status.PLAY
        player.start()
        observeJob = scope.launch(Dispatchers.Main) {
            while (player.isPlaying) {
                delay(100)
                _progress.value = player.currentPosition / player.duration.toDouble()
            }
            _progress.value = 1.0
            _status.value = (Status.STOP)
        }
    }

    private fun prepare() {
        try {
            _status.value = (Status.PREPARE)
            player.prepare()
        } catch (e: Exception) {
            _status.value = (Status.ERROR)
        }
    }

    fun stop() {
        _status.value = (Status.STOP)
        player.stop()
    }

    fun pause() {
        _status.value = (Status.PAUSE)
        player.pause()
    }

    fun setStatListener(listener: (Status) -> Unit) {
        stop()
        val oldStat = _statListener
        _statListener = listener
        oldStat(Status.STOP)
    }
}
