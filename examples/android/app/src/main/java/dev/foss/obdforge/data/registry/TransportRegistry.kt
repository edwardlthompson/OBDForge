package dev.foss.obdforge.data.registry

import android.content.Context
import dev.foss.obdforge.data.diagnostics.DiagnosticEventRecorder
import dev.foss.obdforge.data.transport.LoggingObdTransport
import dev.foss.obdforge.data.transport.TransportFactory
import dev.foss.obdforge.domain.transport.ObdTransport
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType

class TransportRegistry(
    private val context: Context,
    private val eventRecorder: DiagnosticEventRecorder? = null,
) {
    private val supported = setOf(
        TransportType.Simulated,
        TransportType.Bluetooth,
        TransportType.UsbSerial,
        TransportType.WiFi,
        TransportType.Ethernet,
    )

    fun create(type: TransportType, endpoint: TransportEndpoint): ObdTransport? {
        if (type !in supported) return null
        val transport = TransportFactory.create(context, type, endpoint) ?: return null
        val recorder = eventRecorder ?: return transport
        return LoggingObdTransport(transport, recorder)
    }

    fun availableTypes(): Set<TransportType> = supported

    companion object {
        fun default(context: Context, eventRecorder: DiagnosticEventRecorder? = null): TransportRegistry =
            TransportRegistry(context.applicationContext, eventRecorder)
    }
}
