package br.com.neuberoliveira.finance.services

import br.com.neuberoliveira.finance.BuildConfig
import br.com.neuberoliveira.finance.extractor.TransactionDestination
import br.com.neuberoliveira.finance.extractor.TransactionType
import br.com.neuberoliveira.finance.model.entity.TransactionEntity
import com.google.android.gms.auth.api.*
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange

// Scopes
// https://www.googleapis.com/auth/spreadsheets
// https://www.googleapis.com/auth/spreadsheets
class SheetsClient {
  private val SPREADSHEET_ID: String = "1k29GW98EmRiH7N1GkO13TxEoMMLJjl2ovXt4MEQzcwI"
  private val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
  private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance();
  private val SCOPES = listOf(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE_FILE)
  
  private var sheetClient: Sheets? = null
  private var sheetName: String = "GastosUnificada"
  
  fun setSheetName(name: String) {
    sheetName = name
  }
  
  private fun getRangeWithName(a2: String, sheetName: String? = null): String {
    var name: String = this.sheetName
    
    if (sheetName != null) {
      name = sheetName
    }
    
    return "$name!$a2"
  }
  
  private fun amountWithSign(amount: String?, destination: TransactionDestination?): String {
    val sign = translateDestination(destination)
    
    return "$sign$amount"
  }
  
  fun translateType(type: TransactionType?): String {
    val debit = "Débito"
    val credit = "Crédito"
    return when (type) {
      TransactionType.CREDIT -> credit
      TransactionType.DEBIT -> debit
      TransactionType.PIX -> debit
      TransactionType.TRANSFER -> debit
      else -> "???"
    }
  }
  
  fun translateDestination(type: TransactionDestination?): String {
    return when (type) {
      TransactionDestination.IN -> ""
      TransactionDestination.OUT -> "-"
      else -> "???"
    }
  }
  
  @Throws(Exception::class)
  fun authorize() {
    val serviceAccount = BuildConfig.GOOGLE_SERVICE_ACCOUNT
    
    val credential: GoogleCredential = GoogleCredential
      .fromStream(serviceAccount.byteInputStream())
      .createScoped(SCOPES);
    
    val sheetBuilder = Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
    sheetClient = sheetBuilder.setApplicationName("Finance App/0.0.1").build()
    
    return
  }
  
  fun getTestRange(): Any? {
    if (sheetClient == null) {
      return null
    }
    
    val data = sheetClient!!.spreadsheets()
      .values()
      .get(SPREADSHEET_ID, getRangeWithName("A1:E3", "Teste"))
      .execute()
    return data
  }
  
  fun append(transaction: TransactionEntity): Any? {
    if (sheetClient == null) {
      return null
    }
    
    val range = ValueRange()
    val rowPayload = listOf(
      transaction.date,
      translateType(transaction.type),
      amountWithSign(transaction.amount, transaction.destination),
      "",
      "",
    )
    
    range.setValues(listOf(rowPayload))
    
    sheetClient!!.spreadsheets()
      .values()
      .append(SPREADSHEET_ID, getRangeWithName("A1:E"), range)
      .setValueInputOption("USER_ENTERED")
      .execute()
    return rowPayload
  }
}