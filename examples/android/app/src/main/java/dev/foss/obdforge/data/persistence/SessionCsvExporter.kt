package dev.foss.obdforge.data.persistence

import dev.foss.obdforge.domain.session.SessionDetail

object SessionCsvExporter {
    fun export(detail: SessionDetail): String {
        val lines = mutableListOf<String>()
        lines += "section,field,value"
        appendSession(lines, detail)
        detail.dtcSnapshots.forEachIndexed { index, snapshot ->
            lines += "dtc,index,$index"
            lines += "dtc,captured_at,${snapshot.capturedAtEpochMs}"
            lines += "dtc,codes,${csvField(snapshot.codes.joinToString(";"))}"
            lines += "dtc,raw_response,${csvField(snapshot.rawResponse)}"
        }
        detail.freezeFrames.forEachIndexed { index, frame ->
            lines += "freeze_frame,index,$index"
            lines += "freeze_frame,dtc_code,${csvField(frame.dtcCode)}"
            lines += "freeze_frame,captured_at,${frame.capturedAtEpochMs}"
            frame.pidValues.forEach { (pid, value) ->
                lines += "freeze_frame,pid_${pid},${csvField(value)}"
            }
        }
        return lines.joinToString("\n")
    }

    private fun appendSession(lines: MutableList<String>, detail: SessionDetail) {
        val summary = detail.summary
        lines += "session,id,${summary.id}"
        lines += "session,started_at,${summary.startedAtEpochMs}"
        lines += "session,ended_at,${summary.endedAtEpochMs ?: ""}"
        lines += "session,transport,${csvField(summary.transportType)}"
        lines += "session,protocol,${csvField(summary.protocolId ?: "")}"
        lines += "session,vin,${csvField(summary.vin ?: "")}"
        lines += "session,dtc_count,${summary.dtcCount}"
    }

    private fun csvField(value: String): String =
        if (value.any { it == ',' || it == '"' || it == '\n' }) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
}
