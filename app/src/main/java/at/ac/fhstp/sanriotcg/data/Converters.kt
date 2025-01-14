package at.ac.fhstp.sanriotcg.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromCardIds(cardIds: List<Int>?): String? {
        return cardIds?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toCardIds(cardIdsString: String?): List<Int>? {
        return cardIdsString?.let {
            val listType = object : TypeToken<List<Int>>() {}.type
            Gson().fromJson<List<Int>>(it, listType)
        }
    }
}
