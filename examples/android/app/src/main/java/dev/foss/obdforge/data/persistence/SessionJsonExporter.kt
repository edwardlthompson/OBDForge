package dev.foss.obdforge.data.persistence

import dev.foss.obdforge.domain.session.SessionDetail
import org.json.JSONArray
import org.json.JSONObject

object SessionJsonExporter {
    fun export(detail: SessionDetail): String {
        val root = JSONObject()
        val summary = detail.summary
        root.put("id", summary.id)
        root.put("startedAtEpochMs", summary.startedAtEpochMs)
        root.put("endedAtEpochMs", summary.endedAtEpochMs)
        root.put("transportType", summary.transportType)
        root.put("protocolId", summary.protocolId)
        root.put("vin", summary.vin)
        root.put(
            "dtcSnapshots",
            JSONArray().apply {
                detail.dtcSnapshots.forEach { snapshot ->
                    put(
                        JSONObject().apply {
                            put("capturedAtEpochMs", snapshot.capturedAtEpochMs)
                            put("codes", JSONArray(snapshot.codes))
                            put("rawResponse", snapshot.rawResponse)
                        },
                    )
                }
            },
        )
        root.put(
            "freezeFrames",
            JSONArray().apply {
                detail.freezeFrames.forEach { frame ->
                    put(
                        JSONObject().apply {
                            put("dtcCode", frame.dtcCode)
                            put("capturedAtEpochMs", frame.capturedAtEpochMs)
                            put("pidValues", JSONObject(frame.pidValues))
                        },
                    )
                }
            },
        )
        return root.toString(2)
    }
}
