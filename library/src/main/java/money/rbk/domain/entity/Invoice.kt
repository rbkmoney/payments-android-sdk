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

import money.rbk.data.extension.parseDate
import money.rbk.data.extension.parseShortDate
import org.json.JSONObject
import money.rbk.data.serialization.Deserializer
import java.util.Date

internal data class Invoice(
    val id: String,
    val shopID: String,
    val externalID: String?,
    val createdAt: Date,
    val amount: Int,
    val currency: Currency,
    val product: String,
    val description: String?,
    val invoiceTemplateID: String?,
    val status: InvoiceStatus,
    val reason: String?
) {
    companion object :
        Deserializer<JSONObject, Invoice> {
        override fun fromJson(json: JSONObject) = Invoice(
            id = json.getString("id"),
            shopID = json.getString("shopID"),
            externalID = json.optString("externalID", null),
            createdAt = json.parseDate("createdAt"),
            amount = json.getInt("amount"),
            currency = Currency.fromJson(json.getString("currency")),
            product = json.getString("product"),
            description = json.optString("description", null),
            invoiceTemplateID = json.optString("invoiceTemplateID", null),
            status = InvoiceStatus.fromJson(json.getString("status")),
            reason = json.optString("reason", null)
        )
    }
}
