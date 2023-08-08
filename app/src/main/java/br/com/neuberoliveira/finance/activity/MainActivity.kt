package br.com.neuberoliveira.finance.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.neuberoliveira.finance.R
import br.com.neuberoliveira.finance.extractor.TransactionDestination
import br.com.neuberoliveira.finance.extractor.TransactionType
import br.com.neuberoliveira.finance.model.database.getDatabase
import br.com.neuberoliveira.finance.model.entity.TransactionEntity
import br.com.neuberoliveira.finance.services.SheetsClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : Activity() {
  lateinit var transactions: List<TransactionEntity>
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  
    // val prefs = Preferences(applicationContext)
    val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
    val transactionAdapter = CustomAdapter(transactions)
    recyclerView.adapter = transactionAdapter
  
    if (!isNotificationAccessEnabled()) {
      openNotificationSettings()
    }
  }
  
  override fun onResume() {
    super.onResume()
    fetchTransactionList()
  }
  
  private fun goToAuthScreen() {
    val authScreen = Intent(this, AuthenticationActivity::class.java)
    startActivity(authScreen)
  }
  
  fun isNotificationAccessEnabled(): Boolean {
    val enabledNotifications =
      Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
    val enabled = enabledNotifications.contains(
      this.packageName
    )
  
    return enabled
  }
  
  fun openNotificationSettings() {
    startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
  }
  
  fun fetchTestData(view: View) {
    val client = SheetsClient()
    
    GlobalScope.launch(Dispatchers.IO) {
      client.authorize()
      val data = client.getTestRange()
      println(data)
    }
  }
  
  private fun fetchTransactionList() {
    val databaseDao = getDatabase(this).transactionDao()
    GlobalScope.launch(Dispatchers.IO) {
      transactions = databaseDao.getAll()
    }
  }
}


class CustomAdapter(private val dataSet: List<TransactionEntity>) :
  RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
  
  /**
   * Provide a reference to the type of views that you are using
   * (custom ViewHolder).
   */
  class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title: TextView
    val message: TextView
    val appName: TextView
    val date: TextView
    val amount: TextView
    val type: TextView
    val destination: TextView
    val sync: TextView
    
    init {
      // Define click listener for the ViewHolder's View.
      title = view.findViewById(R.id.title)
      message = view.findViewById(R.id.text)
      appName = view.findViewById(R.id.appname)
      date = view.findViewById(R.id.date)
      amount = view.findViewById(R.id.amount)
      type = view.findViewById(R.id.type)
      destination = view.findViewById(R.id.destination)
      sync = view.findViewById(R.id.sync)
    }
  }
  
  // Create new views (invoked by the layout manager)
  override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
    // Create a new view, which defines the UI of the list item
    val view = LayoutInflater.from(viewGroup.context)
      .inflate(R.layout.transaction_item, viewGroup, false)
    
    return ViewHolder(view)
  }
  
  fun translateType(type: TransactionType?): String {
    return when (type) {
      TransactionType.CREDIT -> "CRE"
      TransactionType.DEBIT -> "DEB"
      TransactionType.PIX -> "PIX"
      TransactionType.TRANSFER -> "TRA"
      else -> "???"
    }
  }
  
  fun translateDestination(type: TransactionDestination?): String {
    return when (type) {
      TransactionDestination.IN -> "ENT"
      TransactionDestination.OUT -> "SAI"
      else -> "???"
    }
  }
  
  // Replace the contents of a view (invoked by the layout manager)
  override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
    var syncText = "N"
    
    if(dataSet[position].sync==true){
      syncText = "S"
    }
    
    // Get element from your dataset at this position and replace the
    // contents of the view with that element
    viewHolder.title.text = dataSet[position].notificationTitle
    viewHolder.message.text = dataSet[position].notificationText
    viewHolder.appName.text = dataSet[position].appName
    viewHolder.date.text = dataSet[position].date
    viewHolder.amount.text = dataSet[position].amount
    viewHolder.type.text = translateType(dataSet[position].type)
    viewHolder.destination.text = translateDestination(dataSet[position].destination)
    viewHolder.sync.text = syncText
  }
  
  // Return the size of your dataset (invoked by the layout manager)
  override fun getItemCount() = dataSet.size
  
}