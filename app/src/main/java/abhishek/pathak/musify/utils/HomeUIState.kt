package abhishek.pathak.musify.utils

sealed class HomeUIState{
    object InitialHome: HomeUIState()
    object HomeReady: HomeUIState()
}
