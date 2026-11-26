package cl.triskeledu.aprendoiajugando.models

data class YouTubeSearchResponse(
    val items: List<YouTubeSearchItem> = emptyList(),
    val nextPageToken: String? = null
)

data class YouTubeSearchItem(
    val id: YouTubeVideoId,
    val snippet: YouTubeVideoSnippet
)

data class YouTubeVideoId(
    val videoId: String?
)

data class YouTubeVideoSnippet(
    val title: String,
    val description: String,
    val channelTitle: String,
    val thumbnails: YouTubeThumbnails
)

data class YouTubeThumbnails(
    val medium: YouTubeThumbnail? = null,
    val high: YouTubeThumbnail? = null,
    val default: YouTubeThumbnail? = null
)

data class YouTubeThumbnail(
    val url: String
)
