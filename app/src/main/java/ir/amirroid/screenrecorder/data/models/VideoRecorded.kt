package ir.amirroid.screenrecorder.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.amirroid.screenrecorder.utils.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = Constants.DB_TABLE_NAME)
data class VideoRecorded(
    @PrimaryKey(true)
    val id: Int = 0,
    val fileName: String,
    val filePath: String,
    val duration: Long,
    val dateAdded: Long
) : Parcelable
