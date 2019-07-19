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

import money.rbk.data.serialization.SealedDistributor
import money.rbk.data.serialization.SealedDistributorValue
import kotlin.reflect.KClass

internal sealed class InvoiceChange {

    companion object {
        val DISTRIBUTOR = SealedDistributor("changeType", InvoiceChangeType.values())
    }

    class InvoiceCreated(val invoice: Invoice) :
        InvoiceChange()

    class InvoiceStatusChanged(
        val status: InvoiceStatus,
        val reason: String?) : InvoiceChange()

    class PaymentStarted(val payment: Payment) :
        InvoiceChange()

    class PaymentStatusChanged(
        val status: PaymentStatus,
        val paymentID: String,
        val error: PaymentError?) : InvoiceChange()

    class PaymentInteractionRequested(
        val paymentID: String,
        val userInteraction: UserInteraction
    ) : InvoiceChange()

    class Refund(
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
