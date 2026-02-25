/*
 * Copyright (c) 2025 Arthur Araujo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package cc.wordview.app.misc

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.toBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Globally handles the preloading of images
 */
object ImageCacheManager {
    private lateinit var loader: ImageLoader
    private val globalImageLoaderScope = CoroutineScope(Dispatchers.IO)

    private val imagesStatus = HashMap<String, ImageLoaderStatus>()
    private val imageRequestQueue = ArrayList<ImageRequest>()
    private val imageCache = HashMap<String, Bitmap>()

    var onQueueCompleted = {}

    fun init(context: Context) {
        loader = ImageLoader(context)
    }

    fun enqueue(key: String, request: ImageRequest.Builder) {
        if (isQueued(key)) return

        globalImageLoaderScope.launch {
            request.listener(object : ImageRequest.Listener {
                override fun onStart(request: ImageRequest) {
                    val key = request.memoryCacheKey!!
                    imagesStatus[key] = ImageLoaderStatus.LOADING
                }

                override fun onError(request: ImageRequest, result: ErrorResult) {
                    Timber.e("Failed to download image \"${request.memoryCacheKey!!}\": ${result.throwable.message}")

                    val key = request.memoryCacheKey!!
                    imagesStatus[key] = ImageLoaderStatus.ERROR
                }

                override fun onSuccess(request: ImageRequest, result: SuccessResult) {
                    val key = request.memoryCacheKey!!
                    imagesStatus[key] = ImageLoaderStatus.SUCCESS
                }
            })
            val builtRequest = request.build()

            val existingRequestStatus = imagesStatus[builtRequest.memoryCacheKey!!]

            if (existingRequestStatus == null || existingRequestStatus == ImageLoaderStatus.ERROR) {
                imageRequestQueue.add(builtRequest)
            }
        }
    }

    private fun isQueued(key: String): Boolean {
        return imageRequestQueue.any { img -> img.memoryCacheKey == key }
    }

    suspend fun executeAllInQueue() {
        val queue = imageRequestQueue.toList()

        for (item in queue) {
            val result = loader.execute(item)

            val key = result.request.memoryCacheKey!!
            val value = result.image?.toBitmap()

            value?.let { imageCache[key] = it }
        }

        imageRequestQueue.clear()
        onQueueCompleted.invoke()
    }

    fun getCachedImage(key: String): Bitmap? {
        val currentStatus = imagesStatus[key]

        when (currentStatus) {
            ImageLoaderStatus.LOADING -> {
                Timber.w("Image with key=$key is still being loaded")
                return null
            }

            ImageLoaderStatus.ERROR -> return null

            ImageLoaderStatus.SUCCESS -> return imageCache[key]

            null -> {
                Timber.w("The image associated with key=$key is not present")
                return null
            }
        }
    }

    fun getDiskCachedImage(key: String): Bitmap? {
        val cachedValue = loader.diskCache?.openSnapshot(key)
        return BitmapFactory.decodeFile(cachedValue?.data?.toFile()?.path)
    }

    enum class ImageLoaderStatus {
        LOADING,
        ERROR,
        SUCCESS
    }
}