package br.com.neuberoliveira.finance.extractor

import android.service.notification.StatusBarNotification
import br.com.neuberoliveira.finance.model.entity.TransactionEntity

enum class TransactionType {
    CREDIT,
    DEBIT,
    PIX,
}
enum class TransactionDestination {
    IN, // dinheiro entrando
    OUT, // dinheiro saindo
}
abstract class Extractor(title:String?, text:String?) {
    protected var notificationTitle = title?.lowercase() ?: ""
    protected var notificationText = text?.lowercase() ?: ""

    var amount: String? = null
    var type: TransactionType? = null
    var destination: TransactionDestination? = null
    var date: String? = null

    abstract fun execParse()
    abstract fun getName(): String

    fun getTitle() = notificationTitle
    fun getText() = notificationText

    protected fun regexMatcher(pattern:String, target:String, default:String=""): String {
        val regexp = Regex(pattern)
        val result = regexp.find(target)
        var resultRet = default
        if(result!=null && result.groups.isNotEmpty()){
            resultRet = result.groups[0]?.value ?: default
        }

        return resultRet
    }

    fun parse(){
        execParse()
    }


    fun isValid():Boolean {
        return this.amount!=null && this.type!=null && this.destination!=null
    }
}