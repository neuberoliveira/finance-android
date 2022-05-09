package br.com.neuberoliveira.finance

import android.service.notification.StatusBarNotification
import br.com.neuberoliveira.finance.extractor.C6Extractor
import br.com.neuberoliveira.finance.extractor.EmptyExtractor
import br.com.neuberoliveira.finance.extractor.Extractor


fun extractNotification(statusBarNotification: StatusBarNotification): Extractor {
  var title = getNotificationExtra(statusBarNotification, "android.title", "")
  var text = getNotificationExtra(statusBarNotification, "android.text", "")
  
  var extractor = when (statusBarNotification.packageName) {
    "com.c6bank.app" -> C6Extractor(title, text)
    // "com.discord" -> EmptyExtractor(title, text)
    else -> EmptyExtractor(title, text)
  }
  
  extractor.parse()
  return extractor
}

fun getNotificationExtra(sbn: StatusBarNotification, key: String, default: String = ""): String {
  val value = sbn.notification.extras?.getCharSequence(key, default).toString()
  return value
}

fun inspectNotification(sbn: StatusBarNotification) {
  var bundle = sbn.notification?.extras
  
  println("----------- ${sbn.id}:${sbn.packageName} -----------")
  println("Key: " + sbn.key)
  println("PostTime: " + sbn.postTime)
  println("Tag: " + sbn.tag)
  println("EXTRAS")
  if (bundle != null) {
    for (key in bundle.keySet()) {
      println(key + " : " + if (bundle.get(key) != null) bundle.get(key) else "NULL")
    }
  }
  println("----------- END -----------")
}