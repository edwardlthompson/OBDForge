package dev.foss.obdforge.domain.vehicle

import dev.foss.obdforge.data.demo.DemoObdFixtures
import dev.foss.obdforge.data.demo.SimulatedObdTransport
import dev.foss.obdforge.domain.transport.ConnectionState
import dev.foss.obdforge.domain.transport.ObdTransport
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportMetrics
import dev.foss.obdforge.domain.transport.TransportType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class VinResolverTest {
    @Test
    fun parseMode09Vin_extracts17Chars() {
        val response = "49 02 01 31 47 31 4A 43 35 34 34 32 52 37 32 35 31 32 33 34"
        val vin = VinResolver.parseMode09Vin(response)
        assertNotNull(vin)
        assertEquals(17, vin!!.length)
        assertEquals(VinResolver.DEMO_VIN, vin)
    }

    @Test
    fun demoVin_isValidLength() {
        assertEquals(17, VinResolver.demoVin().vin.length)
    }

    @Test
    fun resolveFromEcu_demoTransportUsesMode09First() = runTest {
        val transport = SimulatedObdTransport()
        val result = VinResolver.resolveFromEcu(transport)
        assertNotNull(result)
        assertEquals(VinSourceType.EcuObd2, result!!.source)
        assertEquals(DemoObdFixtures.DEMO_VIN, result.vin)
    }

    @Test
    fun resolveFromEcu_fallsBackWhenMode09Fails() = runTest {
        val transport = FallbackVinTransport(
            responses = mapOf(
                "0902" to "NO DATA",
                "22 F1 90" to DemoObdFixtures.responseFor("22 F1 90"),
            ),
        )
        val result = VinResolver.resolveFromEcu(transport)
        assertNotNull(result)
        assertEquals(VinSourceType.EcuUds, result!!.source)
        assertEquals(DemoObdFixtures.DEMO_VIN, result.vin)
    }

    @Test
    fun fromManual_rejectsInvalidVin() {
        assertNull(VinResolver.fromManual("TOO SHORT"))
    }

    private class FallbackVinTransport(
        private val responses: Map<String, String>,
    ) : ObdTransport {
        override val type = TransportType.Simulated
        override val endpoint = TransportEndpoint.Simulated
        override var state = ConnectionState.Connected
        override val metrics = TransportMetrics()

        override suspend fun connect(): Result<Unit> = Result.success(Unit)
        override suspend fun disconnect() {
            state = ConnectionState.Disconnected
        }
        override suspend fun write(line: String) = Result.success(Unit)
        override suspend fun read(timeoutMs: Long) = Result.success("OK")
        override suspend fun send(command: String): Result<String> {
            val key = command.trim().uppercase()
            return Result.success(responses[key] ?: "NO DATA")
        }
    }
}
