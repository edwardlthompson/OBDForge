package dev.foss.obdforge.domain.flash

/**
 * ISO-TP framing facade used by the programming session (Stage A: encode/decode only).
 */
object IsoTpLink {
    fun frame(payload: ByteArray): List<ByteArray> = IsoTpCodec.encode(payload)

    fun deframe(frames: List<ByteArray>): ByteArray = IsoTpCodec.decode(frames)
}
