package dev.foss.obdforge.data.ai

import dev.foss.obdforge.data.demo.DemoObdFixtures
import dev.foss.obdforge.domain.ai.AiExplanationSource
import dev.foss.obdforge.domain.ai.ExplainDtcOutcome
import dev.foss.obdforge.domain.livedata.PersonaMode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExplainDtcUseCaseTest {
    private val useCase = ExplainDtcUseCase(
        llmEngine = UnavailableLlmEngine,
        classifier = NoOpDtcClassifier,
    )

    @Test
    fun explain_usesCatalogWhenLlmUnavailable() = runTest {
        val outcome = useCase.explain(DemoObdFixtures.PRIMARY_DTC, PersonaMode.Diy)
        assertTrue(outcome is ExplainDtcOutcome.Success)
        val explanation = (outcome as ExplainDtcOutcome.Success).explanation
        assertEquals(AiExplanationSource.Catalog, explanation.source)
        assertTrue(explanation.summary.isNotBlank())
    }

    @Test
    fun explain_rejectsInvalidCode() = runTest {
        assertEquals(ExplainDtcOutcome.InvalidCode, useCase.explain("INVALID", PersonaMode.Diy))
    }

    private object UnavailableLlmEngine : LocalLlmEngine {
        override val isAvailable: Boolean = false
        override suspend fun generate(prompt: String): Result<String> =
            Result.failure(IllegalStateException("offline test engine"))
        override fun close() = Unit
    }
}
