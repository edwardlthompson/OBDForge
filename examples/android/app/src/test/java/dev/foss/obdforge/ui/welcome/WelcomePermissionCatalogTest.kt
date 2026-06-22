package dev.foss.obdforge.ui.welcome

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class WelcomePermissionCatalogTest {
    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun bluetoothNotRequiredOnApi28() {
        assertFalse(WelcomePermissionCatalog.bluetoothRequired())
        assertTrue(WelcomePermissionCatalog.bluetoothGranted(context))
    }

    @Test
    fun cameraNotGrantedByDefault() {
        assertFalse(WelcomePermissionCatalog.cameraGranted(context))
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class WelcomePermissionCatalogApi31Test {
    @Test
    fun bluetoothRequiredOnApi31() {
        assertTrue(WelcomePermissionCatalog.bluetoothRequired())
    }
}
