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

package money.rbk.data.response

import money.rbk.data.extension.getNullable
import money.rbk.data.extension.parse
import money.rbk.data.extension.parseNullable
import money.rbk.data.extension.parseString
import org.json.JSONObject
import money.rbk.data.serialization.Deserializer
import money.rbk.domain.entity.Currency
import money.rbk.domain.entity.Flow
import money.rbk.domain.entity.Payer
import money.rbk.domain.entity.PaymentError
import money.rbk.domain.entity.PaymentStatus

internal data class CreatePaymentResponse(
    val id: String,
    val externalID: String?,
    val invoiceID: String,
    val createdAt: String, //TODO: DateObject
    val amount: Int,
    val currency: Currency,
    val flow: Flow,
    val payer: Payer,
    val makeRecurrent: Boolean,
    val status: PaymentStatus,
    val error: PaymentError?
) {
    companion object : Deserializer<JSONObject, CreatePaymentResponse> {
        override fun fromJson(json: JSONObject): CreatePaymentResponse = CreatePaymentResponse(
            id = json.getString("id"),
            externalID = json.getNullable("externalID"),
            invoiceID = json.getString("invoiceID"),
            createdAt = json.getString("createdAt"),
            amount = json.getInt("amount"),
            currency = json.parseString("currency", Currency.Companion),
            flow = json.parse("flow", Flow.Companion),
            payer = json.parse("payer", Payer.Companion),
            makeRecurrent = json.optBoolean("makeRecurrent", false),
            status = json.parseString("status", PaymentStatus.Companion),
            error = json.parseNullable("error", PaymentError.Companion)
        )
    }
}
