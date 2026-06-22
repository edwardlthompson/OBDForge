package dev.foss.obdforge.data.demo

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.domain.protocol.ProtocolId
import dev.foss.obdforge.domain.vehicle.VinSourceType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DemoDiagnosticFlowTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun run_connectsReadsVinDtcsAndPidWithoutHardware() = runTest {
        val result = DemoDiagnosticFlow.run(
            transportRegistry = TransportRegistry.default(context),
            protocolRegistry = ProtocolRegistry.default(),
        ).getOrThrow()

        assertEquals(DemoObdFixtures.DEMO_VIN, result.vin.vin)
        assertEquals(VinSourceType.EcuObd2, result.vin.source)
        assertEquals(DemoObdFixtures.PRIMARY_DTC, result.dtcs.entries.first().code)
        assertEquals(0x0C, result.rpm.pid)
        assertEquals(ProtocolId.Elm327, result.protocol.id)
    }
}
