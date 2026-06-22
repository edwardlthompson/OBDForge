package dev.foss.obdforge.data.diagnostics

import android.content.Context
import dev.foss.obdforge.domain.diagnostics.PidRangeEvaluator
import org.json.JSONArray

object PidRangeAssetLoader {
    private const val ASSET = "diagnostics/pid_ranges.json"

    fun loadIntoEvaluator(context: Context): Int {
        val json = context.assets.open(ASSET).bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        val ranges = LinkedHashMap<Int, PidRangeEvaluator.Range>(array.length())
        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)
            ranges[item.getInt("pid")] = PidRangeEvaluator.Range(
                min = item.getDouble("min"),
                max = item.getDouble("max"),
            )
        }
        PidRangeEvaluator.install(ranges)
        return ranges.size
    }
}
