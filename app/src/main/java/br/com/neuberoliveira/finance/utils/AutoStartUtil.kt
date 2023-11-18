package br.com.neuberoliveira.finance.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

object AutostartUtil {
  
  fun isAutostartPermissionGranted(context: Context): Boolean {
    val manufacturer = android.os.Build.MANUFACTURER.toLowerCase()
    
    return when {
      manufacturer.contains("xiaomi") -> isXiaomiAutostartPermissionGranted(context)
      else -> true // Default to true if the manufacturer is not recognized
    }
  }
  
  private fun isXiaomiAutostartPermissionGranted(context: Context): Boolean {
    val intent = Intent().apply {
      component = ComponentName(
        "com.miui.securitycenter",
        "com.miui.permcenter.autostart.AutoStartManagementActivity"
      )
    }
    
    val packageManager: PackageManager = context.packageManager
    return intent.resolveActivity(packageManager) != null
  }
}
