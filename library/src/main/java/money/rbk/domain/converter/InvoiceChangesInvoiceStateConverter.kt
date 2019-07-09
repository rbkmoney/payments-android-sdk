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

import money.rbk.R
import money.rbk.domain.entity.InvoiceChange
import money.rbk.domain.entity.InvoiceEvent
import money.rbk.domain.entity.InvoiceStatus
import money.rbk.domain.entity.Payment
import money.rbk.presentation.model.InvoiceStateModel

internal class InvoiceChangesInvoiceStateConverter : EntityConverter<List<InvoiceEvent>, InvoiceStateModel> {

    private var lastPayment: Payment? = null

    override fun invoke(entity: List<InvoiceEvent>): InvoiceStateModel {
        return entity
            .asSequence()
            .sortedBy { it.createdAt }
            .flatMap { it.changes.asSequence() }
            .mapNotNull(::convertInvoiceChange)
            .lastOrNull() ?: InvoiceStateModel.Pending
    }

    private fun convertInvoiceChange(invoiceChange: InvoiceChange): InvoiceStateModel? =
        when (invoiceChange) {
            is InvoiceChange.InvoiceCreated -> InvoiceStateModel.Pending
            is InvoiceChange.InvoiceStatusChanged -> invoiceChange.process()
            is InvoiceChange.PaymentStarted -> {
                lastPayment = invoiceChange.payment
                null
            }
            is InvoiceChange.PaymentStatusChanged,
            is InvoiceChange.PaymentInteractionRequested,
            is InvoiceChange.Refund -> null
        }

    private fun InvoiceChange.InvoiceStatusChanged.process(): InvoiceStateModel {
        val paymentToolInfo = lastPayment?.payer?.paymentToolInfo.orEmpty()
        val email = lastPayment?.payer?.email.orEmpty()
        return when (status) {
            InvoiceStatus.unpaid -> InvoiceStateModel.Pending
            InvoiceStatus.cancelled -> InvoiceStateModel.Failed(R.string.error_invoice_cancelled)
            InvoiceStatus.paid -> InvoiceStateModel.Success(paymentToolInfo, email)
            InvoiceStatus.fulfilled -> InvoiceStateModel.Success(paymentToolInfo, email)
            InvoiceStatus.unknown -> InvoiceStateModel.Failed(R.string.error_unknown_invoice)
        }
    }

}
