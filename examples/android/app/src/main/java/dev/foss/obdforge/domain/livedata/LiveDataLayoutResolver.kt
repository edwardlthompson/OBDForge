package dev.foss.obdforge.domain.livedata

enum class PersonaMode {
    Diy,
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
    fun resolve(persona: PersonaMode): LiveDataLayoutConfig {
        val definitions = PidCatalog.forPersona(persona)
        return when (persona) {
            PersonaMode.Diy -> LiveDataLayoutConfig(
                persona = persona,
                columns = 2,
                compact = false,
                pollIntervalMs = 500L,
                pids = definitions.map { it.pid },
            )
            PersonaMode.Racing -> LiveDataLayoutConfig(
                persona = persona,
                columns = 4,
                compact = true,
                pollIntervalMs = 100L,
                pids = definitions.map { it.pid },
            )
        }
    }
}
