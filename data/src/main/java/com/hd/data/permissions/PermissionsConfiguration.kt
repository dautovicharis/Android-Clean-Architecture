package com.hd.data.permissions

import android.os.Build
import com.hd.data.permissions.model.PermissionTypeDTO

enum class DeviceType {
    XIAOMI,
    OPPO,
    VIVO,
    LETV,
    HONOR,
    ONEPLUS,
    ALL
}

data class PermissionConfig(
    val permissionType: PermissionTypeDTO,
    val minApiLevel: Int = Build.VERSION_CODES.BASE,
    val supportedDevices: Set<DeviceType> = emptySet(),
    val isOptional: Boolean = false,
    val isSupportedByDevice: () -> Boolean = { true },
) {

    private fun isRequiredByApi(): Boolean {
        return Build.VERSION.SDK_INT >= this.minApiLevel
    }

    fun isRequired(): Boolean {
        return isRequiredByApi() && isSupportedByDevice()
    }
}

class PermissionsConfiguration private constructor(
    val permissionConfigs: List<PermissionConfig>
) {
    data class Builder(
        private val configs: MutableList<PermissionConfig> = mutableListOf()
    ) {
        fun addPermissionConfig(config: PermissionConfig): Builder {
            configs.add(config)
            return this
        }

        fun build(): PermissionsConfiguration {
            return PermissionsConfiguration(configs.toList())
        }
    }
}

