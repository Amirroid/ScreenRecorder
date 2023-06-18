package ir.amirroid.screenrecorder.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import ir.amirroid.screenrecorder.data.models.VideoRecorded


@Database(
    entities = [VideoRecorded::class],
    version = 5,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): SavedVideoDao
}