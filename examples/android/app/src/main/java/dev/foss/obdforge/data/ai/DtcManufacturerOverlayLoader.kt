package dev.foss.obdforge.data.ai

import android.content.Context
import dev.foss.obdforge.domain.ai.DtcCatalog
import dev.foss.obdforge.domain.ai.DtcSeverity
import org.json.JSONArray

object DtcManufacturerOverlayLoader {
    private const val ASSET = "diagnostics/dtc_manufacturer_overlay.json"

    fun loadIntoCatalog(context: Context): Int {
        val json = readAsset(context) ?: return 0
        val array = JSONArray(json)
        val byManufacturer = LinkedHashMap<String, MutableMap<String, DtcCatalog.CatalogEntry>>()
        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)
            val code = item.getString("c").uppercase()
            val manufacturer = item.getString("m").uppercase()
            val entry = DtcCatalog.CatalogEntry(
                title = item.getString("t"),
                summary = item.getString("s"),
                severity = parseSeverity(item.optString("v", "unknown")),
                category = item.optString("g", "unknown"),
            )
            byManufacturer.getOrPut(manufacturer) { LinkedHashMap() }[code] = entry
        }
        DtcCatalog.installManufacturerOverlay(byManufacturer)
        return array.length()
    }

    private fun readAsset(context: Context): String? =
        runCatching {
            context.assets.open(ASSET).bufferedReader().use { it.readText() }
        }.getOrNull()

    private fun parseSeverity(value: String): DtcSeverity = when (value.lowercase()) {
        "low" -> DtcSeverity.Low
        "medium" -> DtcSeverity.Medium
        "high" -> DtcSeverity.High
        else -> DtcSeverity.Unknown
    }
}
