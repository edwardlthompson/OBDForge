package dev.foss.obdforge.domain.ai

object DtcManufacturerNormalizer {
    private val aliases: Map<String, String> = mapOf(
        "CHEVROLET" to "CHEVY",
        "CHEVROLETTRUCK" to "CHEVY",
        "VW" to "VOLKSWAGEN",
        "MERCEDESBENZ" to "MERCEDES",
        "MERCEDES-BENZ" to "MERCEDES",
        "INFINITY" to "INFINITI",
    )

    private val gmFamily: Set<String> = setOf(
        "GM", "CHEVY", "GMC", "BUICK", "CADILLAC", "PONTIAC", "OLDSMOBILE", "GEO", "SATURN",
    )

    fun normalize(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val cleaned = raw.uppercase().trim()
        aliases[cleaned]?.let { return it }
        return cleaned.filter { it.isLetterOrDigit() }.ifBlank { null }
    }

    fun lookupKeys(raw: String?): List<String> {
        val primary = normalize(raw) ?: return emptyList()
        val keys = linkedSetOf(primary)
        if (primary in gmFamily) keys.addAll(gmFamily)
        if (primary == "CHEVY") keys.add("GM")
        return keys.toList()
    }
}
