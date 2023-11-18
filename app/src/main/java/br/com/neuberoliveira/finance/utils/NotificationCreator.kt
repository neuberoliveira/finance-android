package br.com.neuberoliveira.finance.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import br.com.neuberoliveira.finance.R

class NotificationCreator(val applicationContext: Context) {
  val CHANNEL_ID_MAIN = "finance_channel"
  
  fun trigger(title: String, content: String, id: Int?) {
    val notification = this.getBuilder()
      .setContentTitle(title)
      .setContentText(content)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setStyle(NotificationCompat.BigTextStyle().bigText(content))
      .setAutoCancel(true)
      // .setColor(Color.GREEN)
      .setSmallIcon(R.drawable.ic_notification)
      .build()
    
    var notificationId = id ?: getRandomId()
    dispatchNotification(notificationId, notification)
  }
  
  fun trigger(notification: Notification, id: Int?) {
    var notificationId = id ?: getRandomId()
    dispatchNotification(notificationId, notification)
  }
  
  fun getBuilder(): NotificationCompat.Builder {
    return NotificationCompat.Builder(applicationContext, CHANNEL_ID_MAIN)
  }
  
  private fun dispatchNotification(id: Int, notification: Notification) {
    with(NotificationManagerCompat.from(applicationContext)) {
      // notificationId is a unique int for each notification that you must define
      notify(id, notification)
    }
    println("showNotification()")
  }
  
  public fun registerChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = "Finance Channel"
      val descriptionText = "Finance channel description"
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(CHANNEL_ID_MAIN, name, importance).apply {
        description = descriptionText
      }
      // Register the channel with the system
      NotificationManagerCompat.from(applicationContext).createNotificationChannel(channel)
    }
  }
  
  public fun getRandomId(): Int {
    return (1..9999).random()
  }
}