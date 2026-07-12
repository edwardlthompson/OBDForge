package dev.foss.obdforge.domain.livedata

object PidCatalog {
    private val fuelLoop = PidDefinition(
        FuelSystemStatus.PID,
        "Fuel loop",
        PidUnit.None,
        PidDecoders.fuelSystemStatus(),
    )
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

    private val stft1 = PidDefinition(0x06, "STFT Bank 1", PidUnit.Percent, PidDecoders.fuelTrim())
    private val ltft1 = PidDefinition(0x07, "LTFT Bank 1", PidUnit.Percent, PidDecoders.fuelTrim())
    private val stft2 = PidDefinition(0x08, "STFT Bank 2", PidUnit.Percent, PidDecoders.fuelTrim())
    private val ltft2 = PidDefinition(0x09, "LTFT Bank 2", PidUnit.Percent, PidDecoders.fuelTrim())
    private val map = PidDefinition(0x0B, "MAP", PidUnit.Kpa, PidDecoders.mapKpa())
    private val maf = PidDefinition(0x10, "MAF", PidUnit.GramsPerSec, PidDecoders.maf())

    private val o2B1S1 = PidDefinition(0x14, "O2 B1S1 (NB)", PidUnit.Volts, PidDecoders.o2Voltage())
    private val o2B1S2 = PidDefinition(0x15, "O2 B1S2 (NB)", PidUnit.Volts, PidDecoders.o2Voltage())
    private val o2B1S3 = PidDefinition(0x16, "O2 B1S3 (NB)", PidUnit.Volts, PidDecoders.o2Voltage())
    private val o2B1S4 = PidDefinition(0x17, "O2 B1S4 (NB)", PidUnit.Volts, PidDecoders.o2Voltage())
    private val o2B2S1 = PidDefinition(0x18, "O2 B2S1 (NB)", PidUnit.Volts, PidDecoders.o2Voltage())
    private val o2B2S2 = PidDefinition(0x19, "O2 B2S2 (NB)", PidUnit.Volts, PidDecoders.o2Voltage())
    private val o2B2S3 = PidDefinition(0x1A, "O2 B2S3 (NB)", PidUnit.Volts, PidDecoders.o2Voltage())
    private val o2B2S4 = PidDefinition(0x1B, "O2 B2S4 (NB)", PidUnit.Volts, PidDecoders.o2Voltage())

    private val lambda24 = PidDefinition(0x24, "O2 B1S1 (WB λ)", PidUnit.Lambda, PidDecoders.widebandLambda())
    private val lambda25 = PidDefinition(0x25, "O2 B1S2 (WB λ)", PidUnit.Lambda, PidDecoders.widebandLambda())
    private val lambda26 = PidDefinition(0x26, "O2 B1S3 (WB λ)", PidUnit.Lambda, PidDecoders.widebandLambda())
    private val lambda27 = PidDefinition(0x27, "O2 B1S4 (WB λ)", PidUnit.Lambda, PidDecoders.widebandLambda())
    private val lambda28 = PidDefinition(0x28, "O2 B2S1 (WB λ)", PidUnit.Lambda, PidDecoders.widebandLambda())
    private val lambda29 = PidDefinition(0x29, "O2 B2S2 (WB λ)", PidUnit.Lambda, PidDecoders.widebandLambda())
    private val lambda2A = PidDefinition(0x2A, "O2 B2S3 (WB λ)", PidUnit.Lambda, PidDecoders.widebandLambda())
    private val lambda2B = PidDefinition(0x2B, "O2 B2S4 (WB λ)", PidUnit.Lambda, PidDecoders.widebandLambda())
    private val lambda34 = PidDefinition(0x34, "O2 B1S1 (WB λ alt)", PidUnit.Lambda, PidDecoders.widebandLambda())
    private val lambda35 = PidDefinition(0x35, "O2 B1S2 (WB λ alt)", PidUnit.Lambda, PidDecoders.widebandLambda())
    private val lambda36 = PidDefinition(0x36, "O2 B1S3 (WB λ alt)", PidUnit.Lambda, PidDecoders.widebandLambda())
    private val lambda37 = PidDefinition(0x37, "O2 B1S4 (WB λ alt)", PidUnit.Lambda, PidDecoders.widebandLambda())
    private val lambda38 = PidDefinition(0x38, "O2 B2S1 (WB λ alt)", PidUnit.Lambda, PidDecoders.widebandLambda())
    private val lambda39 = PidDefinition(0x39, "O2 B2S2 (WB λ alt)", PidUnit.Lambda, PidDecoders.widebandLambda())
    private val lambda3A = PidDefinition(0x3A, "O2 B2S3 (WB λ alt)", PidUnit.Lambda, PidDecoders.widebandLambda())
    private val lambda3B = PidDefinition(0x3B, "O2 B2S4 (WB λ alt)", PidUnit.Lambda, PidDecoders.widebandLambda())
    private val commandedLambda = PidDefinition(0x44, "Commanded λ", PidUnit.Lambda, PidDecoders.widebandLambda())

    private val core: List<PidDefinition> = listOf(
        fuelLoop, rpm, speed, coolant, load, throttle, intake, fuel, runtime, voltage, ambient, oil,
        stft1, ltft1, stft2, ltft2, map, maf,
    )

    private val narrowbandO2: List<PidDefinition> = listOf(
        o2B1S1, o2B1S2, o2B1S3, o2B1S4, o2B2S1, o2B2S2, o2B2S3, o2B2S4,
    )

    private val widebandO2: List<PidDefinition> = listOf(
        lambda24, lambda25, lambda26, lambda27, lambda28, lambda29, lambda2A, lambda2B,
        lambda34, lambda35, lambda36, lambda37, lambda38, lambda39, lambda3A, lambda3B,
        commandedLambda,
    )

    val all: List<PidDefinition> = core + narrowbandO2 + widebandO2

    private val byPid = all.associateBy { it.pid }

    fun get(pid: Int): PidDefinition? = byPid[pid]

    fun forPersona(persona: PersonaMode): List<PidDefinition> = when (persona) {
        PersonaMode.Diy -> listOf(fuelLoop, rpm, speed, coolant, throttle, o2B1S1, lambda24)
        PersonaMode.SemiPro -> listOf(
            fuelLoop, rpm, speed, coolant, load, throttle, intake, stft1, ltft1, maf,
            o2B1S1, o2B1S2, lambda24, commandedLambda,
        )
        PersonaMode.Shop, PersonaMode.Racing -> all
    }
}
