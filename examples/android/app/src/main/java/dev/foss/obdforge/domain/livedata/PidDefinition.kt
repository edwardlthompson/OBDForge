package dev.foss.obdforge.domain.livedata

enum class PidUnit(val symbol: String) {
    Percent("%"),
    Rpm("rpm"),
    Kph("km/h"),
    Celsius("°C"),
    Volts("V"),
    Seconds("s"),
    Lambda("λ"),
    GramsPerSec("g/s"),
    Kpa("kPa"),
    None(""),
}

data class PidDefinition(
    val pid: Int,
    val name: String,
    val unit: PidUnit,
    val decode: (ByteArray) -> Double?,
)
