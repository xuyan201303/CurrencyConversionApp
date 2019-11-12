package com.example.currencyconversionapp.common

import android.annotation.SuppressLint
import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import kotlin.collections.LinkedHashMap

class Utils {

    companion object {
        var linkedHashMap = LinkedHashMap<String, String>()

        const val ACCESS_KEY = "433a98d5f8a888e9833ba90f9dfe22ec"
        const val BASE_URL = "http://apilayer.net/api/"
        const val LIVE = "live"
        const val LIST = "list"

        const val CURRENCIES_KEY = "currencies"
        const val QUOTES_KEY = "quotes"
        const val SOURCE_KEY = "source"
        const val DATA_PRE_KEY = "dataPre"

        @SuppressLint("CommitPrefEdits")
        fun setPreRate(context: Context, obj: JSONObject?) {
            obj ?: return
            try {
                val sourceValue = obj.getString(SOURCE_KEY)
                context.getSharedPreferences("sourcePreference", Context.MODE_PRIVATE).edit()
                    .apply {
                        putString(SOURCE_KEY, sourceValue)
                    }.apply()

                val source = context.getSharedPreferences("sourcePreference", Context.MODE_PRIVATE)
                    .getString(SOURCE_KEY, "")

                // Get SharedPreferences
                val sharedPref = context.getSharedPreferences("keysPreference", 0)
                val keysArray = sharedPref.getString("keys", "")!!.split(",".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()

                var jsonArray = JSONArray()
                var jsonObject = JSONObject()
                for (i in keysArray.indices) {
                    jsonObject.put("key", keysArray[i])
                    keysArray[i] = source + keysArray[i]
                    val value = obj.getJSONObject(QUOTES_KEY).getDouble(keysArray[i])
                    jsonObject.put("value", value)
                    jsonArray.put(jsonObject)

                    jsonObject = JSONObject()
                }

                context.getSharedPreferences("dataPreference", Context.MODE_PRIVATE).edit()
                    .apply {
                        putString(DATA_PRE_KEY, jsonArray.toString())
                    }.apply()

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        @SuppressLint("CommitPrefEdits")
        fun setPreCurrency(context: Context, obj: JSONObject?) {
            obj ?: return
            try {
                var keys = obj.getJSONObject(CURRENCIES_KEY).keys()
                val editor =
                    context.getSharedPreferences("keysPreference", Context.MODE_PRIVATE).edit()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val value = obj.getJSONObject(CURRENCIES_KEY).getString(key)
                    linkedHashMap[key] = value
                }

                val itr = linkedHashMap.keys.iterator()
                var itrKeys = ""
                while (itr.hasNext()) {
                    val key = itr.next()
                    editor.apply {
                        putString(key, linkedHashMap[key])
                    }.apply()
                    itrKeys += itr.next() + ","
                }
                itrKeys = itrKeys.substring(0, itrKeys.length - 1)
                editor.apply {
                    putString("keys", itrKeys)
                }.apply()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        fun isUpdateETag(context: Context, connection: HttpURLConnection): Boolean {

            val eTag = connection.getHeaderField("ETag")
            val source = context.getSharedPreferences("sourcePreference", Context.MODE_PRIVATE)
                .getString("eTag", "")

            if (source == "") {
                context.getSharedPreferences("eTagPreference", Context.MODE_PRIVATE).edit()
                    .apply {
                        putString("eTag", eTag)
                    }.apply()
                return true
            }
            return eTag == source && eTag != ""
        }
    }
}
