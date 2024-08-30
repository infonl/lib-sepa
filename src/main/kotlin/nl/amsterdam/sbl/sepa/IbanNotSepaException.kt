/*
 * SPDX-FileCopyrightText: INFO 2024
 * SPDX-License-Identifier: EUPL-1.2+
 */

package nl.amsterdam.sbl.sepa;

class IbanNotSepaException(val iban: String):
    RuntimeException("Iban '$iban' does not follow the SEPA standard and can not be used.")
