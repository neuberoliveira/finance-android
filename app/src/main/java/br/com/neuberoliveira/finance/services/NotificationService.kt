package br.com.neuberoliveira.finance.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import br.com.neuberoliveira.finance.extractNotification
import br.com.neuberoliveira.finance.extractor.Extractor
import br.com.neuberoliveira.finance.extractor.TransactionDestination
import br.com.neuberoliveira.finance.extractor.TransactionType
import br.com.neuberoliveira.finance.http.fetcher
import br.com.neuberoliveira.finance.http.hashToQueryString
import br.com.neuberoliveira.finance.model.database.getDatabase
import br.com.neuberoliveira.finance.model.entity.TransactionEntity
import br.com.neuberoliveira.finance.model.prefs.Preferences
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class NotificationService : NotificationListenerService() {
  private lateinit var prefs: Preferences
  private lateinit var queue: RequestQueue
  
  override fun onCreate() {
    super.onCreate()
    println("NotificationService.onCreate")
    
    queue = Volley.newRequestQueue(applicationContext)
    prefs = Preferences(applicationContext)
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
          null,
          extractor.amount,
          extractor.date,
          extractor.type,
          extractor.destination,
          extractor.getTitle(),
          extractor.getText(),
          appId,
          extractor.getName(),
        )
        getDatabase(applicationContext).transactionDao().add(entity)
        println("Notification saved :wink:")
  
        syncServer(extractor)
      }
    }
  }
  
  private fun syncServer(extractor: Extractor) {
    val endpoint = "append.php"
    val params = HashMap<String, String>()
    val isCredit = extractor.type == TransactionType.CREDIT
    val type = if (isCredit) "credit" else "debit"
    var amountSign = ""
    
    if (extractor.destination == TransactionDestination.OUT && !isCredit) {
      amountSign = "-"
    }
    
    params["type"] = type
    params["amount"] = "${amountSign}${extractor.amount}"
    params["description"] = ""
    params["token"] = prefs.getToken()
    
    fetcher(
      queue,
      "${endpoint}?${hashToQueryString(params)}",
      Request.Method.GET
    ) { response: JSONObject, statusCode: Int ->
      run {
        if (statusCode == 200) {
          println("Sync with success")
        } else {
          println("Ops! Failed on sync")
          println(response.toString())
        }
      }
    }
  }
}