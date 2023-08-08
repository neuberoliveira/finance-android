package br.com.neuberoliveira.finance.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import br.com.neuberoliveira.finance.extractNotification
import br.com.neuberoliveira.finance.model.database.getDatabase
import br.com.neuberoliveira.finance.model.entity.TransactionEntity
import br.com.neuberoliveira.finance.model.prefs.Preferences
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotificationService : NotificationListenerService() {
  private lateinit var prefs: Preferences
  private lateinit var queue: RequestQueue
  private lateinit var sheetClient: SheetsClient
  
  override fun onCreate() {
    super.onCreate()
    println("NotificationService.onCreate")
    
    queue = Volley.newRequestQueue(applicationContext)
    prefs = Preferences(applicationContext)
    sheetClient = SheetsClient()
    sheetClient.authorize()
  }
  
  override fun onListenerConnected() {
    super.onListenerConnected()
    println("NotificationService.onListenerConnected")
    println("Notification in statusbar: " + activeNotifications.size)
    
    for (sbn in activeNotifications) {
      handleNotification(sbn, false)
    }
  }
  
  override fun onListenerDisconnected() {
    super.onListenerDisconnected()
    println("NotificationService.onListenerDisconnected")
    // requestRebind()
  }
  
  override fun onNotificationPosted(sbn: StatusBarNotification?) {
    super.onNotificationPosted(sbn)
    println("NotificationService.onNotificationPosted")
    handleNotification(sbn)
  }
  
  private fun handleNotification(sbn: StatusBarNotification?, persist: Boolean = true) {
    val entity: TransactionEntity
    
    if (sbn != null) {
      // inspectNotification(sbn)
      val appId = sbn.packageName
      val extractor = extractNotification(sbn)
      
      if (extractor.isValid() && persist) {
        entity = TransactionEntity(
          null, // sbn.id.toLong(),
          extractor.amount,
          extractor.date,
          extractor.type,
          extractor.destination,
          extractor.store,
          false,
          extractor.getTitle(),
          extractor.getText(),
          appId,
          extractor.getName(),
        )
        syncServer(entity)
      }
    }
  }
  
  private fun syncServer(transaction: TransactionEntity) {
    GlobalScope.launch(Dispatchers.IO) {
      try{
        sendToSheet(transaction)
      }catch(e:Exception){
        saveToDatabase(transaction)
      }
    }
  }
  
  private fun sendToSheet(transaction: TransactionEntity){
    sheetClient.append(transaction)
    println("Sent to sheet")
  }
  
  private fun saveToDatabase(transaction: TransactionEntity){
    getDatabase(applicationContext).transactionDao().add(transaction)
  }
}