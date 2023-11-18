package br.com.neuberoliveira.finance

import android.app.Application
import android.util.Log
import br.com.neuberoliveira.finance.services.NotificationService
import br.com.neuberoliveira.finance.utils.AutostartUtil
import br.com.neuberoliveira.finance.utils.NotificationCreator
import br.com.neuberoliveira.finance.utils.NotificationServiceUtil

class FinanceApp : Application() {
  override fun onCreate() {
    super.onCreate()
    checkNotificationService()
    checkAutoStartPermission()
  }
  
  private fun checkNotificationService() {
    val isRunning =
      NotificationServiceUtil.isNotificationServiceRunning(this, NotificationService::class.java)
    Log.println(Log.INFO, "finance_app", "FinanceApp.checkNotificationService: " + isRunning)
    
    if (!isRunning) {
      Log.println(Log.WARN, "finance_app", "Notification service is not running")
      
      val creator = NotificationCreator(this)
      creator.trigger(
        "Notificações não foram iniciadas",
        "Não foi possível se conectar ao serviço de notificações, por favor verifique se o app tem permissão para Ler todas as notificações",
        null
      )
    }
  }
  
  private fun checkAutoStartPermission() {
    val isGranted = AutostartUtil.isAutostartPermissionGranted(this)
    Log.println(Log.INFO, "finance_app", "FinanceApp.checkAutoStartPermission: " + isGranted)
    if (!isGranted) {
      Log.println(Log.WARN, "finance_app", "Auto start permission is not granted")
      
      val creator = NotificationCreator(this)
      creator.trigger(
        "Serviço em segundo plano",
        "O app precise de permissão para iniciar em segundo plano",
        null
      )
    }
  }
}