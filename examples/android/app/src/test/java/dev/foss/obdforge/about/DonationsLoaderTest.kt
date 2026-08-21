package dev.foss.obdforge.about

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class DonationsLoaderTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun loadsDonationsFromAssets() {
        val cfg = DonationsLoader.load(context)
        assertTrue(cfg.enabled)
        assertEquals("If this project helps you, consider supporting development.", cfg.message)
        assertEquals(1, cfg.links.size)
        assertEquals("Donate via Venmo", cfg.links[0].label)
        assertEquals(
            "https://venmo.com/code?user_id=1857304970395648420",
            cfg.links[0].url,
        )
    }

    @Test
    fun ensureVenmoAlwaysAddsQuietLink() {
        val cfg = DonationsLoader.ensureVenmo(
            DonationsConfig(enabled = false, message = "", links = emptyList()),
        )
        assertTrue(cfg.enabled)
        assertEquals("Donate via Venmo", cfg.links[0].label)
        assertEquals(ProductUpdate.DONATION_URL, cfg.links[0].url)
    }
}
