package ir.amirroid.screenrecorder.utils

sealed class AppPages(val route: String) {
    object PermissionScreen : AppPages(Constants.PERMISSION)
    object HomeScreen : AppPages(Constants.HOME)
    object SettingsScreen : AppPages(Constants.SETTINGS)
    object FilesScreen : AppPages(Constants.FILES)
    object VideoScreen : AppPages(Constants.VIDEO)
}