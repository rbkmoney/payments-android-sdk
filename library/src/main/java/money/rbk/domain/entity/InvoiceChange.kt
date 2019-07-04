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

import money.rbk.data.exception.ParseException
import money.rbk.data.extension.findEnumOrNull
import money.rbk.data.extension.getNullable
import money.rbk.data.extension.parse
import money.rbk.data.extension.parseNullable
import money.rbk.data.extension.parseString
import money.rbk.data.serialization.Deserializer
import money.rbk.data.serialization.SealedDistributor
import money.rbk.data.serialization.SealedDistributorValue
import org.json.JSONObject
import kotlin.reflect.KClass

internal sealed class InvoiceChange {

    companion object : Deserializer<JSONObject, InvoiceChange> {
        val DISTRIBUTOR = SealedDistributor("changeType", InvoiceChangeType.values())

        override fun fromJson(json: JSONObject): InvoiceChange {
            val type = json.getString("changeType")
            return when (findEnumOrNull<InvoiceChangeType>(type)) {
                InvoiceChangeType.InvoiceCreated -> InvoiceCreated(
                    invoice = json.parse("invoice", Invoice)
                )
                InvoiceChangeType.InvoiceStatusChanged -> InvoiceStatusChanged(
                    status = json.parseString("status", InvoiceStatus),
                    reason = json.getNullable("reason")
                )
                InvoiceChangeType.PaymentStarted -> PaymentStarted(
                    payment = json.parse("payment", Payment)
                )
                InvoiceChangeType.PaymentStatusChanged -> PaymentStatusChanged(
                    status = json.parseString("status", PaymentStatus),
                    paymentID = json.getString("paymentID"),
                    error = json.parseNullable("error", PaymentError)
                )
                InvoiceChangeType.PaymentInteractionRequested -> PaymentInteractionRequested(
                    paymentID = json.getString("paymentID"),
                    userInteraction = json.parse("userInteraction", UserInteraction)
                )

                InvoiceChangeType.RefundStarted,
                InvoiceChangeType.RefundStatusChanged -> Refund(
                    paymentID = json.getString("paymentID")
                )

                null -> throw ParseException.UnsupportedInvoiceChangeTypeException(type)
            }
        }
    }

    data class InvoiceCreated(val invoice: Invoice) :
        InvoiceChange()

    data class InvoiceStatusChanged(
        val status: InvoiceStatus,
        val reason: String?) : InvoiceChange()

    data class PaymentStarted(val payment: Payment) :
        InvoiceChange()

    data class PaymentStatusChanged(
        val status: PaymentStatus,
        val paymentID: String,
        val error: PaymentError?) : InvoiceChange()

    data class PaymentInteractionRequested(
        val paymentID: String,
        val userInteraction: UserInteraction
    ) : InvoiceChange()

    data class Refund(
        val paymentID: String
    ) : InvoiceChange()

    private enum class InvoiceChangeType(override val kClass: KClass<out InvoiceChange>) :
        SealedDistributorValue<InvoiceChange> {

        InvoiceCreated(InvoiceChange.InvoiceCreated::class),
        InvoiceStatusChanged(InvoiceChange.InvoiceStatusChanged::class),
        PaymentStarted(InvoiceChange.PaymentStarted::class),
        PaymentStatusChanged(InvoiceChange.PaymentStatusChanged::class),
        PaymentInteractionRequested(InvoiceChange.PaymentInteractionRequested::class),
        RefundStarted(InvoiceChange.Refund::class),
        RefundStatusChanged(InvoiceChange.Refund::class)
    }

}
