package dev.foss.obdforge.data.ai

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.domain.ai.AiExplanationSource
import dev.foss.obdforge.domain.ai.DtcCatalog
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DtcManufacturerOverlayLoaderTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() {
        DtcCatalog.resetForTests()
        DtcCatalogAssetLoader.loadIntoCatalog(context)
        DtcManufacturerOverlayLoader.loadIntoCatalog(context)
    }

    @After
    fun tearDown() {
        DtcCatalog.resetForTests()
    }

    @Test
    fun load_hasThousandsOfManufacturerEntries() {
        val count = DtcManufacturerOverlayLoader.loadIntoCatalog(context)
        assertTrue(count > 8000)
    }

    @Test
    fun explain_usesManufacturerWhenVinMakeMatches() {
        val explanation = DtcCatalog.explain("P0342", "CHEVY")
        assertNotNull(explanation)
        assertEquals(AiExplanationSource.Manufacturer, explanation?.source)
    }
}
