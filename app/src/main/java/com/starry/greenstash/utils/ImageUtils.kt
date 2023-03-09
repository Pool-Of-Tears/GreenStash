package com.starry.greenstash.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

object ImageUtils {
    /** Get bitmap from image Uri. */
    fun uriToBitmap(uri: Uri, context: Context, maxSize: Int): Bitmap {
        val stream = context.contentResolver.openInputStream(uri)
        val imageBm = BitmapFactory.decodeStream(stream)
        return compressBitmap(imageBm, maxSize)
    }

    private fun compressBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}