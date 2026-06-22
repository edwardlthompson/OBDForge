package dev.foss.obdforge.data.ai

import android.content.Context
import dev.foss.obdforge.domain.ai.LocalAiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object LlmModelProvisioner {
    fun resolveModelPath(context: Context): String? {
        val cached = modelFile(context)
        if (cached.exists() && cached.length() > 0L) return cached.absolutePath
        return copyFromAssets(context, cached)?.absolutePath
    }

    fun isModelPresent(context: Context): Boolean = resolveModelPath(context) != null

    suspend fun downloadModel(
        context: Context,
        onProgress: (Float) -> Unit = {},
    ): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val destination = modelFile(context)
            val temp = File(context.filesDir, "${LocalAiConfig.MODEL_FILE_NAME}.download")
            if (temp.exists()) temp.delete()
            val connection = (URL(LocalAiConfig.MODEL_DOWNLOAD_URL).openConnection() as HttpURLConnection).apply {
                connectTimeout = 30_000
                readTimeout = 120_000
                instanceFollowRedirects = true
            }
            connection.connect()
            if (connection.responseCode !in 200..299) {
                throw IOException("Download failed: HTTP ${connection.responseCode}")
            }
            val total = connection.contentLengthLong.takeIf { it > 0 } ?: LocalAiConfig.MODEL_SIZE_BYTES
            connection.inputStream.use { input ->
                temp.outputStream().use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var downloaded = 0L
                    while (true) {
                        val read = input.read(buffer)
                        if (read <= 0) break
                        output.write(buffer, 0, read)
                        downloaded += read
                        onProgress((downloaded.toFloat() / total.toFloat()).coerceIn(0f, 1f))
                    }
                }
            }
            if (temp.length() < 1_000_000L) {
                temp.delete()
                throw IOException("Downloaded model file is too small")
            }
            if (destination.exists()) destination.delete()
            if (!temp.renameTo(destination)) {
                temp.copyTo(destination, overwrite = true)
                temp.delete()
            }
            onProgress(1f)
            destination
        }
    }

    private fun modelFile(context: Context): File =
        File(context.filesDir, LocalAiConfig.MODEL_FILE_NAME)

    private fun copyFromAssets(context: Context, destination: File): File? {
        return try {
            context.assets.open(LocalAiConfig.MODEL_ASSET_PATH).use { input ->
                destination.outputStream().use { output -> input.copyTo(output) }
            }
            destination
        } catch (_: IOException) {
            null
        }
    }
}
