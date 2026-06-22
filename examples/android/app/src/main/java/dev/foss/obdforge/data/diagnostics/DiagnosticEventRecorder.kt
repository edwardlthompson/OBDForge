package dev.foss.obdforge.data.diagnostics

import dev.foss.obdforge.data.persistence.DiagnosticEventRepository
import dev.foss.obdforge.data.preferences.DiagnosticLogPreferences
import dev.foss.obdforge.domain.diagnostics.DiagnosticEventCategory
import dev.foss.obdforge.domain.diagnostics.DiagnosticEventSeverity
import dev.foss.obdforge.domain.transport.TransportType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DiagnosticEventRecorder(
    private val repository: DiagnosticEventRepository,
    private val preferences: DiagnosticLogPreferences,
    private val scope: CoroutineScope,
) {
    fun record(
        category: DiagnosticEventCategory,
        severity: DiagnosticEventSeverity,
        message: String,
        transportType: TransportType? = null,
        protocolId: String? = null,
        detail: String? = null,
    ) {
        scope.launch {
            if (!preferences.isEnabled()) return@launch
            repository.record(
                category = category,
                severity = severity,
                message = DiagnosticLogSanitizer.sanitize(message),
                transportType = transportType?.name,
                protocolId = protocolId,
                detail = detail?.let(DiagnosticLogSanitizer::sanitize),
            )
        }
    }

    suspend fun recordNow(
        category: DiagnosticEventCategory,
        severity: DiagnosticEventSeverity,
        message: String,
        transportType: TransportType? = null,
        protocolId: String? = null,
        detail: String? = null,
    ) {
        if (!preferences.isEnabled()) return
        repository.record(
            category = category,
            severity = severity,
            message = DiagnosticLogSanitizer.sanitize(message),
            transportType = transportType?.name,
            protocolId = protocolId,
            detail = detail?.let(DiagnosticLogSanitizer::sanitize),
        )
    }

    fun recordConnection(
        transportType: TransportType,
        success: Boolean,
        latencyMs: Long? = null,
        errorMessage: String? = null,
    ) {
        val message = if (success) {
            "Connected (${latencyMs ?: 0} ms)"
        } else {
            "Connect failed: ${errorMessage ?: "unknown error"}"
        }
        record(
            category = DiagnosticEventCategory.Connection,
            severity = if (success) DiagnosticEventSeverity.Info else DiagnosticEventSeverity.Error,
            message = message,
            transportType = transportType,
        )
    }

    fun recordProtocolFailure(transportType: TransportType, message: String) {
        record(
            category = DiagnosticEventCategory.Protocol,
            severity = DiagnosticEventSeverity.Error,
            message = message,
            transportType = transportType,
        )
    }

    fun recordTransportIo(
        transportType: TransportType,
        operation: String,
        errorMessage: String,
    ) {
        record(
            category = DiagnosticEventCategory.TransportIo,
            severity = DiagnosticEventSeverity.Warn,
            message = "$operation failed: $errorMessage",
            transportType = transportType,
        )
    }
}
