package br.com.neuberoliveira.finance.services

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.services.sheets.v4.SheetsScopes

// https://github.com/googlesamples/google-services/blob/master/android/signin/app/src/main/java/com/google/samples/quickstart/signin/SignInActivity.java
class GoogleAuthService {
  companion object {
    val RC_SIGN_IN: Int = 9001
  }
  
  private var mGoogleSignInClient: GoogleSignInClient? = null
  private var account: GoogleSignInAccount? = null
  
  fun setup(activity: Activity) {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestEmail()
      .requestId()
      .requestScopes(Scope(SheetsScopes.SPREADSHEETS), Scope(SheetsScopes.DRIVE))
      .build()
    
    mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
  }
  
  fun getSignInIntent(): Intent? {
    return mGoogleSignInClient?.getSignInIntent()
  }
  
  fun getAccount(): GoogleSignInAccount? {
    return account
  }
  
  fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (isSigninActivityResult(requestCode)) {
      signIn(data)
    }
  }
  
  fun signIn(data: Intent?): GoogleSignInAccount {
    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
    // try {
    val acc: GoogleSignInAccount = task.getResult(ApiException::class.java)
    // } catch (e: ApiException) {
    // Log.w(TAG, "signInResult:failed code=" + e.statusCode)
    // }
    
    account = acc
    println(acc.displayName)
    println(acc.id)
    println(acc.idToken)
    println(acc.photoUrl)
    
    return acc
  }
  
  fun isSigninActivityResult(requestCode: Int): Boolean {
    return requestCode == RC_SIGN_IN
  }
}