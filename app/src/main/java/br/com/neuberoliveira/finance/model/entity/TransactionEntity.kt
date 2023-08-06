package br.com.neuberoliveira.finance.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.neuberoliveira.finance.extractor.TransactionDestination
import br.com.neuberoliveira.finance.extractor.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = "amount") val amount: String?,
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "type") val type: TransactionType?,
    @ColumnInfo(name = "destination") val destination: TransactionDestination?,
    @ColumnInfo(name = "sync") val sync: Boolean?,
    @ColumnInfo(name = "notification_title") val notificationTitle: String?,
    @ColumnInfo(name = "notification_text") val notificationText: String?,
    @ColumnInfo(name = "app_id") val appId: String?,
    @ColumnInfo(name = "app_name") val appName: String?,
)