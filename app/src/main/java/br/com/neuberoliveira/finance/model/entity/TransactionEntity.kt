package br.com.neuberoliveira.finance.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.neuberoliveira.finance.extractor.TransactionDestination
import br.com.neuberoliveira.finance.extractor.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) var uid: Long?,
    @ColumnInfo(name = "amount") var amount: String?,
    @ColumnInfo(name = "date") var date: String?,
    @ColumnInfo(name = "type") var type: TransactionType?,
    @ColumnInfo(name = "destination") var destination: TransactionDestination?,
    @ColumnInfo(name = "store") var store: String?,
    @ColumnInfo(name = "sync") var sync: Boolean?,
    @ColumnInfo(name = "notification_title") var notificationTitle: String?,
    @ColumnInfo(name = "notification_text") var notificationText: String?,
    @ColumnInfo(name = "app_id") var appId: String?,
    @ColumnInfo(name = "app_name") var appName: String?,
)