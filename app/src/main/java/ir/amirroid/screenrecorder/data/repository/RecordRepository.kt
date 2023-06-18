package ir.amirroid.screenrecorder.data.repository

import ir.amirroid.screenrecorder.data.db.SavedVideoDao
import ir.amirroid.screenrecorder.data.models.VideoRecorded
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RecordRepository @Inject constructor(private val dao: SavedVideoDao) {
    fun getAllVideos() = dao.getAllVideos()
    suspend fun insertVide(video: VideoRecorded) = dao.addVideo(video)
}