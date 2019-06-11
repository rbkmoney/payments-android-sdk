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

package money.rbk.presentation.screen.card

import android.util.Patterns
import money.rbk.presentation.screen.base.BasePresenter
import java.util.*
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR

class BankCardPresenter : BasePresenter<BankCardView>() {


    fun onBuyClick(cardNumber: String, cardDate: String, cardCcv: String, cardName: String, cardEmail: String) {
        if (isEmailValid(cardEmail).not()) {
            view?.showEmailValid(false)
        }
    }


    private fun isEmailValid(email: String): Boolean =
        email.isNotEmpty() and Patterns.EMAIL_ADDRESS.matcher(email).matches()


    private fun isDataValid(date: String): Boolean {
        if (date.isBlank() or (date.length == 5).not()){
            return false
        }

        val currentDate = Calendar.getInstance()
        val currentYear = currentDate.get(YEAR) % 100
        val currentMonth = currentDate.get(MONTH) + 1

        val monthYear = date.split("/")

        val userMonth = monthYear[0].toInt()
        val userYear = monthYear[1].toInt()

        return when {
            userMonth > 12 -> false
            currentYear > userYear -> false
            currentYear == userYear -> userMonth >= currentMonth
            else -> true
        }
    }

    fun onEmail(email: String) =
        view?.showEmailValid(isEmailValid(email))


    fun onName(name: String) =
        view?.showNameValid(name.isNotEmpty())

    fun onDate(date: String) =
        view?.showDateValid(isDataValid(date))

    fun onCcv(name: String) =
        view?.showCcvValid(name.length == 3)

    fun onNumber(number: String) =
        view?.showNumberValid(number.isNotBlank() and (number.length == 19))

}