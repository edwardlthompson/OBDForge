package dev.foss.obdforge.ui.connect

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object BluetoothPermissionGate {
    fun requiredPermissions(): Array<String> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
            )
        } else {
            emptyArray()
        }

    fun missingPermissions(context: Context, permissions: Array<String> = requiredPermissions()): List<String> =
        permissions.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }

    fun hasAllPermissions(context: Context): Boolean =
        missingPermissions(context).isEmpty()

    fun allGranted(results: Map<String, Boolean>): Boolean =
        results.values.all { it }
}
