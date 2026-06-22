package dev.foss.obdforge.data.demo

import org.junit.Assert.assertEquals
import org.junit.Test

class DemoObdFixturesTest {
    @Test
    fun responseFor_isDeterministic() {
        assertEquals("ELM327 v2.3", DemoObdFixtures.responseFor("ATZ"))
        assertEquals("41 0C 0F A0", DemoObdFixtures.responseFor("010C"))
        assertEquals("43 01 33 00 00 00 00 00", DemoObdFixtures.responseFor("03"))
    }

    @Test
    fun mode03Fixture_containsPrimaryDtcBytes() {
        val response = DemoObdFixtures.responseFor("03")
        assert(response.contains("33"))
    }
}
