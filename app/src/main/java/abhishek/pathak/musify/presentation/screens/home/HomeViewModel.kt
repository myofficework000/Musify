package abhishek.pathak.musify.presentation.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import abhishek.pathak.musify.data.local.models.AudioItem
import abhishek.pathak.musify.data.repository.MusicRepository
import abhishek.pathak.musify.media_player.service.MusicServiceHandler
import abhishek.pathak.musify.utils.HomeUIState
import abhishek.pathak.musify.utils.HomeUiEvents
import abhishek.pathak.musify.utils.MediaStateEvents
import abhishek.pathak.musify.utils.MusicStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.MINUTES
import java.util.concurrent.TimeUnit.SECONDS

@OptIn(SavedStateHandleSaveableApi::class)
class HomeViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel(), KoinComponent {

    private val musicServiceHandler: MusicServiceHandler by inject<MusicServiceHandler>()
    private val repository: MusicRepository by inject<MusicRepository>()

    private var duration by savedStateHandle.saveable { mutableLongStateOf(0L) }
    var progress by savedStateHandle.saveable { mutableFloatStateOf(0f) }
    private var progressValue by savedStateHandle.saveable { mutableStateOf("00:00") }
    var isMusicPlaying by savedStateHandle.saveable { mutableStateOf(false) }
    var currentSelectedMusic by mutableStateOf(
            AudioItem(
                0L,
                "".toUri(),
                "",
                "",
                0,
                "",
                "",
                null
            )
        )

    var musicList by mutableStateOf(listOf<AudioItem>())

    private val _homeUiState: MutableStateFlow<HomeUIState> =
        MutableStateFlow(HomeUIState.InitialHome)
    val homeUIState: StateFlow<HomeUIState> = _homeUiState.asStateFlow()

    init {
        getMusicData()
    }

    init {
        viewModelScope.launch {
            musicServiceHandler.musicStates.collectLatest { musicStates: MusicStates ->
                when (musicStates) {
                    MusicStates.Initial -> _homeUiState.value = HomeUIState.InitialHome
                    is MusicStates.MediaBuffering -> progressCalculation(musicStates.progress)
                    is MusicStates.MediaPlaying -> isMusicPlaying = musicStates.isPlaying
                    is MusicStates.MediaProgress -> progressCalculation(musicStates.progress)
                    is MusicStates.CurrentMediaPlaying -> {
                        currentSelectedMusic = musicList[musicStates.mediaItemIndex]
                    }

                    is MusicStates.MediaReady -> {
                        duration = musicStates.duration
                        _homeUiState.value = HomeUIState.HomeReady
                    }
                }
            }
        }
    }

    fun onHomeUiEvents(homeUiEvents: HomeUiEvents) = viewModelScope.launch {
        when (homeUiEvents) {
            HomeUiEvents.Backward -> musicServiceHandler.onMediaStateEvents(MediaStateEvents.Backward)
            HomeUiEvents.Forward -> musicServiceHandler.onMediaStateEvents(MediaStateEvents.Forward)
            HomeUiEvents.SeekToNext -> musicServiceHandler.onMediaStateEvents(MediaStateEvents.SeekToNext)
            HomeUiEvents.SeekToPrevious -> musicServiceHandler.onMediaStateEvents(MediaStateEvents.SeekToPrevious)
            is HomeUiEvents.PlayPause -> {
                musicServiceHandler.onMediaStateEvents(
                    MediaStateEvents.PlayPause
                )
            }

            is HomeUiEvents.SeekTo -> {
                musicServiceHandler.onMediaStateEvents(
                    MediaStateEvents.SeekTo,
                    seekPosition = ((duration * homeUiEvents.position) / 100f).toLong()
                )
            }

            is HomeUiEvents.CurrentAudioChanged -> {
                musicServiceHandler.onMediaStateEvents(
                    MediaStateEvents.SelectedMusicChange,
                    selectedMusicIndex = homeUiEvents.index
                )
            }

            is HomeUiEvents.UpdateProgress -> {
                musicServiceHandler.onMediaStateEvents(
                    MediaStateEvents.MediaProgress(
                        homeUiEvents.progress
                    )
                )
                progress = homeUiEvents.progress
            }

        }
    }

    private fun getMusicData() {
        viewModelScope.launch {
            val musicData = repository.getAudioData()
            musicList = musicData
            setMusicItems()
        }
    }

    private fun setMusicItems() {
        musicList.map { audioItem ->
            MediaItem.Builder()
                .setUri(audioItem.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setAlbumArtist(audioItem.artist)
                        .setDisplayTitle(audioItem.title)
                        .setSubtitle(audioItem.displayName)
                        .build()
                )
                .build()
        }.also {
            musicServiceHandler.setMediaItemList(it)
        }
    }

    private fun progressCalculation(currentProgress: Long) {
        progress =
            if (currentProgress > 0) ((currentProgress.toFloat() / duration.toFloat()) * 100f)
            else 0f

        progressValue = formatDurationValue(currentProgress)
    }

    private fun formatDurationValue(duration: Long): String {
        val minutes = MINUTES.convert(duration, MILLISECONDS)
        val seconds = (minutes) - minutes * SECONDS.convert(1, MINUTES)

        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onCleared() {
        viewModelScope.launch {
            musicServiceHandler.onMediaStateEvents(MediaStateEvents.Stop)
        }
        super.onCleared()
    }
}