package br.com.neuberoliveira.finance.utils

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.service.notification.NotificationListenerService

object NotificationServiceUtil {
  
  fun isNotificationServiceRunning(
    context: Context,
    serviceClass: Class<out NotificationListenerService>
  ): Boolean {
    val cn = ComponentName(context, serviceClass)
    val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
    return flat?.contains(cn.flattenToString()) == true
  }
}