package dev.foss.obdforge.data.shop

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.domain.shop.WorkOrderStatus
import dev.foss.obdforge.domain.vehicle.VinResolver
import kotlinx.coroutines.flow.first
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
class ShopRepositoryTest {
    private lateinit var database: ObdForgeDatabase
    private lateinit var repository: ShopRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ObdForgeDatabase::class.java).build()
        repository = ShopRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun createIntake_andCloseOut() = runTest {
        val order = repository.createIntake(
            customerName = "Alex Driver",
            phone = "555-0100",
            vin = VinResolver.DEMO_VIN,
            notes = "Check engine light",
        )
        assertEquals(WorkOrderStatus.Intake, order.status)
        assertEquals("Alex Driver", order.customerName)

        repository.advanceStatus(order.id, WorkOrderStatus.Inspecting)
        repository.advanceStatus(order.id, WorkOrderStatus.InProgress)
        val closed = repository.closeOut(order.id, "Replaced O2 sensor")
        assertNotNull(closed)
        assertEquals(WorkOrderStatus.Closed, closed!!.status)

        val openOrders = repository.observeOpenOrders().first()
        assertEquals(0, openOrders.size)
    }
}
