package dev.foss.obdforge.domain.flash

import dev.foss.obdforge.domain.transport.TransportType

/**
 * Flash transport: USB host (USB-C OTG) or Simulated demo only.
 * Bluetooth / OBDLink MX and Wi‑Fi / Ethernet are never flash transports.
 */
object FlashTransportPolicy {
    fun allows(type: TransportType?): Boolean = when (type) {
        TransportType.UsbSerial,
        TransportType.Simulated,
        -> true
        TransportType.Bluetooth,
        TransportType.WiFi,
        TransportType.Ethernet,
        null,
        -> false
    }

    fun blockReasonMessage(type: TransportType?): String = when (type) {
        TransportType.Bluetooth ->
            "Bluetooth adapters (including OBDLink MX) cannot be used for ECU flash — use USB-C OTG"
        TransportType.WiFi, TransportType.Ethernet ->
            "Wi‑Fi/Ethernet flash is not allowed — use USB-C host (OBDLink EX/SX + OTG)"
        null -> "Flash requires a USB serial adapter (USB-C OTG host)"
        else -> "Transport not allowed for flash"
    }
}
