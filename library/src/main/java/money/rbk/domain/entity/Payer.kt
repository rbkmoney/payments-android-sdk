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
import money.rbk.data.extension.getNullable
import money.rbk.data.extension.parse
import money.rbk.data.extension.parseNullable
import money.rbk.data.serialization.Deserializer
import money.rbk.data.serialization.Serializable

private const val PAYMENT_RESOURCE_PAYER = "PaymentResourcePayer"

sealed class Payer(val payerType: String) : Serializable {

    companion object : Deserializer<JSONObject, Payer> {
        override fun fromJson(json: JSONObject): Payer = when (json.get("payerType")) {
            PAYMENT_RESOURCE_PAYER -> PaymentResourcePayer(
                paymentToolToken = json.getString("paymentToolToken"),
                paymentSession = json.getNullable("paymentSession"),
                paymentToolDetails = json.parseNullable("paymentToolDetails",
                    PaymentToolDetails),
                clientInfo = json.parseNullable("clientInfo", ClientInfo),
                contactInfo = json.parse("contactInfo", ContactInfo)
            )
            else -> UnknownPayer
        }
    }

    object UnknownPayer : Payer("") {
        override fun toJson() = JSONObject()
    }

    data class PaymentResourcePayer(
        val paymentToolToken: String,
        val paymentSession: String?,
        val contactInfo: ContactInfo,
        val paymentToolDetails: PaymentToolDetails? = null,
        val clientInfo: ClientInfo? = null
    ) : Payer(PAYMENT_RESOURCE_PAYER) {

        override fun toJson(): JSONObject = JSONObject().apply {
            put("payerType", payerType)
            put("paymentToolToken", paymentToolToken)
            paymentSession?.let { put("paymentSession", it) }
            put("contactInfo", contactInfo.toJson())
            paymentToolDetails?.let { put("paymentToolDetails", it) }
            clientInfo?.let { put("clientInfo", it) }
        }
    }

}