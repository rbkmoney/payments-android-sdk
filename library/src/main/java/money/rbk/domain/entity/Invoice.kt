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

import money.rbk.data.serialization.Deserializer
import money.rbk.data.serialization.invoke
import org.json.JSONObject
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
            id = json("id"),
            shopID = json("shopID"),
            externalID = json("externalID"),
            createdAt = json("createdAt"),
            amount = json("amount"),
            currency = json("currency", Currency.Companion),
            product = json("product"),
            description = json("description"),
            invoiceTemplateID = json("invoiceTemplateID"),
            status = json("status", InvoiceStatus.Companion),
            reason = json("reason")
        )
    }
}
