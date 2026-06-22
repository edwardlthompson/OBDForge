package dev.foss.obdforge.data.ai

import android.content.Context
import dev.foss.obdforge.domain.ai.DtcCatalog
import dev.foss.obdforge.domain.ai.DtcClassification
import dev.foss.obdforge.domain.ai.DtcSeverity
import org.tensorflow.lite.Interpreter
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TfliteDtcClassifier(
    context: Context,
) : DtcClassifier {
    private val interpreter: Interpreter? = loadInterpreter(context)

    override val isAvailable: Boolean
        get() = interpreter != null

    override fun classify(code: String): DtcClassification? {
        val model = interpreter ?: return null
        val entry = DtcCatalog.lookup(code) ?: return null
        val input = encodeInput(code)
        val output = Array(1) { FloatArray(LABELS.size) }
        model.run(input, output)
        val index = output[0].indices.maxByOrNull { output[0][it] } ?: return null
        return DtcClassification(
            severity = severityForLabel(LABELS[index]),
            category = entry.category,
            confidence = output[0][index],
        )
    }

    override fun close() {
        interpreter?.close()
    }

    private fun encodeInput(code: String): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(FEATURE_SIZE * 4).order(ByteOrder.nativeOrder())
        val digits = code.filter { it.isDigit() }.padEnd(4, '0').take(4)
        digits.forEach { char ->
            buffer.putFloat(char.digitToInt().toFloat() / 9f)
        }
        repeat(FEATURE_SIZE - 4) { buffer.putFloat(0f) }
        buffer.rewind()
        return buffer
    }

    private fun severityForLabel(label: String): DtcSeverity = when (label) {
        "low" -> DtcSeverity.Low
        "medium" -> DtcSeverity.Medium
        "high" -> DtcSeverity.High
        else -> DtcSeverity.Unknown
    }

    companion object {
        const val MODEL_ASSET = "ai/dtc_classifier.tflite"
        private const val FEATURE_SIZE = 8
        private val LABELS = listOf("low", "medium", "high")

        fun isAssetBundled(context: Context): Boolean =
            try {
                context.assets.open(MODEL_ASSET).close()
                true
            } catch (_: IOException) {
                false
            }

        private fun loadInterpreter(context: Context): Interpreter? {
            return try {
                context.assets.open(MODEL_ASSET).use { stream ->
                    val bytes = stream.readBytes()
                    Interpreter(ByteBuffer.allocateDirect(bytes.size).apply {
                        order(ByteOrder.nativeOrder())
                        put(bytes)
                        rewind()
                    })
                }
            } catch (_: IOException) {
                null
            }
        }
    }
}
