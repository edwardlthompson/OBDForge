package dev.foss.obdforge.data.transport

import dev.foss.obdforge.data.demo.CannedSessions
import dev.foss.obdforge.data.demo.MockSessionTransport
import dev.foss.obdforge.data.demo.SimulatedObdTransport
import dev.foss.obdforge.domain.transport.ObdTransport

/**
 * Snapshot bridge from [dev.foss.obdforge.data.preferences.DebugPreferences] into
 * [TransportRegistry] so simulated connects can swap to [MockSessionTransport].
 */
class SimulatedTransportSelector {
    @Volatile
    var useMockTransport: Boolean = false

    @Volatile
    var mockSessionId: String = CannedSessions.DEFAULT_ID

    fun create(): ObdTransport {
        return if (useMockTransport) {
            MockSessionTransport(CannedSessions.byId(mockSessionId))
        } else {
            SimulatedObdTransport()
        }
    }
}
