package abhishek.pathak.musify.data.repository

import abhishek.pathak.musify.data.local.ContentResolverHelper
import abhishek.pathak.musify.data.local.models.AudioItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicRepository(
    private val contentResolverHelper: ContentResolverHelper,
) {
    suspend fun getAudioData(): List<AudioItem> = withContext(Dispatchers.IO) {
        contentResolverHelper.getAudioData()
    }
}