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


package com.starry.greenstash.backup

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.ByteArrayOutputStream

/**
 * KSerializer for serializing and deserializing goal images which
 * are stored as [Bitmap] in the database.
 */
object BitmapSerializer : KSerializer<Bitmap> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Bitmap", PrimitiveKind.STRING)

    // Encode the Bitmap to a Base64 string
    override fun serialize(encoder: Encoder, value: Bitmap) {
        val stream = ByteArrayOutputStream()
        value.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        encoder.encodeString(
            android.util.Base64.encodeToString(
                byteArray, android.util.Base64.NO_WRAP
            )
        )
    }

    // Decode the Base64 string to a Bitmap
    override fun deserialize(decoder: Decoder): Bitmap {
        val byteArray =
            android.util.Base64.decode(decoder.decodeString(), android.util.Base64.NO_WRAP)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}