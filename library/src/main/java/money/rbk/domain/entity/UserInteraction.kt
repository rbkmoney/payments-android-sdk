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

import money.rbk.data.extension.findEnumOrNull
import money.rbk.data.extension.parse
import org.json.JSONObject
import money.rbk.data.serialization.Deserializer

sealed class UserInteraction(val interactionType: InteractionType) {

    companion object : Deserializer<JSONObject, UserInteraction> {
        override fun fromJson(json: JSONObject): UserInteraction {
            val type = json.getString("interactionType")
            return when (findEnumOrNull<InteractionType>(type)) {
                InteractionType.PaymentTerminalReceipt -> PaymentTerminalReceipt(
                    shortPaymentID = json.getString("shortPaymentID"),
                    dueDate = json.getString("dueDate")
                )
                InteractionType.Redirect -> Redirect(
                    request = json.parse("request", BrowserRequest)
                )
                null -> TODO()
            }
        }
    }

    data class PaymentTerminalReceipt(
        val shortPaymentID: String,
        val dueDate: String //TODO: Date
    ) : UserInteraction(InteractionType.PaymentTerminalReceipt)

    data class Redirect(
        val request: BrowserRequest
    ) : UserInteraction(InteractionType.Redirect)

    enum class InteractionType {
        PaymentTerminalReceipt,
        Redirect
    }
}