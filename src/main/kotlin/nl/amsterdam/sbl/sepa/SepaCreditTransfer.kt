/*
 * SPDX-FileCopyrightText: 2024 INFO
 * SPDX-License-Identifier: EUPL-1.2+
 */

package nl.amsterdam.sbl.sepa

import iso.std.iso._20022.tech.xsd.pain_001_001.AccountIdentification4Choice
import iso.std.iso._20022.tech.xsd.pain_001_001.ActiveOrHistoricCurrencyAndAmount
import iso.std.iso._20022.tech.xsd.pain_001_001.AmountType3Choice
import iso.std.iso._20022.tech.xsd.pain_001_001.BranchAndFinancialInstitutionIdentification4
import iso.std.iso._20022.tech.xsd.pain_001_001.CashAccount16
import iso.std.iso._20022.tech.xsd.pain_001_001.ChargeBearerType1Code
import iso.std.iso._20022.tech.xsd.pain_001_001.CreditTransferTransactionInformation10
import iso.std.iso._20022.tech.xsd.pain_001_001.CustomerCreditTransferInitiationV03
import iso.std.iso._20022.tech.xsd.pain_001_001.Document
import iso.std.iso._20022.tech.xsd.pain_001_001.FinancialInstitutionIdentification7
import iso.std.iso._20022.tech.xsd.pain_001_001.GroupHeader32
import iso.std.iso._20022.tech.xsd.pain_001_001.ObjectFactory
import iso.std.iso._20022.tech.xsd.pain_001_001.PartyIdentification32
import iso.std.iso._20022.tech.xsd.pain_001_001.PaymentIdentification1
import iso.std.iso._20022.tech.xsd.pain_001_001.PaymentInstructionInformation3
import iso.std.iso._20022.tech.xsd.pain_001_001.PaymentMethod3Code
import iso.std.iso._20022.tech.xsd.pain_001_001.PaymentTypeInformation19
import iso.std.iso._20022.tech.xsd.pain_001_001.RemittanceInformation5
import iso.std.iso._20022.tech.xsd.pain_001_001.ServiceLevel8Choice
import nl.garvelink.iban.IBAN
import java.io.OutputStream
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.GregorianCalendar
import javax.xml.bind.JAXBContext
import javax.xml.datatype.DatatypeFactory


class SepaCreditTransfer(
    private val sepaCreditTransferConfiguration: SepaCreditTransferConfiguration,
    private val messageId: String,
    private val paymentId: String,
    private val date: Instant
) {
    private val transactions = mutableListOf<CreditTransferTransactionInformation10>()

    fun addTransaction(id: String, amountEuro: BigDecimal, creditor: String, iban: IBAN, comment: String) {
        iban
            .takeIf { it.isSEPA }
            ?.let { buildCreditTransferTransactionInformation(id, amountEuro, creditor, it, comment) }
            ?.run(transactions::add)
            ?: throw (IbanNotSepaException(iban.toPlainString()))

    }

    private fun buildCreditTransferTransactionInformation(
        id: String,
        amountEuro: BigDecimal,
        creditor: String,
        iban: IBAN,
        comment: String
    ) = CreditTransferTransactionInformation10()
            .apply {
                pmtId = PaymentIdentification1().apply { endToEndId = id }
                amt = AmountType3Choice().apply {
                    instdAmt = ActiveOrHistoricCurrencyAndAmount().apply {
                        ccy = "EUR"
                        value = amountEuro
                    }
                }
                cdtr = PartyIdentification32().apply { nm = creditor }
                cdtrAcct = CashAccount16().also { cashAcc ->
                    cashAcc.id = AccountIdentification4Choice().also { accId ->
                        accId.iban = iban.toPlainString()
                    }
                }
                rmtInf = RemittanceInformation5().apply { ustrd.add(comment) }
            }

    fun writeToStream(stream: OutputStream) {
        val jaxbDocument = (ObjectFactory()).createDocument(this.buildDocument())
        val jaxbContext = JAXBContext.newInstance(Document::class.java)
        jaxbContext.createMarshaller().marshal(jaxbDocument, stream)
    }

    private fun buildDocument() = Document().apply {
        cstmrCdtTrfInitn = CustomerCreditTransferInitiationV03().apply {
            grpHdr = createGroupHeader()
            pmtInf.add(createPaymentInfo())
        }
    }

    private fun createPaymentInfo(): PaymentInstructionInformation3 = PaymentInstructionInformation3()
        .apply {
            pmtInfId = paymentId
            pmtMtd = PaymentMethod3Code.TRF
            nbOfTxs = transactions.size.toString()
            ctrlSum = sumOfTransactions
            pmtTpInf = PaymentTypeInformation19().apply {
                svcLvl = ServiceLevel8Choice().apply { cd = "SEPA" }
            }
            reqdExctnDt = date.truncatedTo(ChronoUnit.DAYS as TemporalUnit).toXmlGregorianCalendar()
            dbtr = PartyIdentification32().apply {
                nm = sepaCreditTransferConfiguration.debtorName
            }
            dbtrAcct = CashAccount16().apply {
                id = AccountIdentification4Choice().apply {
                    iban = sepaCreditTransferConfiguration.debtorIban.toPlainString()

                }
            }
            dbtrAgt = BranchAndFinancialInstitutionIdentification4().apply {
                finInstnId = FinancialInstitutionIdentification7().apply {
                    bic = sepaCreditTransferConfiguration.debtorBic
                }
            }
            chrgBr = ChargeBearerType1Code.SLEV
            cdtTrfTxInf.addAll(transactions)
        }

    private val sumOfTransactions: BigDecimal
        get() = transactions.map { it.amt }.map { it.instdAmt }.sumOf { it.value }

    private fun createGroupHeader() = GroupHeader32()
        .apply {
            msgId = messageId
            creDtTm = date.toXmlGregorianCalendar()
            nbOfTxs = transactions.size.toString()
            ctrlSum = sumOfTransactions
            initgPty = PartyIdentification32().apply {
                nm = sepaCreditTransferConfiguration.debtorName
            }
        }

    private fun Instant.toXmlGregorianCalendar() =
        DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar().apply {
            timeInMillis = toEpochMilli()
        })
}
