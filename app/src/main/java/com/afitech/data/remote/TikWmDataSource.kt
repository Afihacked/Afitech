package com.afitech.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import com.afitech.data.model.RemoteMeta
import kotlinx.coroutines.delay
import java.net.URLDecoder
import java.net.URLEncoder

class TikWmDataSource {

    companion object {

        private const val BASE_API_URL =
            "https://www.tikwm.com/api/?url="

        private const val USER_AGENT =
            "AfitechTok/2.0 Android"

        private const val CONNECT_TIMEOUT =
            20000

        private const val READ_TIMEOUT =
            30000

        private const val TAG =
            "TikWmDataSource"
    }

    private inline fun <T> HttpURLConnection.useConnection(
        block: (HttpURLConnection) -> T
    ): T {

        return try {

            block(this)

        } finally {

            disconnect()
        }
    }
    suspend fun fetchApiData(
        url: String
    ): JSONObject? = withContext(Dispatchers.IO) {

        repeat(3) { attempt ->

            try {

                val apiUrl =
                    BASE_API_URL +
                            URLEncoder.encode(
                                url,
                                "UTF-8"
                            )

                (URL(apiUrl)
                    .openConnection() as HttpURLConnection)
                    .useConnection { connection ->

                        connection.requestMethod =
                            "GET"

                        connection.setRequestProperty(
                            "User-Agent",
                            USER_AGENT
                        )

                        connection.connectTimeout =
                            CONNECT_TIMEOUT

                        connection.readTimeout =
                            READ_TIMEOUT

                        connection.connect()

                        if (
                            connection.responseCode !=
                            HttpURLConnection.HTTP_OK
                        ) {

                            Log.w(
                                TAG,
                                "fetchApiData HTTP ${connection.responseCode}"
                            )

                            return@useConnection
                        }

                        val response =
                            connection.inputStream
                                .bufferedReader()
                                .use { it.readText() }

                        return@withContext JSONObject(
                            response
                        )
                    }

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "fetchApiData attempt=${attempt + 1}",
                    e
                )
            }

            delay(1000)
        }

        null
    }

    suspend fun resolveShortLink(
        shortUrl: String
    ): String? = withContext(Dispatchers.IO) {

        repeat(3) { attempt ->

            try {

                (URL(shortUrl)
                    .openConnection() as HttpURLConnection)
                    .useConnection { connection ->

                        connection.instanceFollowRedirects =
                            false

                        connection.requestMethod =
                            "GET"

                        connection.setRequestProperty(
                            "User-Agent",
                            USER_AGENT
                        )

                        connection.connectTimeout =
                            CONNECT_TIMEOUT

                        connection.readTimeout =
                            READ_TIMEOUT

                        connection.connect()

                        val responseCode =
                            connection.responseCode

                        if (
                            responseCode !in 300..399 &&
                            responseCode != HttpURLConnection.HTTP_OK
                        ) {

                            Log.w(
                                TAG,
                                "resolveShortLink HTTP $responseCode"
                            )

                            return@useConnection
                        }

                        val resolvedUrl =
                            connection.getHeaderField(
                                "Location"
                            ) ?: shortUrl

                        return@withContext resolvedUrl
                    }

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "resolveShortLink attempt=${attempt + 1}",
                    e
                )
            }

            delay(1000)
        }

        null
    }
    suspend fun probeRemote(
        url: String
    ): RemoteMeta = withContext(Dispatchers.IO) {

        repeat(3) { attempt ->

            try {

                (URL(url)
                    .openConnection() as HttpURLConnection)
                    .useConnection { connection ->

                        connection.requestMethod =
                            "HEAD"

                        connection.setRequestProperty(
                            "User-Agent",
                            USER_AGENT
                        )

                        connection.connectTimeout =
                            CONNECT_TIMEOUT

                        connection.readTimeout =
                            READ_TIMEOUT

                        connection.instanceFollowRedirects =
                            true

                        connection.connect()

                        if (
                            connection.responseCode !in 200..299
                        ) {

                            Log.w(
                                TAG,
                                "probeRemote HTTP ${connection.responseCode}"
                            )

                            return@useConnection
                        }

                        val contentType =
                            connection.contentType

                        val contentLength =
                            connection.getHeaderFieldLong(
                                "Content-Length",
                                -1
                            ).let {

                                if (it >= 0)
                                    it
                                else
                                    null
                            }

                        val disposition =
                            connection.getHeaderField(
                                "Content-Disposition"
                            )

                        val filename =
                            disposition?.let {

                                val index =
                                    it.indexOf(
                                        "filename="
                                    )

                                if (index >= 0) {

                                    val candidate =
                                        it.substring(
                                            index + 9
                                        )
                                            .trim()
                                            .trim('"')

                                    try {

                                        URLDecoder.decode(
                                            candidate,
                                            "UTF-8"
                                        )

                                    } catch (_: Exception) {

                                        candidate
                                    }

                                } else {

                                    null
                                }
                            }

                        return@withContext RemoteMeta(

                            contentType =
                                contentType,

                            contentLength =
                                contentLength,

                            filenameFromServer =
                                filename
                        )
                    }

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "probeRemote attempt=${attempt + 1}",
                    e
                )
            }

            delay(1000)
        }

        RemoteMeta(

            contentType = null,

            contentLength = null,

            filenameFromServer = null
        )
    }
}