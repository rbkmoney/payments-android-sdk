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

import money.rbk.domain.entity.CreditCardType
import money.rbk.presentation.screen.base.BasePaymentView

interface BankCardView : BasePaymentView {

    fun setCardDate(formatMonthYear: String)

    fun showEmailValid(isValid: Boolean)
    fun showDateValid(isValid: Boolean)
    fun showNameValid(isValid: Boolean)
    fun showCcvValid(isValid: Boolean)
    fun showNumberValid(cardType: CreditCardType?)
    fun setCost(cost: String)

}
