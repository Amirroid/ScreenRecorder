package ir.amirroid.screenrecorder.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ir.amirroid.screenrecorder.data.models.VideoRecorded
import ir.amirroid.screenrecorder.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface SavedVideoDao {
    @Query("SELECT * FROM ${Constants.DB_TABLE_NAME}")
    fun getAllVideos(): Flow<List<VideoRecorded>>

    @Insert
    suspend fun addVideo(videoRecorded: VideoRecorded)
}