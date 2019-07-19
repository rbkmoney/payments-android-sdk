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

package money.rbk.presentation.utils

import android.util.Patterns
import java.util.ArrayList

private const val SPACE_STRING = "\\s"
private const val EMPTY_STRING = ""

private const val LENGTH_CVV = 3

internal fun String.isEmailValid(): Boolean =
    isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

internal fun String.removeSpaces() = replace(SPACE_STRING.toRegex(), EMPTY_STRING)

internal fun CharSequence.removeSpaces() = toString().removeSpaces()

internal fun String.isValidCvv() = length == LENGTH_CVV

internal fun String.isCardValidByLuna(): Boolean {
    var currentNumber: Int
    var evenSum = 0
    val unevenNumList = ArrayList<Int>()
    var index = 1
    this.toCharArray()
        .forEach { c ->

            currentNumber = c.toString()
                .toInt()
            if (index % 2 == 0) {
                evenSum += currentNumber
            } else {
                currentNumber *= 2
                if (currentNumber / 10 >= 1) {
                    unevenNumList.add(currentNumber / 10)
                    unevenNumList.add(currentNumber % 10)
                } else {
                    unevenNumList.add(currentNumber)
                }
            }
            index++
        }
    return (unevenNumList.sum() + evenSum) % 10 == 0
}

