package dev.foss.obdforge.domain.livedata

object PidCatalog {
    private val rpm = PidDefinition(0x0C, "Engine RPM", PidUnit.Rpm, PidDecoders.rpm())
    private val speed = PidDefinition(0x0D, "Vehicle speed", PidUnit.Kph, PidDecoders.singleByteOffset(0.0))
    private val coolant = PidDefinition(0x05, "Coolant temp", PidUnit.Celsius, PidDecoders.singleByteOffset(-40.0))
    private val load = PidDefinition(0x04, "Engine load", PidUnit.Percent, PidDecoders.singleByteScale(100.0))
    private val throttle = PidDefinition(0x11, "Throttle", PidUnit.Percent, PidDecoders.singleByteScale(100.0))
    private val intake = PidDefinition(0x0F, "Intake air temp", PidUnit.Celsius, PidDecoders.singleByteOffset(-40.0))
    private val fuel = PidDefinition(0x2F, "Fuel level", PidUnit.Percent, PidDecoders.singleByteScale(100.0))
    private val runtime = PidDefinition(0x1F, "Engine runtime", PidUnit.Seconds, PidDecoders.u16Scale(1.0))
    private val voltage = PidDefinition(0x42, "Control voltage", PidUnit.Volts, PidDecoders.u16Scale(1000.0))
    private val ambient = PidDefinition(0x46, "Ambient air temp", PidUnit.Celsius, PidDecoders.singleByteOffset(-40.0))
    private val oil = PidDefinition(0x5C, "Oil temp", PidUnit.Celsius, PidDecoders.singleByteOffset(-40.0))

    val all: List<PidDefinition> = listOf(
        rpm, speed, coolant, load, throttle, intake, fuel, runtime, voltage, ambient, oil,
    )

    private val byPid = all.associateBy { it.pid }

    fun get(pid: Int): PidDefinition? = byPid[pid]

    fun forPersona(persona: PersonaMode): List<PidDefinition> = when (persona) {
        PersonaMode.Diy -> listOf(rpm, speed, coolant, throttle)
        PersonaMode.SemiPro -> listOf(rpm, speed, coolant, load, throttle, intake)
        PersonaMode.Shop -> all
        PersonaMode.Racing -> all
    }
}
