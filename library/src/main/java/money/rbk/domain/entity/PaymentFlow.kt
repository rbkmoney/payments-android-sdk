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
import money.rbk.data.serialization.Deserializer
import money.rbk.data.serialization.SealedDistributor
import money.rbk.data.serialization.SealedDistributorValue
import org.json.JSONObject
import kotlin.reflect.KClass

sealed class PaymentFlow() {

    companion object : Deserializer<JSONObject, PaymentFlow> {
        val DISTRIBUTOR = SealedDistributor("type", PaymentFlowType.values())

        override fun fromJson(json: JSONObject): PaymentFlow {

            val type = json.getString("type")

            return when (findEnumOrNull<PaymentFlowType>(type)) {
                PaymentFlowType.PaymentFlowInstant -> PaymentFlowInstant
                else -> throw UnknownPaymentFlowType(type)
            }
        }
    }

    object PaymentFlowInstant : PaymentFlow()

    private enum class PaymentFlowType(override val kClass: KClass<out PaymentFlow>) :
        SealedDistributorValue<PaymentFlow> {
        PaymentFlowInstant(PaymentFlow.PaymentFlowInstant::class)
    }

}

class UnknownPaymentFlowType(type: String) : Throwable("Unknown payment flow type: $type")
