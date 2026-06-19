package com.afitech.data.repository

import com.afitech.data.model.DownloadInfo
import com.afitech.data.model.TikTokPreview
import com.afitech.data.remote.TikWmDataSource

class TikTokRepositoryImpl(

    private val dataSource:
    TikWmDataSource = TikWmDataSource()

) : TikTokRepository {

    private var lastUrl: String? = null

    private var lastData:
            org.json.JSONObject? = null

    private val shortLinkCache =
        mutableMapOf<String, String>()
    override suspend fun analyzeUrl(
        url: String
    ): Result<TikTokPreview> {

        val resolvedUrl =

            if (
                url.contains("vt.tiktok.com") ||
                url.contains("vm.tiktok.com")
            ) {

                shortLinkCache[url]

                    ?: dataSource
                        .resolveShortLink(url)
                        ?.also {

                            shortLinkCache[url] = it
                        }

                    ?: url

            } else {

                url
            }

        val data =
            getCachedData(
                resolvedUrl
            )

                ?: return Result.failure(
                    Exception(
                        "Failed connect TikWM"
                    )
                )
        val title =
            data.optString(
                "title",
                "No Title"
            )

        val authorObject =
            data.optJSONObject("author")

        val author =

            authorObject?.optString(
                "unique_id"
            )?.let {
                "@$it"
            }

                ?: "@unknown"

        val imagesArray =
            data.optJSONArray("images")

        val isSlide =
            imagesArray != null &&
                    imagesArray.length() > 0


        val durationSeconds =
            data.optInt(
                "duration",
                0
            )

        val duration =
            String.format(
                "%02d:%02d",
                durationSeconds / 60,
                durationSeconds % 60
            )

        val thumbnail =
            data.optString(
                "cover",
                ""
            )
        val previewVideoUrl =

            if (!isSlide)
                data.optString("play")
            else
                null
        return Result.success(

            TikTokPreview(

                title = title,

                author = author,

                duration = duration,

                thumbnail = thumbnail,

                sourceUrl = resolvedUrl,

                isSlide = isSlide,

                previewVideoUrl = previewVideoUrl
            )
        )
    }

    override suspend fun getDownloadInfo(
        url: String,
        format: String
    ): Result<DownloadInfo> {

        val data =
            getCachedData(url)

                ?: return Result.failure(
                    Exception("Failed connect TikWM")
                )

        val downloadUrl =
            when (format) {

                "VIDEO" ->
                    data.optString("play")

                "MUSIC" ->
                    data.optJSONObject("music_info")
                        ?.optString("play")

                "COVER" ->
                    data.optString("cover")

                else -> null
            }


        if (downloadUrl.isNullOrEmpty()) {


            return Result.failure(
                Exception("Download URL not found")
            )
        }

        val remoteMeta =
            dataSource.probeRemote(downloadUrl)

        val result =
            guessExtensionAndMime(
                remoteMeta.contentType,
                remoteMeta.filenameFromServer
                    ?: downloadUrl
            )


        return Result.success(
            DownloadInfo(
                url = downloadUrl,
                ext = result.first,
                mime = result.second
            )
        )
    }

    private suspend fun getCachedData(
        url: String
    ): org.json.JSONObject? {

        if (
            lastUrl == url &&
            lastData != null
        ) {
            return lastData
        }

        val json =
            dataSource.fetchApiData(url)

        val data =
            json?.optJSONObject("data")

        if (data != null) {

            lastUrl = url

            lastData = data
        }

        return data
    }

    private fun guessExtensionAndMime(

        contentType: String?,

        fallbackUrl: String

    ): Pair<String, String> {

        val type =
            contentType?.lowercase()

        return when {

            type == "video/mp4" ->
                ".mp4" to "video/mp4"

            type == "audio/mpeg" ->
                ".mp3" to "audio/mpeg"

            type == "audio/mp4" ->
                ".m4a" to "audio/mp4"

            type?.startsWith("image/") == true ->

                ".jpg" to "image/jpeg"

            else -> {

                when {

                    fallbackUrl.contains(
                        ".mp3"
                    ) ->
                        ".mp3" to "audio/mpeg"

                    fallbackUrl.contains(
                        ".m4a"
                    ) ->
                        ".m4a" to "audio/mp4"

                    fallbackUrl.contains(
                        ".jpg"
                    ) ->
                        ".jpg" to "image/jpeg"

                    else ->
                        ".mp4" to "video/mp4"
                }
            }
        }
    }
    override suspend fun getSlideImages(
        url: String
    ): Result<List<String>> {

        val data =

            getCachedData(url)

                ?: return Result.failure(

                    Exception(
                        "Failed connect TikWM"
                    )
                )
        val imagesArray =

            data.optJSONArray("images")

                ?: return Result.failure(

                    Exception(
                        "Slide images not found"
                    )
                )

        val images = mutableListOf<String>()

        for (i in 0 until imagesArray.length()) {

            images.add(

                imagesArray.optString(i)
            )
        }

        return Result.success(images)
    }
}

