package dev.foss.obdforge.ui.shop

import dev.foss.obdforge.data.shop.ShopRepository
import dev.foss.obdforge.domain.shop.WorkOrder
import dev.foss.obdforge.domain.shop.WorkOrderDetail
import dev.foss.obdforge.domain.shop.WorkOrderStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ShopCoordinator(
    private val shopRepository: ShopRepository,
) {
    val openOrders: Flow<List<WorkOrder>> = shopRepository.observeOpenOrders()

    private val _selectedDetail = MutableStateFlow<WorkOrderDetail?>(null)
    val selectedDetail: StateFlow<WorkOrderDetail?> = _selectedDetail.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    suspend fun selectOrder(orderId: Long) {
        _selectedDetail.value = shopRepository.getDetail(orderId)
    }

    suspend fun createIntake(
        customerName: String,
        phone: String?,
        vin: String,
        notes: String?,
    ): WorkOrder? {
        if (customerName.isBlank() || vin.length != 17) {
            _statusMessage.value = STATUS_INVALID_INTAKE
            return null
        }
        val order = shopRepository.createIntake(customerName, phone, vin, notes)
        _statusMessage.value = null
        return order
    }

    suspend fun beginInspection(orderId: Long) {
        shopRepository.advanceStatus(orderId, WorkOrderStatus.Inspecting)
        selectOrder(orderId)
    }

    suspend fun markInProgress(orderId: Long) {
        shopRepository.advanceStatus(orderId, WorkOrderStatus.InProgress)
        selectOrder(orderId)
    }

    suspend fun attachLatestSession(orderId: Long) {
        val updated = shopRepository.attachLatestSession(orderId)
        if (updated == null) {
            _statusMessage.value = STATUS_NO_SESSION
        } else {
            _statusMessage.value = null
            selectOrder(orderId)
        }
    }

    suspend fun closeOut(orderId: Long, notes: String?) {
        shopRepository.closeOut(orderId, notes)
        _selectedDetail.value = null
    }

    fun clearSelection() {
        _selectedDetail.value = null
        _statusMessage.value = null
    }

    companion object {
        const val STATUS_INVALID_INTAKE = "shop_error_invalid_intake"
        const val STATUS_NO_SESSION = "shop_error_no_session"
    }
}
