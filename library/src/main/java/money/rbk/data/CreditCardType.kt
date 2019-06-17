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

package money.rbk.data

import money.rbk.presentation.utils.*

enum class CreditCardType(val lenghts: IntArray, val prefixes: Array<String>,val formatMasks: Array<String>) {

    UNKNOWN(intArrayOf(), emptyArray(), emptyArray()),

    VISA(
        lenghts = intArrayOf(13, 16, 19),
        prefixes = arrayOf("4"),
        formatMasks = arrayOf(STANDART_MASK)

    ),

    MAESTRO(
        lenghts = intArrayOf(12, 13, 14, 15, 16, 17, 18, 19),
        prefixes = arrayOf(
            "50", "56", "57", "58", "59", "60", "61", "62", "63", "64",
            "65", "66", "67", "68", "69"
        ),
        formatMasks = arrayOf(STANDART_MASK, THREE_PARTS_MASK, THREE_PARTS_MASK_3, FIVE_PARTS_MASK)
    ),
    MASTERCARD(
        lenghts = intArrayOf(16),
        prefixes = arrayOf(
            "2221", "2222", "2223", "2224", "2225", "2226", "2227", "2228", "2229",
            "223", "224", "225", "226", "227", "228", "229",
            "23", "24", "25", "26", "271", "2720",
            "51", "52", "53", "54", "55"
        ),
        formatMasks = arrayOf(STANDART_MASK)
    ),
    AMERICAN_EXPRESS(
        lenghts = intArrayOf(15),
        prefixes = arrayOf("34", "37"),
        formatMasks = arrayOf(THREE_PARTS_MASK)
    ),
    DISCOVER(
        lenghts = intArrayOf(16, 19),
        prefixes = arrayOf(
            "6011", "622126", "622127", "622128", "622129", "62213",
            "62214", "62215", "62216", "62217", "62218", "62219",
            "6222", "6223", "6224", "6225", "6226", "6227", "6228",
            "62290", "62291", "622920", "622921", "622922", "622923",
            "622924", "622925", "644", "645", "646", "647", "648",
            "649", "65"
        ),
        formatMasks = arrayOf(STANDART_MASK)
    ),
    JCB(
        lenghts = intArrayOf(15, 16),
        prefixes = arrayOf("1800", "2131", "3528", "3529", "353", "354", "355", "356", "357", "358"),
        formatMasks = arrayOf(STANDART_MASK)
    ),

    DINERS(
        lenghts = intArrayOf(14, 16),
        prefixes = arrayOf("300", "301", "302", "303", "304", "305", "36", "54", "55"),
        formatMasks = arrayOf(STANDART_MASK, THREE_PARTS_MASK_2)
    ),

    UNIONPAY(
        lenghts = intArrayOf(16, 17, 18, 19),
        prefixes = arrayOf(
            "622126", "622127", "622128", "622129", "62213", "62214",
            "62215", "62216", "62217", "62218", "62219", "6222", "6223",
            "6224", "6225", "6226", "6227", "6228", "62290", "62291",
            "622920", "622921", "622922", "622923", "622924", "622925"
        ),
        formatMasks = arrayOf(STANDART_MASK, TWO_PARTS_MASK)
    ),

    MIR(
        lenghts = intArrayOf(13, 16),
        prefixes = arrayOf("2200", "2201", "2202", "2203", "2204"),
        formatMasks = arrayOf(STANDART_MASK)
    );

    companion object {
        fun detect(credirCardNumber: String): CreditCardType =
            CreditCardType.values()
                .asSequence()
                .filter { creditCardType ->
                    val creditCardLength = credirCardNumber.length
                    creditCardType.lenghts.contains(creditCardLength) &&
                            creditCardType.prefixes.any { credirCardNumber.startsWith(it) }
                }
                .firstOrNull() ?: UNKNOWN


        fun predetect(creditCardNumber: String): List<CreditCardType> =
            CreditCardType.values()
                .asList()
                .filter { creditCardType ->
                    creditCardType.prefixes.any { it.startsWith(creditCardNumber) }
                }
    }
}

