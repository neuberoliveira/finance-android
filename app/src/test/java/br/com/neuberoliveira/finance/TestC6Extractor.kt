package br.com.neuberoliveira.finance
import br.com.neuberoliveira.finance.extractor.C6Extractor
import br.com.neuberoliveira.finance.extractor.TransactionDestination
import br.com.neuberoliveira.finance.extractor.TransactionType
import org.junit.Assert.*
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId


class TestC6Extractor {
    @Test
    fun shouldFail() {
        assertFalse(C6Extractor("What??", "Iam need to fail now").isValid())
    }

    @Test
    fun debitPurchase() {
        val extactor = C6Extractor("Compra no débito aprovada", "Sua compra no cartão final 0000 no valor de R$ 32,00 em 14/10/2021 às 12:18 em NOME ESTABELECIMENTO SAO PAULO BRA foi aprovada.")
        extactor.parse()

        assertTrue(extactor.isValid())
        assertEquals("32,00", extactor.amount)
        assertEquals("14/10/2021", extactor.date)
        assertEquals(TransactionType.DEBIT, extactor.type)
        assertEquals(TransactionDestination.OUT, extactor.destination)
    }

    @Test
    fun creditPurchase() {
        val extactor = C6Extractor("Compra no crédito aprovada", "Sua compra no cartão final 0000 no valor de R$ 1360,68 em 12/10/2021 às 22:18 em NOME ESTABELECIMENTO SAO PAULO BRA foi aprovada.")
        extactor.parse()

        assertTrue(extactor.isValid())
        assertEquals("1360,68", extactor.amount)
        assertEquals("12/10/2021", extactor.date)
        assertEquals(TransactionType.CREDIT, extactor.type)
        assertEquals(TransactionDestination.OUT, extactor.destination)
    }

    @Test
    fun pixOutgoing() {
        /* val fixedClock = Clock.fixed(Instant.parse("2022-06-05T10:00:00Z"), ZoneId.systemDefault())
        val instant = Instant.now(fixedClock) */

        val extactor = C6Extractor("Transação PIX feita com sucesso!", "Sua transação Pix no valor de R$ 15,00 foi concluída com sucesso")
        extactor.parse()

        assertTrue(extactor.isValid())
        assertEquals("15,00", extactor.amount)
        // TODO arrumar um jeito, de preferencia sem o mockito para testar isso, por hora vai meio na gambi
        // assertEquals("", extactor.date)
        assertTrue(extactor.date != "")
        assertEquals(TransactionType.PIX, extactor.type)
        assertEquals(TransactionDestination.OUT, extactor.destination)
    }

    @Test
    fun pixIncoming() {
        val extactor = C6Extractor("Transação PIX recebida!", "Você recebeu um Pix com valor R$ 1.234,56")
        extactor.parse()

        assertTrue(extactor.isValid())
        assertEquals("1.234,56", extactor.amount)
        assertTrue(extactor.date != "")
        assertEquals(TransactionType.PIX, extactor.type)
        assertEquals(TransactionDestination.IN, extactor.destination)
    }
}