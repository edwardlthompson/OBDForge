package dev.foss.obdforge.domain.flash

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FlashTransportPolicyTest {
    @Test
    fun allowsUsbAndDemo_rejectsBluetoothAndWifi() {
        assertTrue(FlashTransportPolicy.allows(dev.foss.obdforge.domain.transport.TransportType.UsbSerial))
        assertTrue(FlashTransportPolicy.allows(dev.foss.obdforge.domain.transport.TransportType.Simulated))
        assertFalse(FlashTransportPolicy.allows(dev.foss.obdforge.domain.transport.TransportType.Bluetooth))
        assertFalse(FlashTransportPolicy.allows(dev.foss.obdforge.domain.transport.TransportType.WiFi))
        assertFalse(FlashTransportPolicy.allows(dev.foss.obdforge.domain.transport.TransportType.Ethernet))
        assertFalse(FlashTransportPolicy.allows(null))
    }
}

class IsoTpCodecTest {
    @Test
    fun singleFrame_roundTrip() {
        val payload = byteArrayOf(0x22, 0xF1.toByte(), 0x90.toByte())
        val frames = IsoTpCodec.encode(payload)
        assertEquals(1, frames.size)
        assertEquals(payload.toList(), IsoTpCodec.decode(frames).toList())
    }

    @Test
    fun multiFrame_roundTrip() {
        val payload = ByteArray(20) { it.toByte() }
        val frames = IsoTpCodec.encode(payload)
        assertTrue(frames.size > 1)
        assertEquals(payload.toList(), IsoTpCodec.decode(frames).toList())
    }
}

class SecurityAccessPluginTest {
    @Test
    fun rejectingPlugin_fails() {
        assertTrue(RejectingSecurityAccessPlugin.keyFor(byteArrayOf(1), 1, "ecu").isFailure)
    }

    @Test
    fun demoPlugin_onlyForDemoIds() {
        assertTrue(DemoSecurityAccessPlugin.keyFor(byteArrayOf(1), 1, "demo-isotp-v1").isSuccess)
        assertTrue(DemoSecurityAccessPlugin.keyFor(byteArrayOf(1), 1, "OEM-ECU").isFailure)
    }

    @Test
    fun manualKey_parsesHex() {
        val key = ManualKeySecurityAccessPlugin("DEADBEEF").keyFor(byteArrayOf(), 1, "x").getOrThrow()
        assertEquals(listOf(0xDE, 0xAD, 0xBE, 0xEF), key.map { it.toInt() and 0xFF })
    }
}

class FlashSecurityAssistTest {
    @Test
    fun matchInstalledPlugin_byPrefix() {
        SecurityAccessPluginCatalog.reset()
        SecurityAccessPluginCatalog.register(
            SecurityPluginDescriptor(
                pluginId = "user.demo-pack",
                displayName = "Demo pack",
                ecuIdPatterns = listOf("demo*"),
            ),
        )
        assertEquals(1, FlashSecurityAssist.matchInstalledPlugin("demo-isotp-v1").size)
        assertTrue(FlashSecurityAssist.matchInstalledPlugin("OEM-ECU").isEmpty())
        SecurityAccessPluginCatalog.reset()
    }

    @Test
    fun assist_refusesKeyGeneration() {
        val result = FlashSecurityAssist.assist("please generate key from seed")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("cannot generate") == true)
    }

    @Test
    fun assist_explainsNrc() {
        val text = FlashSecurityAssist.assist("what does this mean", nrc = 0x33).getOrThrow()
        assertTrue(text.contains("0x33"))
        assertTrue(text.contains("Security access denied"))
    }
}

class FlashTransferEngineTest {
    @Test
    fun plan_rejectsEmptyBinary() {
        val engine = FlashTransferEngine(DemoSecurityAccessPlugin)
        assertTrue(engine.plan(DemoEcuFlashProfile.profile, ByteArray(0)).isFailure)
    }

    @Test
    fun planAndDemoExecute_withDemoSecurity() {
        val engine = FlashTransferEngine()
        val binary = ByteArray(40) { 0xAA.toByte() }
        val plan = engine.plan(
            DemoEcuFlashProfile.profile,
            binary,
            allowDemoSecurity = true,
        ).getOrThrow()
        assertTrue(plan.totalBlocks >= 1)
        assertTrue(plan.steps.any { it.serviceId == 0x34 })
        assertTrue(plan.steps.any { it.serviceId == 0x36 })
        assertTrue(plan.steps.any { it.serviceId == 0x37 })
        val responses = engine.executeDemo(plan).getOrThrow()
        assertEquals(plan.steps.size, responses.size)
    }

    @Test
    fun plan_withoutPlugin_failsClosed() {
        SecurityAccessRegistry.reset()
        val engine = FlashTransferEngine()
        assertTrue(
            engine.plan(DemoEcuFlashProfile.profile, byteArrayOf(1), allowDemoSecurity = false).isFailure,
        )
    }
}
