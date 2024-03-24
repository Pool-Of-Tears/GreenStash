/**
 * MIT License
 *
 * Copyright (c) [2022 - Present] Stɑrry Shivɑm
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.starry.greenstash.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector

object ImageUtils {

    private const val TAG = "ImageUtils"

    /** Create image vector from icon name. */
    fun createIconVector(name: String): ImageVector? {
        return try {
            val className = "androidx.compose.material.icons.filled.${name}Kt"
            val cl = Class.forName(className)
            val method = cl.declaredMethods.first()
            method.invoke(null, Icons.Filled) as ImageVector
        } catch (ex: Exception) {
            Log.e(TAG, "Error creating image vector", ex)
            null
        }
    }

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