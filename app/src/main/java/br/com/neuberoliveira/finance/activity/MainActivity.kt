package br.com.neuberoliveira.finance.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.neuberoliveira.finance.R
import br.com.neuberoliveira.finance.model.database.getDatabase
import br.com.neuberoliveira.finance.model.entity.TransactionEntity

class MainActivity : Activity() {
    lateinit var transactions:List<TransactionEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        transactions = getDatabase(this).transactionDao().getAll()
        println("Transactions SIZE: ${transactions.size}")

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val transactionAdapter = CustomAdapter(transactions)
        recyclerView.adapter = transactionAdapter

        if(!isNotificationAccessEnabled()){
            openNotificationSettings()
        }
    }

    fun isNotificationAccessEnabled() : Boolean {
        val enabledNotifications = Settings.Secure.getString(contentResolver,"enabled_notification_listeners")
        val enabled = enabledNotifications.contains(
            this.packageName
        )

        return enabled
    }

    fun openNotificationSettings(){
        startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
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

        init {
            // Define click listener for the ViewHolder's View.
            title = view.findViewById(R.id.title)
            message = view.findViewById(R.id.text)
            appName = view.findViewById(R.id.appname)
            date = view.findViewById(R.id.date)
            amount = view.findViewById(R.id.amount)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.transaction_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.title.text = dataSet[position].notificationTitle
        viewHolder.message.text = dataSet[position].notificationText
        viewHolder.appName.text = dataSet[position].appName
        viewHolder.date.text = dataSet[position].date
        viewHolder.amount.text = dataSet[position].amount
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}