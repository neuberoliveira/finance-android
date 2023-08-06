package br.com.neuberoliveira.finance

import br.com.neuberoliveira.finance.extractor.TransactionDestination
import br.com.neuberoliveira.finance.extractor.TransactionType
import br.com.neuberoliveira.finance.model.entity.TransactionEntity
import br.com.neuberoliveira.finance.services.SheetsClient
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TestSheets {
  @Test
  fun fetchTest() {
    val client = SheetsClient()
    client.setSheetName("Teste")
    client.authorize()
    client.getTestRange()
  }
  
  @Test
  fun append() {
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    val client = SheetsClient()
    // client.setSheetName("Teste")
    client.authorize()
    
    
    val transaction = TransactionEntity(
      1,
      "1",
      date,
      TransactionType.DEBIT,
      TransactionDestination.OUT,
      false,
      "bla bla bla title bla bla bla",
      "bla bla bla text bla bla bla",
      "app.tester",
      "tester"
    )
    client.append(transaction)
  }
}