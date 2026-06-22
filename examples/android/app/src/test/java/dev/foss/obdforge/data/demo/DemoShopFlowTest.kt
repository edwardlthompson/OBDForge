package dev.foss.obdforge.data.demo

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.data.persistence.SessionRepository
import dev.foss.obdforge.data.shop.ShopRepository
import dev.foss.obdforge.domain.shop.WorkOrderStatus
import dev.foss.obdforge.domain.vehicle.VinResolver
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DemoShopFlowTest {
    private lateinit var database: ObdForgeDatabase
    private lateinit var shopRepository: ShopRepository
    private lateinit var sessionRepository: SessionRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ObdForgeDatabase::class.java).build()
        shopRepository = ShopRepository(database)
        sessionRepository = SessionRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun intakeInspectCloseOut_linksSessionWithoutHardware() = runTest {
        val sessionId = sessionRepository.startSession("Simulated", "elm327", VinResolver.DEMO_VIN)
        sessionRepository.endSession(sessionId)

        val order = shopRepository.createIntake(
            customerName = "Demo Customer",
            phone = null,
            vin = VinResolver.DEMO_VIN,
            notes = "Intake from simulation",
        )
        shopRepository.advanceStatus(order.id, WorkOrderStatus.Inspecting)
        val linked = shopRepository.attachLatestSession(order.id)
        assertNotNull(linked)
        assertEquals(sessionId, linked!!.sessionId)

        shopRepository.advanceStatus(order.id, WorkOrderStatus.InProgress)
        val closed = shopRepository.closeOut(order.id, "Ready for pickup")
        assertEquals(WorkOrderStatus.Closed, closed!!.status)
    }
}
