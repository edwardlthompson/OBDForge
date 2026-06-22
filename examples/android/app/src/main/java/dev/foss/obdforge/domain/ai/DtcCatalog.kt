package dev.foss.obdforge.domain.ai

object DtcCatalog {
    private val fallback = mapOf(
        "P0133" to CatalogEntry(
            title = "O2 sensor slow response (Bank 1 Sensor 1)",
            summary = "The upstream oxygen sensor is responding slower than expected. " +
                "Common causes include a worn sensor, exhaust leak, or wiring fault.",
            severity = DtcSeverity.Medium,
            category = "emissions",
        ),
        "P0171" to CatalogEntry(
            title = "System too lean (Bank 1)",
            summary = "The engine is receiving too much air or too little fuel on bank 1. " +
                "Check vacuum leaks, MAF sensor, and fuel delivery.",
            severity = DtcSeverity.Medium,
            category = "fuel",
        ),
        "P0300" to CatalogEntry(
            title = "Random/multiple cylinder misfire",
            summary = "The ECU detected misfires across multiple cylinders. " +
                "Inspect spark, fuel, and compression before clearing codes.",
            severity = DtcSeverity.High,
            category = "ignition",
        ),
    )

    private var assetEntries: Map<String, CatalogEntry>? = null
    private var manufacturerOverlay: Map<String, Map<String, CatalogEntry>> = emptyMap()

    fun install(entries: Map<String, CatalogEntry>) {
        assetEntries = entries
    }

    fun installManufacturerOverlay(entries: Map<String, Map<String, CatalogEntry>>) {
        manufacturerOverlay = entries
    }

    fun resetForTests() {
        assetEntries = null
        manufacturerOverlay = emptyMap()
    }

    fun lookup(code: String, manufacturer: String? = null): CatalogEntry? {
        val normalized = code.uppercase()
        lookupManufacturerOverlay(normalized, manufacturer)?.let { return it }
        return assetEntries?.get(normalized) ?: fallback[normalized]
    }

    private fun lookupManufacturerOverlay(code: String, manufacturer: String?): CatalogEntry? {
        if (manufacturerOverlay.isEmpty() || manufacturer.isNullOrBlank()) return null
        for (key in DtcManufacturerNormalizer.lookupKeys(manufacturer)) {
            manufacturerOverlay[key]?.get(code)?.let { return it }
        }
        return null
    }

    fun explain(code: String, manufacturer: String? = null): DtcExplanation? {
        val normalized = code.uppercase()
        val manufacturerEntry = lookupManufacturerOverlay(normalized, manufacturer)
        if (manufacturerEntry != null) {
            return explanationFor(normalized, manufacturerEntry, AiExplanationSource.Manufacturer)
        }
        val entry = assetEntries?.get(normalized) ?: fallback[normalized] ?: return null
        return explanationFor(normalized, entry, AiExplanationSource.Catalog)
    }

    private fun explanationFor(
        code: String,
        entry: CatalogEntry,
        source: AiExplanationSource,
    ): DtcExplanation =
        DtcExplanation(
            code = code,
            title = entry.title,
            summary = entry.summary,
            source = source,
            severity = entry.severity,
            classification = DtcClassification(
                severity = entry.severity,
                category = entry.category,
                confidence = 1.0f,
            ),
        )

    data class CatalogEntry(
        val title: String,
        val summary: String,
        val severity: DtcSeverity,
        val category: String,
    )
}
