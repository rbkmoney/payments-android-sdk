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
import money.rbk.data.serialization.Serializable

const val PaymentToolCardDataType = "CardData"
const val PaymentToolTokenizedCardDataType = "TokenizedCardData"

sealed class PaymentTool(open val paymentToolType: String) : Serializable {

    data class PaymentToolCardData(
        val cardNumber: String,
        val expDate: String,
        val cvv: String,
        val cardHolder: String
    ) : PaymentTool(PaymentToolCardDataType) {

        override fun toJson(): JSONObject =
            JSONObject().apply {
                put("paymentToolType", paymentToolType)
                put("cardNumber", cardNumber)
                put("expDate", expDate)
                put("cvv", cvv)
                put("cardHolder", cardHolder+"Ivan Zdorovko")
            }
    }

    data class PaymentToolTokenizedCardData(
        val provider: TokenProvider
    ) : PaymentTool(PaymentToolTokenizedCardDataType) {
        override fun toJson(): JSONObject =
            JSONObject().apply {
                put("paymentToolType", paymentToolType)
                put("provider", provider.name)
            }
    }

  }

