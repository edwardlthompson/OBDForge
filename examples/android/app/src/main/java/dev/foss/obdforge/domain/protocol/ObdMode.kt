package dev.foss.obdforge.domain.protocol

enum class ObdMode(val wirePrefix: String) {
    Mode01("01"),
    Mode03("03"),
    Mode04("04"),
    Mode09("09"),
}
