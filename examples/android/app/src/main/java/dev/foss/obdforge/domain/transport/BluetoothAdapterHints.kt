package dev.foss.obdforge.domain.transport

/**
 * Pure helpers for Bluetooth adapter naming and link-kind defaults.
 * OBDLink MX / MX+ use Classic SPP, not BLE GATT serial profiles.
 */
object BluetoothAdapterHints {
    private val classicPreferredPatterns = listOf(
        Regex("""obdlink""", RegexOption.IGNORE_CASE),
        Regex("""\bmx\+?\b""", RegexOption.IGNORE_CASE),
        Regex("""\bcx\b""", RegexOption.IGNORE_CASE),
        Regex("""\blx\b""", RegexOption.IGNORE_CASE),
    )

    fun prefersClassicSpp(displayName: String?): Boolean {
        val name = displayName?.trim().orEmpty()
        if (name.isEmpty()) return false
        return classicPreferredPatterns.any { it.containsMatchIn(name) }
    }

    fun defaultLinkKind(displayName: String?): BluetoothLinkKind =
        if (prefersClassicSpp(displayName)) BluetoothLinkKind.Classic else BluetoothLinkKind.Auto

    fun isBlankAddress(address: String?): Boolean = address.isNullOrBlank()
}
