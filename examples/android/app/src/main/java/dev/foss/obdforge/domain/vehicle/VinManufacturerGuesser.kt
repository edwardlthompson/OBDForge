package dev.foss.obdforge.domain.vehicle

import dev.foss.obdforge.domain.ai.DtcManufacturerNormalizer

object VinManufacturerGuesser {
    private val wmiToManufacturer: Map<String, String> = mapOf(
        "1G1" to "CHEVY",
        "1G6" to "CADILLAC",
        "1GC" to "CHEVY",
        "1GM" to "GMC",
        "1GN" to "CHEVY",
        "2G1" to "CHEVY",
        "3G1" to "CHEVY",
        "1FA" to "FORD",
        "1FB" to "FORD",
        "1FC" to "FORD",
        "1FD" to "FORD",
        "1FM" to "FORD",
        "1FT" to "FORD",
        "1FU" to "FORD",
        "1FV" to "FORD",
        "1HG" to "HONDA",
        "1HF" to "HONDA",
        "19U" to "ACURA",
        "1N4" to "NISSAN",
        "1N6" to "NISSAN",
        "3N1" to "NISSAN",
        "5N1" to "INFINITI",
        "JT2" to "TOYOTA",
        "JTD" to "TOYOTA",
        "4T1" to "TOYOTA",
        "2T1" to "TOYOTA",
        "5TD" to "TOYOTA",
        "5TE" to "TOYOTA",
        "4S3" to "SUBARU",
        "4S4" to "SUBARU",
        "JF1" to "SUBARU",
        "WBA" to "BMW",
        "WBS" to "BMW",
        "WDB" to "MERCEDES",
        "WDC" to "MERCEDES",
        "WAU" to "AUDI",
        "WVW" to "VOLKSWAGEN",
        "3VW" to "VOLKSWAGEN",
        "1C3" to "CHRYSLER",
        "1C4" to "JEEP",
        "1C6" to "DODGE",
        "1D3" to "DODGE",
        "1D4" to "DODGE",
        "1D7" to "DODGE",
        "1J4" to "JEEP",
        "1J8" to "JEEP",
        "KMH" to "HYUNDAI",
        "KM8" to "HYUNDAI",
        "KNA" to "KIA",
        "KND" to "KIA",
        "JM1" to "MAZDA",
        "JM3" to "MAZDA",
        "4F2" to "MAZDA",
        "JA3" to "MITSUBISHI",
        "JA4" to "MITSUBISHI",
        "4A3" to "MITSUBISHI",
        "2HG" to "HONDA",
        "5FN" to "HONDA",
        "5J6" to "HONDA",
        "5NP" to "HYUNDAI",
        "5XY" to "KIA",
        "JTH" to "LEXUS",
        "2T2" to "LEXUS",
        "5YJ" to "OTHER",
    )

    fun guessFromVin(vin: String): String? {
        val normalized = vin.uppercase().filter { it.isLetterOrDigit() }
        if (normalized.length < 3) return null
        return wmiToManufacturer[normalized.take(3)]
            ?: wmiToManufacturer[normalized.take(2)]
    }

    fun resolve(profile: VehicleProfile?): String? {
        profile?.label?.let { DtcManufacturerNormalizer.normalize(it) }?.let { return it }
        profile?.vin?.let { guessFromVin(it) }?.let { return it }
        return null
    }
}
