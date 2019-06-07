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

import money.rbk.data.exception.UnsupportedPaymentToolDetails
import org.json.JSONObject
import money.rbk.data.extension.getNullable
import money.rbk.data.extension.parseNullableString
import money.rbk.data.serialization.Deserializer

const val PaymentToolDetailsBankCardType = "BankCard"

sealed class PaymentToolDetails(val detailsType: String) {

    data class BankCard(
        val cardNumberMask: String,
        val bin: String?,
        val lastDigits: String?,
        val paymentSystem: String,
        val tokenProvider: TokenProvider?
    ) : PaymentToolDetails(PaymentToolDetailsBankCardType) {

        companion object : Deserializer<JSONObject, BankCard> {

            override fun fromJson(json: JSONObject): BankCard =
                BankCard(
                    cardNumberMask = json.getString("cardNumberMask"),
                    bin = json.getNullable("bin"),
                    lastDigits = json.getNullable("lastDigits"),
                    paymentSystem = json.getString("paymentSystem"),
                    tokenProvider = json.parseNullableString("tokenProvider",
                        TokenProvider)
                )
        }
    }

    companion object : Deserializer<JSONObject, PaymentToolDetails> {

        override fun fromJson(json: JSONObject): PaymentToolDetails {
            val detailsType = json.getString("detailsType")
            return when (detailsType) {
                PaymentToolDetailsBankCardType -> BankCard.fromJson(json)
                else -> throw UnsupportedPaymentToolDetails(detailsType)

            }
        }
    }
}