package dev.foss.obdforge.domain.transport

enum class BluetoothLinkKind {
    /** RFCOMM serial port profile (classic paired ELM327 / OBDLink). */
    Classic,

    /** BLE GATT serial bridge (common ELM327 BLE clones). */
    Ble,

    /** Try BLE GATT first, then classic SPP. */
    Auto,
}
