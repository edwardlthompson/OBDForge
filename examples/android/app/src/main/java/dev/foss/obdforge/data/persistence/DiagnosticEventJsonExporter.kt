package dev.foss.obdforge.data.persistence

import dev.foss.obdforge.BuildConfig
import dev.foss.obdforge.domain.diagnostics.DiagnosticEventRecord
import org.json.JSONArray
import org.json.JSONObject

object DiagnosticEventJsonExporter {
    fun export(records: List<DiagnosticEventRecord>): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("appVersion", BuildConfig.VERSION_NAME)
        root.put("exportedAtEpochMs", System.currentTimeMillis())
        root.put(
            "entries",
            JSONArray().apply {
                records.forEach { record ->
                    put(
                        JSONObject().apply {
                            put("id", record.id)
                            put("timestampEpochMs", record.timestampEpochMs)
                            put("category", record.category.name)
                            put("severity", record.severity.name)
                            put("transportType", record.transportType)
                            put("protocolId", record.protocolId)
                            put("message", record.message)
                            put("detail", record.detail)
                        },
                    )
                }
            },
        )
        return root.toString(2)
    }
}
