package dev.foss.obdforge.domain.protocol

enum class ObdMode(val wirePrefix: String) {
    Mode01("01"),
    Mode02("02"),
    Mode03("03"),
    Mode04("04"),
    Mode07("07"),
    Mode09("09"),
}
