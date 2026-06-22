package dev.foss.obdforge.domain.livedata

enum class PersonaMode {
    Diy,
    SemiPro,
    Shop,
    Racing,
}

data class LiveDataLayoutConfig(
    val persona: PersonaMode,
    val columns: Int,
    val compact: Boolean,
    val pollIntervalMs: Long,
    val pids: List<Int>,
)

object LiveDataLayoutResolver {
    fun resolve(persona: PersonaMode, supportedPids: Set<Int>? = null): LiveDataLayoutConfig {
        val definitions = PidCatalog.forPersona(persona)
        val pids = definitions.map { it.pid }.let { requested ->
            supportedPids?.let { requested.filter { pid -> pid in supportedPids } } ?: requested
        }
        return when (persona) {
            PersonaMode.Diy -> LiveDataLayoutConfig(
                persona = persona,
                columns = 2,
                compact = false,
                pollIntervalMs = 500L,
                pids = pids,
            )
            PersonaMode.SemiPro -> LiveDataLayoutConfig(
                persona = persona,
                columns = 3,
                compact = false,
                pollIntervalMs = 300L,
                pids = pids,
            )
            PersonaMode.Shop -> LiveDataLayoutConfig(
                persona = persona,
                columns = 4,
                compact = true,
                pollIntervalMs = 400L,
                pids = pids,
            )
            PersonaMode.Racing -> LiveDataLayoutConfig(
                persona = persona,
                columns = 4,
                compact = true,
                pollIntervalMs = 100L,
                pids = pids,
            )
        }
    }
}
