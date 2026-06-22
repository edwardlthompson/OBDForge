package dev.foss.obdforge.data.shop

import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.data.local.entity.CustomerEntity
import dev.foss.obdforge.data.local.entity.WorkOrderEntity
import dev.foss.obdforge.domain.shop.Customer
import dev.foss.obdforge.domain.shop.WorkOrder
import dev.foss.obdforge.domain.shop.WorkOrderDetail
import dev.foss.obdforge.domain.shop.WorkOrderStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShopRepository(
    database: ObdForgeDatabase,
) {
    private val customerDao = database.customerDao()
    private val workOrderDao = database.workOrderDao()
    private val sessionDao = database.sessionDao()

    fun observeOpenOrders(): Flow<List<WorkOrder>> =
        workOrderDao.observeOpenWithCustomer().map { rows ->
            rows.map { row -> row.order.toDomain(row.customerName) }
        }

    suspend fun getDetail(orderId: Long): WorkOrderDetail? {
        val entity = workOrderDao.getById(orderId) ?: return null
        val customer = entity.customerId?.let { customerDao.getById(it)?.toDomain() }
        return WorkOrderDetail(
            order = entity.toDomain(customer?.name),
            customer = customer,
        )
    }

    suspend fun createIntake(
        customerName: String,
        phone: String?,
        vin: String,
        notes: String?,
    ): WorkOrder {
        val now = System.currentTimeMillis()
        val customerId = customerDao.insert(
            CustomerEntity(
                name = customerName.trim(),
                phone = phone?.trim()?.takeIf { it.isNotEmpty() },
                email = null,
                createdAtEpochMs = now,
            ),
        )
        val orderId = workOrderDao.insert(
            WorkOrderEntity(
                customerId = customerId,
                vin = vin.trim().uppercase(),
                status = WorkOrderStatus.Intake.name,
                notes = notes?.trim()?.takeIf { it.isNotEmpty() },
                sessionId = null,
                openedAtEpochMs = now,
                closedAtEpochMs = null,
            ),
        )
        return requireNotNull(getDetail(orderId)).order
    }

    suspend fun advanceStatus(orderId: Long, status: WorkOrderStatus): WorkOrder? {
        val entity = workOrderDao.getById(orderId) ?: return null
        workOrderDao.update(entity.copy(status = status.name))
        return getDetail(orderId)?.order
    }

    suspend fun attachLatestSession(orderId: Long): WorkOrder? {
        val latestSession = sessionDao.latest() ?: return null
        val entity = workOrderDao.getById(orderId) ?: return null
        workOrderDao.update(entity.copy(sessionId = latestSession.id))
        return getDetail(orderId)?.order
    }

    suspend fun closeOut(orderId: Long, notes: String?): WorkOrder? {
        val entity = workOrderDao.getById(orderId) ?: return null
        workOrderDao.update(
            entity.copy(
                status = WorkOrderStatus.Closed.name,
                notes = notes?.trim()?.takeIf { it.isNotEmpty() } ?: entity.notes,
                closedAtEpochMs = System.currentTimeMillis(),
            ),
        )
        return getDetail(orderId)?.order
    }

    private fun WorkOrderEntity.toDomain(customerName: String? = null) = WorkOrder(
        id = id,
        customerId = customerId,
        customerName = customerName,
        vin = vin,
        status = WorkOrderStatus.valueOf(status),
        notes = notes,
        sessionId = sessionId,
        openedAtEpochMs = openedAtEpochMs,
        closedAtEpochMs = closedAtEpochMs,
    )

    private fun CustomerEntity.toDomain() = Customer(
        id = id,
        name = name,
        phone = phone,
        email = email,
        createdAtEpochMs = createdAtEpochMs,
    )
}
