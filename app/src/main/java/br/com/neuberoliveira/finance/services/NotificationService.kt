package br.com.neuberoliveira.finance.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import br.com.neuberoliveira.finance.R
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
  private val CHANNEL_ID: String = "finance_channel"
  
  override fun onCreate() {
    super.onCreate()
    println("NotificationService.onCreate")
  
    queue = Volley.newRequestQueue(applicationContext)
    prefs = Preferences(applicationContext)
    sheetClient = SheetsClient()
    sheetClient.authorize()
  
  
    registerChannel()
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
        println("handleNotification.extractor.isValid()")
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
    println("handleNotification.syncServer()")
    GlobalScope.launch(Dispatchers.IO) {
      try{
        sendToSheet(transaction)
      }catch(e:Exception){
        saveToDatabase(transaction, e)
      }
    }
  }
  
  private fun sendToSheet(transaction: TransactionEntity) {
    sheetClient.append(transaction)
    println("Sent to sheet")
  
    /* val title = "Enviado para a planilha"
    val message = "Registrada a transacão do tipo ${transaction.type} no valor de ${transaction.amount}(${transaction.destination}) em ${transaction.store} as ${transaction.date}"
    showNotification(getRandomId(), title, message) */
  }
  
  private fun saveToDatabase(transaction: TransactionEntity, e: Exception) {
    getDatabase(applicationContext).transactionDao().add(transaction)
    println("Sent to database")
    
    showNotification(getRandomId(), "Ops! não foi enviado para a planilha", e.message ?: "?????")
  }
  
  private fun showNotification(id: Int, title: String, content: String) {
    val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
      .setContentTitle(title)
      .setContentText(content)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setStyle(NotificationCompat.BigTextStyle().bigText(content))
      .setAutoCancel(true)
      // .setColor(Color.GREEN)
      .setSmallIcon(R.drawable.ic_notification)
      .build()
    
    startForeground(id, notification)
    println("showNotification()")
  }
  
  private fun registerChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = "Finance Channel"
      val descriptionText = "Finance channel description"
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
        description = descriptionText
      }
      // Register the channel with the system
      NotificationManagerCompat.from(applicationContext).createNotificationChannel(channel)
    }
  }
  
  private fun getRandomId(): Int {
    return (1..9999).random()
  }
}