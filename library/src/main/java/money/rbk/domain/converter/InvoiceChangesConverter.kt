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

package money.rbk.domain.converter

import money.rbk.domain.entity.InvoiceChange
import money.rbk.domain.entity.InvoiceEvent
import money.rbk.presentation.model.CheckoutState
import money.rbk.presentation.model.InvoiceStateModel
import money.rbk.presentation.model.PaymentStateModel

internal class InvoiceChangesConverter(
    private val paymentStateConverter: EntityConverter<InvoiceChange, PaymentStateModel?> = InvoiceChangePaymentStateConverter(),
    private val invoiceStateConverter: EntityConverter<InvoiceChange, InvoiceStateModel?> = InvoiceChangeInvoiceStateConverter()
) : EntityConverter<List<InvoiceEvent>, CheckoutState> {

    override fun invoke(entity: List<InvoiceEvent>): CheckoutState {

        val invoiceChanges = entity.flatMap(InvoiceEvent::changes)

        val paymentState = invoiceChanges.lastOrNull {
            it.eventType == InvoiceChange.EventType.Payment
        }
            ?.let(paymentStateConverter::invoke)

        val invoiceState = invoiceChanges.lastOrNull {
            it.eventType == InvoiceChange.EventType.Invoice
        }
            ?.let(invoiceStateConverter::invoke)

        return CheckoutState(invoiceState, paymentState)
    }

}
