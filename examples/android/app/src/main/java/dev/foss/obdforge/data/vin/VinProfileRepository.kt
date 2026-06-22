package dev.foss.obdforge.data.vin

import dev.foss.obdforge.data.local.ObdForgeDatabase
import dev.foss.obdforge.data.local.entity.VehicleProfileEntity
import dev.foss.obdforge.domain.vehicle.VehicleProfile
import dev.foss.obdforge.domain.vehicle.VinReadResult
import dev.foss.obdforge.domain.vehicle.VinSourceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VinProfileRepository(
    database: ObdForgeDatabase,
) {
    private val dao = database.vehicleProfileDao()

    fun observeLatest(): Flow<VehicleProfile?> =
        dao.observeLatest().map { entity -> entity?.toDomain() }

    suspend fun latest(): VehicleProfile? = dao.latest()?.toDomain()

    suspend fun save(result: VinReadResult, adapterIdHash: String? = null): VehicleProfile {
        val existing = dao.getByVin(result.vin)
        val source = resolveSourceOnConflict(existing?.sourceType, result.source)
        val entity = VehicleProfileEntity(
            vin = result.vin,
            sourceType = source.name,
            resolvedAtEpochMs = System.currentTimeMillis(),
            adapterIdHash = adapterIdHash,
            label = existing?.label,
        )
        dao.upsert(entity)
        return entity.copy(sourceType = source.name).toDomain()
    }

    private fun resolveSourceOnConflict(existing: String?, incoming: VinSourceType): VinSourceType {
        if (existing == null) return incoming
        val existingSource = runCatching { VinSourceType.valueOf(existing) }.getOrNull() ?: return incoming
        return if (sourcePriority(incoming) >= sourcePriority(existingSource)) incoming else existingSource
    }

    private fun sourcePriority(source: VinSourceType): Int = when (source) {
        VinSourceType.EcuObd2, VinSourceType.EcuUds, VinSourceType.EcuKwp, VinSourceType.EcuJ1939 -> 4
        VinSourceType.Barcode -> 3
        VinSourceType.Manual -> 2
        VinSourceType.Demo -> 1
        VinSourceType.PlateLookup -> 1
    }

    private fun VehicleProfileEntity.toDomain() = VehicleProfile(
        vin = vin,
        source = VinSourceType.valueOf(sourceType),
        resolvedAtEpochMs = resolvedAtEpochMs,
        adapterIdHash = adapterIdHash,
        label = label,
    )
}
