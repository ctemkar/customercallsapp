package com.smartshehar.customercallingv2.activities.home.permissions

data class PermissionModel(
    var permissionId: Int,
    var permissionName: String,
    var isAllowed: Boolean,
    var permissionString : String
)