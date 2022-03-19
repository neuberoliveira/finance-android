package br.com.neuberoliveira.finance.services
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import br.com.neuberoliveira.finance.extractNotification
import br.com.neuberoliveira.finance.inspectNotification
import br.com.neuberoliveira.finance.model.database.getDatabase
import br.com.neuberoliveira.finance.model.entity.TransactionEntity

class NotificationService : NotificationListenerService() {
    override fun onCreate() {
        super.onCreate()
        println("NotificationService.onCreate")
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

    private fun handleNotification(sbn:StatusBarNotification?, persist:Boolean=true){
        val entity:TransactionEntity

        if(sbn!=null){
            inspectNotification(sbn)
            val appId = sbn.packageName
            val extractor = extractNotification(sbn)

            if(extractor.isValid() && persist){
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
            }
        }
    }



}