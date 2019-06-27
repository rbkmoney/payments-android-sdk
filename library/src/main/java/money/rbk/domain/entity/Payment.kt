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
import money.rbk.data.extension.parseDate
import money.rbk.data.extension.parseNullable
import money.rbk.data.extension.parseString
import money.rbk.data.serialization.Deserializer
import java.util.Date

internal data class Payment(
    val id: String,
    val externalID: String?,
    val invoiceID: String,
    val createdAt: Date,
    val amount: Int,
    val currency: Currency,
    val flow: PaymentFlow,
    val payer: Payer,
    val makeRecurrent: Boolean,
    val status: PaymentStatus,
    val error: PaymentError?
) {

    companion object : Deserializer<JSONObject, Payment> {
        override fun fromJson(json: JSONObject): Payment = Payment(
            id = json.getString("id"),
            externalID = json.getNullable<String>("externalID"),
            invoiceID = json.getString("invoiceID"),
            createdAt = json.parseDate("createdAt"),
            amount = json.getInt("amount"),
            currency = json.parseString("currency", Currency),
            flow = json.parse("flow", PaymentFlow),
            payer = json.parse("payer", Payer),
            makeRecurrent = json.optBoolean("makeRecurrent", false),
            status = json.parseString("status", PaymentStatus),
            error = json.parseNullable("error", PaymentError)
        )
    }

}
