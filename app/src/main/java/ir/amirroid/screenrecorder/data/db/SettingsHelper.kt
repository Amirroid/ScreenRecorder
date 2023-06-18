package ir.amirroid.screenrecorder.data.db

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.screenrecorder.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SettingsHelper @Inject constructor(
    @ApplicationContext val context: Context
) {
    private val Context.preferences: DataStore<Preferences> by preferencesDataStore(Constants.PREFERENCES_NAME)

    object Keys {
        val frameRate = intPreferencesKey(Constants.FRAME_RATE)
        val resolution = intPreferencesKey(Constants.RESOLUTION)
        val quality = intPreferencesKey(Constants.QUALITY)
        val defaultCamera = booleanPreferencesKey(Constants.OPEN_DEFAULT_CAMERA)
    }

    val frameRate = context.preferences.data.map {
        it[Keys.frameRate] ?: 0
    }
    val resolution = context.preferences.data.map {
        it[Keys.resolution] ?: 0
    }
    val quality = context.preferences.data.map {
        it[Keys.quality] ?: 0
    }
    val defaultCamera = context.preferences.data.map {
        it[Keys.defaultCamera] ?: false
    }

    suspend fun setFrameRate(index: Int) {
        context.preferences.edit {
            it[Keys.frameRate] = index
        }
    }

    suspend fun setResolution(index: Int) {
        context.preferences.edit {
            it[Keys.resolution] = index
        }
    }

    suspend fun setQuality(index: Int) {
        context.preferences.edit {
            it[Keys.quality] = index
        }
    }

    suspend fun setCameraDefaultOpen(toggle: Boolean) {
        context.preferences.edit {
            it[Keys.defaultCamera] = toggle
        }
    }
}