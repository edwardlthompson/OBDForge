package dev.foss.obdforge.data.transport.io

import java.util.UUID

internal data class BleSerialProfile(
    val service: UUID,
    val write: UUID,
    val notify: UUID,
)

internal object ObdBleUuids {
    val PROFILES = listOf(
        BleSerialProfile(
            service = uuid("0000fff0-0000-1000-8000-00805f9b34fb"),
            write = uuid("0000fff1-0000-1000-8000-00805f9b34fb"),
            notify = uuid("0000fff2-0000-1000-8000-00805f9b34fb"),
        ),
        BleSerialProfile(
            service = uuid("6e400001-b5a3-f393-e0a9-e50e24dcca9e"),
            write = uuid("6e400002-b5a3-f393-e0a9-e50e24dcca9e"),
            notify = uuid("6e400003-b5a3-f393-e0a9-e50e24dcca9e"),
        ),
    )

    private fun uuid(value: String): UUID = UUID.fromString(value)
}
