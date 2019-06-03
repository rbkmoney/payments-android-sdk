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

import org.json.JSONObject
import money.rbk.data.extension.parseNullableList
import money.rbk.data.extension.parseStringList
import money.rbk.data.serialization.Deserializer

const val METHOD_BANK_CARD = "BankCard"

sealed class PaymentMethod(open val method: String) {

    data class PaymentMethodBankCard(
        val paymentSystems: List<String>,
        val tokenProviders: List<TokenProvider>?
    ) : PaymentMethod(METHOD_BANK_CARD)

    object PaymentMethodUnknown : PaymentMethod("")

    companion object :
        Deserializer<JSONObject, PaymentMethod> {
        override fun fromJson(json: JSONObject): PaymentMethod =
            when (json.getString("method")) {
                METHOD_BANK_CARD -> PaymentMethodBankCard(
                    json.parseStringList("paymentSystems"),
                    json.parseNullableList("tokenProviders", TokenProvider)
                )
                else -> PaymentMethodUnknown
            }
    }
}