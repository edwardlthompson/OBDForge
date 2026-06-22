package dev.foss.obdforge.data.ai

import dev.foss.obdforge.domain.ai.DtcClassification
import dev.foss.obdforge.domain.ai.DtcSeverity

interface DtcClassifier {
    val isAvailable: Boolean
    fun classify(code: String): DtcClassification?
    fun close()
}

object NoOpDtcClassifier : DtcClassifier {
    override val isAvailable: Boolean = false
    override fun classify(code: String): DtcClassification? = null
    override fun close() = Unit
}
