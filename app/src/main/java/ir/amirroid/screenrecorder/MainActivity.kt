package ir.amirroid.screenrecorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import ir.amirroid.screenrecorder.ui.features.files.FilesScreen
import ir.amirroid.screenrecorder.ui.features.home.HomeScreen
import ir.amirroid.screenrecorder.ui.features.permission.PermissionScreen
import ir.amirroid.screenrecorder.ui.features.settings.SettingsScreen
import ir.amirroid.screenrecorder.ui.features.video.VideoScreen
import ir.amirroid.screenrecorder.ui.theme.ScreenRecorderTheme
import ir.amirroid.screenrecorder.utils.AppPages
import ir.amirroid.screenrecorder.utils.Constants
import ir.amirroid.screenrecorder.utils.PermissionUtils
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var permissionUtils: PermissionUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScreenRecorderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(permissionUtils)
                }
            }
        }
    }
}

@Composable
fun MainScreen(permissionUtils: PermissionUtils) {
    val navigation = rememberNavController()
    val firstDestination =
        if (permissionUtils.checkPermissions()) AppPages.HomeScreen.route else AppPages.PermissionScreen.route
    NavHost(navController = navigation, startDestination = firstDestination) {
        composable(AppPages.PermissionScreen.route) {
            PermissionScreen(navigation, permissionUtils)
        }
        composable(AppPages.HomeScreen.route) {
            HomeScreen(navigation)
        }
        composable(AppPages.SettingsScreen.route) {
            SettingsScreen(navigation)
        }
        composable(AppPages.FilesScreen.route) {
            FilesScreen(navigation)
        }
        composable(
            AppPages.VideoScreen.route + "?${Constants.VIDEO_ARG}={${Constants.VIDEO_ARG}}",
            arguments = listOf(navArgument(Constants.VIDEO_ARG) {
                type = NavType.StringType
            })
        ) {
            val filePath = it.arguments?.getString(Constants.VIDEO_ARG)
            if (filePath == null) {
                navigation.popBackStack()
            } else {
                VideoScreen(filePath)
            }
        }
    }
}