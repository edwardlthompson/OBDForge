package dev.foss.obdforge.ui.session

import dev.foss.obdforge.data.persistence.SessionRepository
import dev.foss.obdforge.domain.session.SessionDetail
import dev.foss.obdforge.domain.session.SessionSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionHistoryCoordinator(
    private val sessionRepository: SessionRepository,
) {
    val summaries: Flow<List<SessionSummary>> = sessionRepository.observeSummaries()

    private val _selectedDetail = MutableStateFlow<SessionDetail?>(null)
    val selectedDetail: StateFlow<SessionDetail?> = _selectedDetail.asStateFlow()

    private val _exportJson = MutableStateFlow<String?>(null)
    val exportJson: StateFlow<String?> = _exportJson.asStateFlow()

    private val _exportCsv = MutableStateFlow<String?>(null)
    val exportCsv: StateFlow<String?> = _exportCsv.asStateFlow()

    suspend fun selectSession(sessionId: Long) {
        _selectedDetail.value = sessionRepository.getDetail(sessionId)
        _exportJson.value = null
        _exportCsv.value = null
    }

    suspend fun exportSelectedJson(): String? {
        val sessionId = _selectedDetail.value?.summary?.id ?: return null
        val json = sessionRepository.exportJson(sessionId)
        _exportJson.value = json
        return json
    }

    suspend fun exportSelectedCsv(): String? {
        val sessionId = _selectedDetail.value?.summary?.id ?: return null
        val csv = sessionRepository.exportCsv(sessionId)
        _exportCsv.value = csv
        return csv
    }

    fun clearSelection() {
        _selectedDetail.value = null
        _exportJson.value = null
        _exportCsv.value = null
    }
}
