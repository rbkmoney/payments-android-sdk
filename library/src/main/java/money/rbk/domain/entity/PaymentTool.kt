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

import money.rbk.data.serialization.Serializable
import org.json.JSONObject

sealed class PaymentTool(protected val paymentToolType: PaymentToolType) : Serializable {

    //TODO: Make it secured way
    data class CardData(
        val cardNumber: String,
        val expDate: String,
        val cvv: String,
        val cardHolder: String
    ) : PaymentTool(PaymentToolType.CardData) {

        override fun toJson(): JSONObject =
            JSONObject().apply {
                put("paymentToolType", paymentToolType)
                put("cardNumber", cardNumber)
                put("expDate", expDate)
                put("cvv", cvv)
                put("cardHolder", cardHolder)
            }
    }

    data class TokenizedCardData(
        val provider: TokenProvider
    ) : PaymentTool(PaymentToolType.TokenizedCardData) {

        override fun toJson(): JSONObject =
            JSONObject().apply {
                put("paymentToolType", paymentToolType)
                put("provider", provider.name)
            }
    }

    enum class PaymentToolType {
        CardData,
        TokenizedCardData
    }

}
