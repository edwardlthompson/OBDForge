package dev.foss.obdforge.data.ai

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.domain.ai.DtcCatalog
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DtcCatalogAssetLoaderTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() {
        DtcCatalog.resetForTests()
        DtcCatalogAssetLoader.loadIntoCatalog(context)
    }

    @After
    fun tearDown() {
        DtcCatalog.resetForTests()
    }

    @Test
    fun load_includesKnownGenericCodes() {
        assertNotNull(DtcCatalog.lookup("P0133"))
        assertNotNull(DtcCatalog.lookup("P0420"))
        assertNotNull(DtcCatalog.explain("P0171"))
    }

    @Test
    fun load_hasThousandsOfEntries() {
        val count = DtcCatalogAssetLoader.loadIntoCatalog(context)
        assertTrue(count > 9000)
    }

    @Test
    fun unknownCode_returnsNull() {
        assertNull(DtcCatalog.explain("P9999"))
    }
}
