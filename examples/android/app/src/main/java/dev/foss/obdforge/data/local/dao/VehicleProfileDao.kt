package dev.foss.obdforge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.foss.obdforge.data.local.entity.VehicleProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: VehicleProfileEntity)

    @Query("SELECT * FROM vehicle_profiles WHERE vin = :vin LIMIT 1")
    suspend fun getByVin(vin: String): VehicleProfileEntity?

    @Query("SELECT * FROM vehicle_profiles ORDER BY resolvedAtEpochMs DESC LIMIT 1")
    suspend fun latest(): VehicleProfileEntity?

    @Query("SELECT * FROM vehicle_profiles ORDER BY resolvedAtEpochMs DESC LIMIT 1")
    fun observeLatest(): Flow<VehicleProfileEntity?>
}
