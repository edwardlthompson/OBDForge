package dev.foss.obdforge.data.persistence

import dev.foss.obdforge.domain.safety.AuditLogRecord
import org.json.JSONArray
import org.json.JSONObject

object AuditLogJsonExporter {
    fun export(records: List<AuditLogRecord>): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put(
            "entries",
            JSONArray().apply {
                records.forEach { record ->
                    put(
                        JSONObject().apply {
                            put("id", record.id)
                            put("timestampEpochMs", record.timestampEpochMs)
                            put("persona", record.persona)
                            put("protocolId", record.protocolId)
                            put("commandType", record.commandType)
                            put("commandHash", record.commandHash)
                            put("outcome", record.outcome)
                            put("userNote", record.userNote)
                        },
                    )
                }
            },
        )
        return root.toString(2)
    }
}
