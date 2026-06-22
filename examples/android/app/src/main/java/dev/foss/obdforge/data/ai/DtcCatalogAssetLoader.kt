package dev.foss.obdforge.data.ai

import android.content.Context
import dev.foss.obdforge.domain.ai.DtcCatalog
import dev.foss.obdforge.domain.ai.DtcSeverity
import org.json.JSONArray

object DtcCatalogAssetLoader {
    // Source in git is dtc_catalog.json.gz; AGP decompresses to this path at merge time.
    private const val ASSET = "diagnostics/dtc_catalog.json"

    fun loadIntoCatalog(context: Context): Int {
        val json = context.assets.open(ASSET).bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        val entries = LinkedHashMap<String, DtcCatalog.CatalogEntry>(array.length())
        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)
            val code = item.getString("c").uppercase()
            entries[code] = DtcCatalog.CatalogEntry(
                title = item.getString("t"),
                summary = item.getString("s"),
                severity = parseSeverity(item.optString("v", "unknown")),
                category = item.optString("g", "unknown"),
            )
        }
        DtcCatalog.install(entries)
        return entries.size
    }

    private fun parseSeverity(value: String): DtcSeverity = when (value.lowercase()) {
        "low" -> DtcSeverity.Low
        "medium" -> DtcSeverity.Medium
        "high" -> DtcSeverity.High
        else -> DtcSeverity.Unknown
    }
}
