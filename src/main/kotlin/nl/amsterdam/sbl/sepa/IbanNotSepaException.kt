/*
 * SPDX-FileCopyrightText: 2024 INFO
 * SPDX-License-Identifier: EUPL-1.2+
 */

package nl.amsterdam.sbl.sepa

class IbanNotSepaException(iban: String):
    RuntimeException("Iban '$iban' does not follow the SEPA standard and can not be used.")
