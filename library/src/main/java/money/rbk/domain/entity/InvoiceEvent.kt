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

import money.rbk.data.extension.parseList
import money.rbk.data.serialization.Deserializer
import org.json.JSONObject

internal data class InvoiceEvent(
    val id: Int,
    val createdAt: String,
    val changes: List<InvoiceChange>
) {
    companion object : Deserializer<JSONObject, InvoiceEvent> {
        override fun fromJson(json: JSONObject): InvoiceEvent = InvoiceEvent(
            id = json.getInt("id"),
            createdAt = json.getString("createdAt"),
            changes = json.parseList("changes", InvoiceChange)
        )
    }
}