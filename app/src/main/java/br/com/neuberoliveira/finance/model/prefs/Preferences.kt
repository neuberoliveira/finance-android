package br.com.neuberoliveira.finance.model.prefs

import android.content.Context
import android.content.SharedPreferences

class Preferences(ctx: Context) {
  val NAME: String = "finance-prefs"
  val KEY_TOKEN: String = "token"
  val context: Context = ctx
  val prefs: SharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
  
  
  fun hasToken(): Boolean {
    return prefs.contains(KEY_TOKEN)
  }
  
  fun getToken(): String {
    var tk = ""
    if (hasToken()) {
      tk = prefs.getString(KEY_TOKEN, "")!!
    }
    return tk
  }
  
  fun setToken(token: String) {
    prefs.edit().putString(KEY_TOKEN, token).apply()
  }
}
