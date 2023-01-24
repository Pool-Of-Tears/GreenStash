package com.starry.greenstash.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray? {
        if (bitmap != null) {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            return outputStream.toByteArray()
        }
        return null
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray?): Bitmap? {
        if (byteArray != null) {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
        return null
    }

    @TypeConverter
    fun fromTransactionType(value: TransactionType) = value.ordinal

    @TypeConverter
    fun totransactiontype(value: Int) = when (value) {
        TransactionType.Deposit.ordinal -> TransactionType.Deposit
        TransactionType.Withdraw.ordinal -> TransactionType.Withdraw
        else -> TransactionType.Invalid
    }
}