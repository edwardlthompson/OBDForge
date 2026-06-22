package dev.foss.obdforge.data.persistence

import org.json.JSONArray
import org.json.JSONObject

object SessionJsonCodec {
    fun encodeCodes(codes: List<String>): String = JSONArray(codes).toString()

    fun decodeCodes(json: String): List<String> {
        val array = JSONArray(json)
        return buildList {
            for (index in 0 until array.length()) {
                add(array.getString(index))
            }
        }
    }

    fun encodePidValues(values: Map<String, String>): String = JSONObject(values).toString()

    fun decodePidValues(json: String): Map<String, String> {
        val objectJson = JSONObject(json)
        val keys = objectJson.keys()
        return buildMap {
            while (keys.hasNext()) {
                val key = keys.next()
                put(key, objectJson.getString(key))
            }
        }
    }
}
