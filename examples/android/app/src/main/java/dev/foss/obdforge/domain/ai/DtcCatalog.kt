package dev.foss.obdforge.domain.ai

object DtcCatalog {
    private val entries = mapOf(
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

    fun lookup(code: String): CatalogEntry? = entries[code.uppercase()]

    fun explain(code: String): DtcExplanation? {
        val entry = lookup(code) ?: return null
        return DtcExplanation(
            code = code.uppercase(),
            title = entry.title,
            summary = entry.summary,
            source = AiExplanationSource.Catalog,
            severity = entry.severity,
            classification = DtcClassification(
                severity = entry.severity,
                category = entry.category,
                confidence = 1.0f,
            ),
        )
    }

    data class CatalogEntry(
        val title: String,
        val summary: String,
        val severity: DtcSeverity,
        val category: String,
    )
}
