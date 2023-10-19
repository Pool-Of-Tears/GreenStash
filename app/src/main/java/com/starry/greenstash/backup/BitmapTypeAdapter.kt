package com.starry.greenstash.backup

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type


/**
 * Gson type adaptor used for serializing and deserializing goal image which is
 * stored as [Bitmap] in the database.
 * Currently used for backup & restore functionality.
 */
class BitmapTypeAdapter : JsonSerializer<Bitmap?>, JsonDeserializer<Bitmap?> {

    /**
     * Gson invokes this call-back method during serialization when it encounters a field of the
     * specified type.
     *
     *
     * In the implementation of this call-back method, you should consider invoking
     * [JsonSerializationContext.serialize] method to create JsonElements for any
     * non-trivial field of the `src` object. However, you should never invoke it on the
     * `src` object itself since that will cause an infinite loop (Gson will call your
     * call-back method again).
     *
     * @param src the object that needs to be converted to Json.
     * @param typeOfSrc the actual type (fully genericized version) of the source object.
     * @return a JsonElement corresponding to the specified object.
     */
    override fun serialize(
        src: Bitmap?, typeOfSrc: Type?, context: JsonSerializationContext?
    ): JsonElement {
        val byteArrayOutputStream = ByteArrayOutputStream()
        src?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return JsonPrimitive(
            Base64.encodeToString(
                byteArrayOutputStream.toByteArray(), Base64.NO_WRAP
            )
        )
    }

    /**
     * Gson invokes this call-back method during deserialization when it encounters a field of the
     * specified type.
     *
     * In the implementation of this call-back method, you should consider invoking
     * [JsonDeserializationContext.deserialize] method to create objects
     * for any non-trivial field of the returned object. However, you should never invoke it on the
     * the same type passing `json` since that will cause an infinite loop (Gson will call your
     * call-back method again).
     *
     * @param json The Json data being deserialized
     * @param typeOfT The type of the Object to deserialize to
     * @return a deserialized object of the specified type typeOfT which is a subclass of `T`
     * @throws JsonParseException if json is not in the expected format of `typeofT`
     */
    override fun deserialize(
        json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?
    ): Bitmap? {
        if (json?.asString == null) return null
        val byteArray: ByteArray = Base64.decode(json.asString, Base64.NO_WRAP)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.count())
    }
}