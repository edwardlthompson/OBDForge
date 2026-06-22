package dev.foss.obdforge.data.registry

import dev.foss.obdforge.data.demo.SimulatedObdTransport
import dev.foss.obdforge.domain.transport.Transport
import dev.foss.obdforge.domain.transport.TransportType

class TransportRegistry {
    private val factories = linkedMapOf<TransportType, () -> Transport>()

    fun register(type: TransportType, factory: () -> Transport) {
        factories[type] = factory
    }

    fun create(type: TransportType): Transport? = factories[type]?.invoke()

    fun availableTypes(): Set<TransportType> = factories.keys

    companion object {
        fun default(): TransportRegistry =
            TransportRegistry().apply {
                register(TransportType.Simulated) { SimulatedObdTransport() }
            }
    }
}
