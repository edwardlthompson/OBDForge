package dev.foss.obdforge.domain.ai

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class DtcCatalogManufacturerTest {
    @After
    fun tearDown() {
        DtcCatalog.resetForTests()
    }

    @Test
    fun lookup_prefersManufacturerOverlayWhenMakeMatches() {
        DtcCatalog.installManufacturerOverlay(
            mapOf(
                "FORD" to mapOf(
                    "P1234" to DtcCatalog.CatalogEntry(
                        title = "Ford-specific P1234",
                        summary = "Manufacturer definition for Ford.",
                        severity = DtcSeverity.Medium,
                        category = "powertrain",
                    ),
                ),
            ),
        )
        DtcCatalog.install(
            mapOf(
                "P1234" to DtcCatalog.CatalogEntry(
                    title = "Generic P1234",
                    summary = "Generic definition.",
                    severity = DtcSeverity.Low,
                    category = "powertrain",
                ),
            ),
        )

        val ford = DtcCatalog.lookup("P1234", "FORD")
        assertNotNull(ford)
        assertEquals("Ford-specific P1234", ford?.title)

        val generic = DtcCatalog.lookup("P1234", "TOYOTA")
        assertEquals("Generic P1234", generic?.title)
    }

    @Test
    fun explain_marksManufacturerSource() {
        DtcCatalog.installManufacturerOverlay(
            mapOf(
                "CHEVY" to mapOf(
                    "P0133" to DtcCatalog.CatalogEntry(
                        title = "Chevy P0133",
                        summary = "Chevrolet-specific oxygen sensor code.",
                        severity = DtcSeverity.Medium,
                        category = "emissions",
                    ),
                ),
            ),
        )

        val explanation = DtcCatalog.explain("P0133", "CHEVROLET")
        assertNotNull(explanation)
        assertEquals(AiExplanationSource.Manufacturer, explanation?.source)
        assertEquals("Chevy P0133", explanation?.title)
    }

    @Test
    fun lookup_fallsBackToGenericWhenManufacturerHasNoOverlay() {
        DtcCatalog.install(
            mapOf(
                "P0420" to DtcCatalog.CatalogEntry(
                    title = "Catalyst efficiency",
                    summary = "Generic catalyst code.",
                    severity = DtcSeverity.Medium,
                    category = "emissions",
                ),
            ),
        )

        assertEquals("Catalyst efficiency", DtcCatalog.lookup("P0420", "FORD")?.title)
        assertNotNull(DtcCatalog.lookup("P0420"))
    }
}
