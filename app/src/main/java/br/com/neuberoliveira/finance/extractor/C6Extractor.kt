package br.com.neuberoliveira.finance.extractor

import java.text.SimpleDateFormat
import java.util.Date


class C6Extractor(title: String?, text: String?) : Extractor(title, text) {
  
  override fun getName(): String {
    return "C6"
  }
  
  override fun execParse() {
    type = extractType()
    amount = extractAmount()
    date = extractDate()
  
    // precisam ser os ultimos por que dependem do type
    store = extractStoreName()
    destination = extractDestination()
  }
  
  private fun extractType(): TransactionType? {
    return when {
      this.notificationTitle.contains("crédito") -> TransactionType.CREDIT
      this.notificationTitle.contains("débito") -> TransactionType.DEBIT
      this.notificationTitle.contains("pix") -> TransactionType.PIX
      this.notificationTitle.contains("saque") -> TransactionType.TRANSFER
      else -> null
    }
  }
  
  private fun extractAmount(): String {
    return regexMatcher("\\d+(\\.\\d+)?,\\d{2}", getText(), "0,00")
  }
  
  private fun extractDate(): String? {
    if (type === TransactionType.PIX) {
      val sdf = SimpleDateFormat("dd/M/yyyy")
      return sdf.format(Date())
    }
    return regexMatcher("\\d{2}/\\d{2}/\\d{4}", getText(), "")
  }
  
  private fun extractDestination(): TransactionDestination {
    return when (type) {
      TransactionType.PIX -> {
        var dest: TransactionDestination
        if (getTitle().contains("pix recebida") && getText().contains("você recebeu um pix")) {
          dest = TransactionDestination.IN
        } else {
          dest = TransactionDestination.OUT
        }
        return dest
      }
      TransactionType.TRANSFER -> {
        var dest: TransactionDestination
        if (getTitle().contains("recebido")) {
          dest = TransactionDestination.IN
        } else {
          dest = TransactionDestination.OUT
        }
        return dest
      }
  
      else -> TransactionDestination.OUT
    }
  }
  
  private fun extractStoreName(): String? {
    val regexp = Regex("(às\\s\\d{2}:\\d{2}\\s?)(em|no\\s)(.*)(bra)")
    val result = regexp.find(getText())
    var storeName: String? = null
    
    if (result != null) {
      storeName = result.groups[3]?.value
    }
    
    if (storeName != null) {
      storeName = storeName.trim()
    }
    
    return storeName
  }
}