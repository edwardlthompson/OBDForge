package dev.foss.obdforge.domain.shop

data class Customer(
    val id: Long,
    val name: String,
    val phone: String?,
    val email: String?,
    val createdAtEpochMs: Long,
)

enum class WorkOrderStatus {
    Intake,
    Inspecting,
    InProgress,
    Closed,
}

data class WorkOrder(
    val id: Long,
    val customerId: Long?,
    val customerName: String?,
    val vin: String,
    val status: WorkOrderStatus,
    val notes: String?,
    val sessionId: Long?,
    val openedAtEpochMs: Long,
    val closedAtEpochMs: Long?,
)

data class WorkOrderDetail(
    val order: WorkOrder,
    val customer: Customer?,
)
