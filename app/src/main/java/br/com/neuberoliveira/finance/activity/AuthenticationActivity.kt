package br.com.neuberoliveira.finance.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.neuberoliveira.finance.R
import br.com.neuberoliveira.finance.model.prefs.Preferences
import br.com.neuberoliveira.finance.services.GoogleAuthService
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import java.util.Timer


object Status {
  const val WAITING = "waiting"
  const val IDLE = "idle"
  const val DONE = "done"
}

class AuthenticationActivity : AppCompatActivity() {
  private var status = Status.IDLE
  private val intervalTime: Long = 10000
  private val pollTimer: Timer = Timer()
  private lateinit var prefs: Preferences
  private lateinit var queue: RequestQueue
  private lateinit var loader: View
  private var gauth: GoogleAuthService = GoogleAuthService()
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_authentication)
  
    loader = findViewById(R.id.loader)
    prefs = Preferences(applicationContext)
    queue = Volley.newRequestQueue(applicationContext)
  
    gauth.setup(this)
  }
  
  override fun onStop() {
    super.onStop()
    println("STOP!!!")
  }
  
  override fun onResume() {
    super.onResume()
    if (status == Status.WAITING) {
    }
  }
  
  fun startAuth(view: View) {
    loader.visibility = View.VISIBLE
    val gauthIntent: Intent? = gauth.getSignInIntent()
    if (gauthIntent != null) {
      startActivityForResult(gauthIntent, GoogleAuthService.RC_SIGN_IN)
    }
  }
  
  fun stopAuth(auth: String) {
    println(auth)
    prefs.setToken(auth)
    finish()
  }
  
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    
    if (gauth.isSigninActivityResult(requestCode)) {
      gauth.signIn(data)
      stopAuth("nomoretoken")
    }
  }
}

