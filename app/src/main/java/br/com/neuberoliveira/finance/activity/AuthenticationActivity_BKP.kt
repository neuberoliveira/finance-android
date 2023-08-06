package br.com.neuberoliveira.finance.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.neuberoliveira.finance.R
import br.com.neuberoliveira.finance.http.fetcher
import br.com.neuberoliveira.finance.model.prefs.Preferences
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.Timer
import java.util.TimerTask

/*object Status {
  const val WAITING = "waiting"
  const val IDLE = "idle"
  const val DONE = "done"
}*/

class AuthenticationActivity_BKP : AppCompatActivity() {
  private var status = Status.IDLE
  private val intervalTime: Long = 10000
  private val pollTimer: Timer = Timer()
  private lateinit var prefs: Preferences
  private lateinit var queue: RequestQueue
  private lateinit var loader: View
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_authentication)
    
    loader = findViewById(R.id.loader)
    prefs = Preferences(applicationContext)
    queue = Volley.newRequestQueue(applicationContext)
  }
  
  override fun onStop() {
    super.onStop()
    println("STOP!!!")
    stopAuthPooling()
  }
  
  override fun onResume() {
    super.onResume()
    if (status == Status.WAITING) {
      startAuthPooling()
    }
  }
  
  fun startAuth(view: View) {
    loader.visibility = View.VISIBLE
    startAuthPooling()
  }
  
  fun stopAuth(auth: String) {
    println(auth)
    prefs.setToken(auth)
    stopAuthPooling()
    finish()
  }
  
  private fun startAuthPooling() {
    pollTimer.scheduleAtFixedRate(object : TimerTask() {
      override fun run() {
        val endpoint = "get-token.php"
        fetcher(queue, endpoint, Request.Method.GET) { response: JSONObject, statusCode: Int ->
          run {
            if (statusCode == 200) {
              println("Auth pool - Received token")
              stopAuth(response.toString())
              this@AuthenticationActivity_BKP.status = Status.DONE
            } else if (statusCode == 404) {
              println("Auth pool - No token yet")
              if (status.equals(Status.IDLE)) {
                println("Auth pool - First run, open browser for auth")
                this@AuthenticationActivity_BKP.status = Status.WAITING
                this@AuthenticationActivity_BKP.openBrowser(response.getString("auth_url"))
              }
            }
          }
        }
      }
    }, 0, intervalTime)
  }
  
  private fun stopAuthPooling() {
    pollTimer.cancel()
    pollTimer.purge()
  }
  
  private fun openBrowser(url: String) {
    val authPage = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(authPage)
  }
}

