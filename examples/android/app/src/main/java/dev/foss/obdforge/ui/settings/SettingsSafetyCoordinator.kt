package dev.foss.obdforge.ui.settings

import dev.foss.obdforge.data.ObdForgeCompositionRoot
import dev.foss.obdforge.data.preferences.ExpertUnlockPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsSafetyCoordinator(
    private val root: ObdForgeCompositionRoot,
) {
    private val expertUnlockPreferences: ExpertUnlockPreferences = root.expertUnlockPreferences

    private val _auditEntryCount = MutableStateFlow(0)
    val auditEntryCount: StateFlow<Int> = _auditEntryCount.asStateFlow()

    private val _auditExportJson = MutableStateFlow<String?>(null)
    val auditExportJson: StateFlow<String?> = _auditExportJson.asStateFlow()

    private val _expertPinError = MutableStateFlow<String?>(null)
    val expertPinError: StateFlow<String?> = _expertPinError.asStateFlow()

    val unlockExpiresAtMs = expertUnlockPreferences.unlockExpiresAtMs

    suspend fun refreshAuditCount() {
        _auditEntryCount.value = root.auditLogRepository.count()
    }

    suspend fun exportAuditLog() {
        _auditExportJson.value = root.auditLogRepository.exportJson()
    }

    suspend fun unlockExpert(pin: String): Boolean {
        val success = expertUnlockPreferences.unlock(pin)
        _expertPinError.value = if (success) null else "invalid_pin"
        return success
    }

    suspend fun lockExpert() {
        expertUnlockPreferences.clearUnlock()
        _expertPinError.value = null
    }

    fun isExpertUnlocked(expiresAtMs: Long?, nowMs: Long = System.currentTimeMillis()): Boolean =
        expertUnlockPreferences.isUnlocked(expiresAtMs, nowMs)
}
