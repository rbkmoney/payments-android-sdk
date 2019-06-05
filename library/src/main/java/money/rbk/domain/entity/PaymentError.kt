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
import money.rbk.data.extension.parseNullable
import money.rbk.data.serialization.Deserializer

data class PaymentError(
    val code: String,
    val subError: PaymentError?
) {
    companion object : Deserializer<JSONObject, PaymentError> {
        override fun fromJson(json: JSONObject): PaymentError =
            PaymentError(
                code = json.getString("code"),
                subError = json.parseNullable("subError", this)
            )
    }
}