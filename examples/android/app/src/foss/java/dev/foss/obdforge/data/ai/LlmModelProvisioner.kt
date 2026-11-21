package dev.foss.obdforge.data.ai

import android.content.Context
import java.io.File

/** F-Droid flavor: no Gemma download and no bundled MediaPipe model. */
object LlmModelProvisioner {
    fun resolveModelPath(context: Context): String? = null

    fun isModelPresent(context: Context): Boolean = false

    @Suppress("UNUSED_PARAMETER")
    suspend fun downloadModel(
        context: Context,
        onProgress: (Float) -> Unit = {},
    ): Result<File> = Result.failure(
        UnsupportedOperationException("On-device LLM is not included in the F-Droid build"),
    )
}
