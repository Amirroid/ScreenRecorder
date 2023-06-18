package ir.amirroid.screenrecorder.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amirroid.screenrecorder.data.models.VideoRecorded
import ir.amirroid.screenrecorder.data.repository.RecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilesViewModel @Inject constructor(
    private val repository: RecordRepository
) : ViewModel() {
    val videos = MutableStateFlow(emptyList<VideoRecorded>())

    init {
        getAllVideos()
    }

    private fun getAllVideos() = viewModelScope.launch {
        repository.getAllVideos().collect {
            videos.value = it
        }
    }
}