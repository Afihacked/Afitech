package com.afitech.utils

object TikTokUrlHelper {

    private val urlRegex = Regex(
        "(https?://[^\\s]+)"
    )

    fun extractTikTokUrl(
        text: String
    ): String? {

        val urls =

            urlRegex.findAll(text)

        return urls.firstOrNull {

            val url = it.value

            url.contains("tiktok.com")
                    ||
                    url.contains("vt.tiktok.com")

        }?.value
    }

    fun isValidTikTokUrl(
        url: String
    ): Boolean {

        val cleanUrl =
            url.trim()

        return listOf(

            Regex(
                "https?://vt\\.tiktok\\.com/[A-Za-z0-9]{8,}/?"
            ),

            Regex(
                "https?://(www\\.)?tiktok\\.com/@[^/]+/video/\\d+"
            ),

            Regex(
                "https?://m\\.tiktok\\.com/v/\\d+.*"
            ),

            Regex(
                "https?://(www\\.)?tiktok\\.com/t/[A-Za-z0-9]+/?"
            )

        ).any {

            it.matches(cleanUrl)
        }
    }
}