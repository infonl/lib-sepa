/*
 * SPDX-FileCopyrightText: 2024 INFO
 * SPDX-License-Identifier: EUPL-1.2+
 */

package nl.amsterdam.sbl.sepa

import nl.garvelink.iban.IBAN
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.time.Instant

class SepaCreditTransferTest {

    private val debtorIban = IBAN.valueOf("NL91ABNA0417164300")
    private val creditorIban = IBAN.valueOf("NL91ABNA0417164300")
    private val configuration = SepaCreditTransferConfiguration(
        debtorIban = debtorIban,
        debtorBic = "ABNANL2A",
        debtorName = "Stadsbank van Lening"
    )

    private fun newTransfer() = SepaCreditTransfer(
        sepaCreditTransferConfiguration = configuration,
        messageId = "MSG-0001",
        paymentId = "PMT-0001",
        date = Instant.parse("2024-01-02T03:04:05Z")
    )

    @Test
    fun `marshals a credit transfer to a valid pain_001_001_03 xml document`() {
        val transfer = newTransfer().apply {
            addTransaction(
                id = "E2E-0001",
                amountEuro = BigDecimal("12.34"),
                creditor = "Jane Doe",
                iban = creditorIban,
                comment = "Invoice 42"
            )
        }

        val xml = ByteArrayOutputStream().use { stream ->
            transfer.writeToStream(stream)
            stream.toString(Charsets.UTF_8)
        }

        // Guards against a javax/jakarta JAXB regression: if the generated model and the
        // JAXB runtime namespaces do not match, marshalling throws before we get here.
        assertTrue(
            xml.contains("urn:iso:std:iso:20022:tech:xsd:pain.001.001.03"),
            "expected the pain.001.001.03 namespace in the output, but was:\n$xml"
        )
        assertTrue(xml.contains("<Document"), "expected a <Document> root element")
        assertTrue(xml.contains("MSG-0001"), "expected the message id in the output")
        assertTrue(xml.contains("E2E-0001"), "expected the end-to-end id in the output")
        assertTrue(xml.contains(creditorIban.toPlainString()), "expected the creditor IBAN in the output")
        assertTrue(
            xml.contains("<Ccy>EUR</Ccy>") || xml.contains("Ccy=\"EUR\""),
            "expected the EUR currency in the output"
        )
    }

    @Test
    fun `rejects a transaction with a non-sepa iban`() {
        val transfer = newTransfer()
        val nonSepaIban = IBAN.valueOf("TR330006100519786457841326")

        val exception = assertThrows(IbanNotSepaException::class.java) {
            transfer.addTransaction(
                id = "E2E-0002",
                amountEuro = BigDecimal("1.00"),
                creditor = "Foreign Payee",
                iban = nonSepaIban,
                comment = "Should fail"
            )
        }

        assertEquals(
            "Iban '${nonSepaIban.toPlainString()}' does not follow the SEPA standard and can not be used.",
            exception.message
        )
    }
}
