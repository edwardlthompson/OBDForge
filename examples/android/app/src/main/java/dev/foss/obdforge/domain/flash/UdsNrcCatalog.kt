package dev.foss.obdforge.domain.flash

/**
 * Static UDS negative-response explanations for flash assist (education only).
 */
object UdsNrcCatalog {
    private val entries = mapOf(
        0x11 to "Service not supported",
        0x12 to "Sub-function not supported",
        0x13 to "Incorrect message length or invalid format",
        0x22 to "Conditions not correct",
        0x24 to "Request sequence error",
        0x31 to "Request out of range",
        0x33 to "Security access denied",
        0x35 to "Invalid key",
        0x36 to "Exceed number of attempts",
        0x37 to "Required time delay not expired",
        0x70 to "Upload/download not accepted",
        0x71 to "Transfer data suspended",
        0x72 to "General programming failure",
        0x73 to "Wrong block sequence counter",
        0x78 to "Request correctly received — response pending",
        0x7E to "Sub-function not supported in active session",
        0x7F to "Service not supported in active session",
    )

    fun explain(nrc: Int): String? = entries[nrc and 0xFF]

    fun format(nrc: Int): String {
        val code = nrc and 0xFF
        val text = explain(code) ?: "Unknown negative response code"
        return "NRC 0x%02X: %s".format(code, text)
    }
}
