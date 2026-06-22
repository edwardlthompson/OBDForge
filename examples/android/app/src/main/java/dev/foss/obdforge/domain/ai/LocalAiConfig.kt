package dev.foss.obdforge.domain.ai

/**
 * On-device LLM: Gemma 3 1B instruction-tuned INT4 (.task) via MediaPipe GenAI.
 *
 * Rationale: short DTC explanations, ~530 MB download, runs on mid-range phones offline.
 * See docs/LOCAL_AI.md for comparison with alternatives.
 */
object LocalAiConfig {
    const val MODEL_FILE_NAME = "llm_model.task"
    const val MODEL_ASSET_PATH = "ai/llm_model.task"
    const val MODEL_ID = "gemma3-1b-it-int4"
    const val MODEL_DISPLAY_NAME = "Gemma 3 1B IT"
    const val MODEL_DOWNLOAD_URL =
        "https://huggingface.co/litert-community/Gemma3-1B-IT/resolve/main/gemma3-1b-it-int4.task"
    const val MODEL_SIZE_BYTES = 555_000_000L
    const val MAX_OUTPUT_TOKENS = 384
}
