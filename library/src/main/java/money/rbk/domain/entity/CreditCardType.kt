/*
 *
 * Copyright 2019 RBKmoney
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package money.rbk.domain.entity

import androidx.annotation.DrawableRes
import money.rbk.R
import money.rbk.presentation.utils.isCardValidByLuna
import money.rbk.presentation.utils.removeSpaces

enum class CreditCardType(
    val lengths: IntArray,
    val prefixes: Array<String>,
    val cardName: String,
    @DrawableRes
    val iconRes: Int?
) {

    visaelectron(
        lengths = intArrayOf(16),
        prefixes = arrayOf(
            "4026",
            "417500",
            "4405",
            "4508",
            "4844",
            "4913",
            "4917"),
        cardName = "Visa Electron",
        iconRes = R.drawable.ic_logo_visa_electron
    ),
    visa(
        lengths = intArrayOf(13, 16, 19),
        prefixes = arrayOf("4"),
        cardName = "Visa",
        iconRes = R.drawable.ic_logo_visa
    ),
    maestro(
        lengths = intArrayOf(12, 13, 14, 15, 16, 17, 18, 19),
        prefixes = arrayOf(
            "50",
            *"56".."69"
        ),
        cardName = "Maestro",
        iconRes = R.drawable.ic_maestro_logo
    ),
    mastercard(
        lengths = intArrayOf(16),
        prefixes = arrayOf(
            *"2221".."2720",
            *"51".."55"
        ),
        cardName = "MasterCard",
        iconRes = R.drawable.ic_master_card_logo
    ),

    nspkmir(
        lengths = intArrayOf(13, 16),
        prefixes = arrayOf(*"2200".."2204"),
        cardName = "MIR",
        iconRes = R.drawable.ic_mir
    ),
    dankort(
        lengths = intArrayOf(16),
        prefixes = arrayOf("5019"),
        cardName = "Dankort",
        iconRes = null
    ),
    amex(
        lengths = intArrayOf(15),
        prefixes = arrayOf(
            "34",
            "37"
        ),
        cardName = "American Express",
        iconRes = null
    ),
    dinersclub(
        lengths = intArrayOf(14, 15, 16, 17, 18, 19),
        prefixes = arrayOf(
            *"300".."305",
            "3095",
            "36",
            "38",
            "39"),
        cardName = "Diners Club",
        iconRes = null
    ),
    discover(
        lengths = intArrayOf(16, 17, 18, 19),
        prefixes = arrayOf(
            "60110",
            *"60112".."60114", "601174",
            *"601177".."601179",
            *"601186".."601199",
            *"644".."659",
            *"622126".."622925",
            *"624".."626",
            *"6282".."6288",
            "64", "65"),
        cardName = "Discover Card",
        iconRes = null
    ),
    unionpay(
        lengths = intArrayOf(16, 17, 18, 19),
        prefixes = arrayOf("62"),
        cardName = "China UnionPay",
        iconRes = null
    ),
    jcb(
        lengths = intArrayOf(16, 17, 18, 19),
        prefixes = arrayOf(*"3528".."3589"),
        cardName = "China UnionPay",
        iconRes = null
    );

    companion object {

        fun detectCardType(cardNumber: String): CreditCardType? =
            values()
                .asSequence()
                .find { cardType ->
                    cardType.lengths.contains(cardNumber.length) &&
                        (cardType.prefixes.any { cardNumber.startsWith(it) })
                }

        fun suggestCardType(creditCardNumber: String): CreditCardType? =
            values()
                .asSequence()
                .firstOrNull { creditCardType ->
                    creditCardType.prefixes.any { creditCardNumber.startsWith(it) }
                }

    }
}

private operator fun String.rangeTo(that: String): Array<String> =
    IntRange(this.toInt(), that.toInt()).map { it.toString() }.toTypedArray()

fun String.getCardType(): CreditCardType? {
    val rawNumberString = removeSpaces()
    return CreditCardType.detectCardType(rawNumberString)
        .takeIf { rawNumberString.isCardValidByLuna() }
}
