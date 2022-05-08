package br.com.neuberoliveira.finance.extractor

class EmptyExtractor(title: String?, text: String?) : Extractor(title, text) {
  override fun getName(): String {
    return "Empty"
  }
  
  override fun execParse() {
    /*this.amount = "100,00"
    this.date = "05/06/2021"
    this.destination = TransactionDestination.OUT
    this.type = TransactionType.DEBIT*/
  }
}