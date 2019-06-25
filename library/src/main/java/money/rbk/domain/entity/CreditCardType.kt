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
    val iconRes: Int) {

    visaelectron(
        lengths = intArrayOf(16),
        prefixes = arrayOf("4026", "417500", "4405", "4508", "4844", "4913", "4917"),
        cardName = "Visa Electron",
        iconRes = R.drawable.selector_logo_visa_electron
    ),

    visa(
        lengths = intArrayOf(13, 16, 19),
        prefixes = arrayOf("4"),
        cardName = "Visa",
        iconRes = R.drawable.selector_logo_visa
    ),

    maestro(
        lengths = intArrayOf(12, 13, 14, 15, 16, 17, 18, 19),
        prefixes = arrayOf(
            "50", "56", "57", "58", "59", "60", "61", "62", "63", "64",
            "65", "66", "67", "68", "69"
        ),
        cardName = "Maestro",
        iconRes = R.drawable.ic_maestro_logo
    ),

    mastercard(
        lengths = intArrayOf(16),
        prefixes = arrayOf(
            "2221", "2222", "2223", "2224", "2225", "2226", "2227", "2228", "2229",
            "223", "224", "225", "226", "227", "228", "229",
            "23", "24", "25", "26", "271", "2720",
            "51", "52", "53", "54", "55"
        ),
        cardName = "MasterCard",
        iconRes = R.drawable.selector_logo_master_card
    ),

    nspkmir(
        lengths = intArrayOf(13, 16),
        prefixes = arrayOf("2200", "2201", "2202", "2203", "2204"),
        cardName = "MIR",
        iconRes = R.drawable.selector_logo_mir
    );

    // TODO: "forbrugsforeningen" "dankort" "amex" "dinersclub" "discover" "unionpay" "jcb"

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

fun String.getCardType(): CreditCardType? {
    val rawNumberString = removeSpaces()
    return CreditCardType.detectCardType(rawNumberString)
        .takeIf { rawNumberString.isCardValidByLuna() }
}
