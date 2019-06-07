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

package money.rbk.presentation.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import money.rbk.R

sealed class PaymentMethodModel(
    @StringRes val name: Int? = null,
    @StringRes val description: Int? = null,
    @DrawableRes val icon: Int
) : BaseIUModel() {

    object BankCard : PaymentMethodModel(name = R.string.label_card, icon = R.drawable.ic_card)

    object GooglePay : PaymentMethodModel(icon = R.drawable.ic_google_pay)

}