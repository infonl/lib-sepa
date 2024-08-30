/*
 * SPDX-FileCopyrightText: INFO 2024
 * SPDX-License-Identifier: EUPL-1.2+
 */

package nl.amsterdam.sbl.sepa

import nl.garvelink.iban.IBAN

class SepaCreditTransferConfiguration(val debtorIban: IBAN, val debtorBic: String, val debtorName: String)
