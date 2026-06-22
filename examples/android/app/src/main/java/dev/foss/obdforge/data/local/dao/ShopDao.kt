package dev.foss.obdforge.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.foss.obdforge.data.local.entity.CustomerEntity
import dev.foss.obdforge.data.local.entity.WorkOrderEntity
import kotlinx.coroutines.flow.Flow

data class WorkOrderWithCustomerRow(
    @Embedded val order: WorkOrderEntity,
    val customerName: String?,
)

@Dao
interface CustomerDao {
    @Insert
    suspend fun insert(customer: CustomerEntity): Long

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CustomerEntity?
}

@Dao
interface WorkOrderDao {
    @Insert
    suspend fun insert(order: WorkOrderEntity): Long

    @Update
    suspend fun update(order: WorkOrderEntity)

    @Query("SELECT * FROM work_orders WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): WorkOrderEntity?

    @Query(
        """
        SELECT wo.*, c.name AS customerName FROM work_orders wo
        LEFT JOIN customers c ON wo.customerId = c.id
        WHERE wo.status != 'Closed'
        ORDER BY wo.openedAtEpochMs DESC
        """,
    )
    fun observeOpenWithCustomer(): Flow<List<WorkOrderWithCustomerRow>>
}
