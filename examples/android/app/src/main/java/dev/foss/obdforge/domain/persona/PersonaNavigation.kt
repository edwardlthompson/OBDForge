package dev.foss.obdforge.domain.persona

import dev.foss.obdforge.domain.livedata.PersonaMode

enum class AppDestination {
    VinResolve,
    LiveData,
    SessionHistory,
    Shop,
    DtcExplain,
}

object PersonaNavigation {
    fun isVisible(persona: PersonaMode, destination: AppDestination): Boolean =
        destination in destinationsFor(persona)

    fun destinationsFor(persona: PersonaMode): Set<AppDestination> = when (persona) {
        PersonaMode.Diy -> setOf(
            AppDestination.VinResolve,
            AppDestination.LiveData,
            AppDestination.SessionHistory,
            AppDestination.DtcExplain,
        )
        PersonaMode.SemiPro -> setOf(
            AppDestination.VinResolve,
            AppDestination.LiveData,
            AppDestination.SessionHistory,
            AppDestination.DtcExplain,
        )
        PersonaMode.Shop -> setOf(
            AppDestination.Shop,
            AppDestination.VinResolve,
            AppDestination.LiveData,
            AppDestination.SessionHistory,
            AppDestination.DtcExplain,
        )
        PersonaMode.Racing -> setOf(
            AppDestination.LiveData,
            AppDestination.SessionHistory,
            AppDestination.VinResolve,
        )
    }

    fun showsAuditExport(persona: PersonaMode): Boolean =
        persona == PersonaMode.Shop || persona == PersonaMode.Racing

    fun showsExpertMode(persona: PersonaMode): Boolean =
        persona != PersonaMode.Diy
}
