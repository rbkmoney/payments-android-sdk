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

import androidx.annotation.StringRes
import money.rbk.R
import money.rbk.di.Injector
import money.rbk.domain.entity.InvoiceChange
import money.rbk.domain.entity.InvoiceEvent
import money.rbk.domain.entity.InvoiceStatus
import money.rbk.domain.entity.Payment
import money.rbk.domain.entity.PaymentError
import money.rbk.domain.entity.PaymentStatus
import money.rbk.domain.entity.UserInteraction
import money.rbk.domain.repository.CheckoutRepository
import money.rbk.presentation.model.BrowserRequestModel
import money.rbk.presentation.model.CheckoutStateModel

internal class InvoiceChangesCheckoutStateConverter(
    private val redirectConverter: EntityConverter<UserInteraction.Redirect, BrowserRequestModel> =
        RedirectBrowserRequestConverter,
    private val checkoutRepository: CheckoutRepository = Injector.checkoutRepository
) : EntityConverter<List<InvoiceEvent>, CheckoutStateModel> {

    private var lastPayment: Payment? = null

    override fun invoke(entity: List<InvoiceEvent>): CheckoutStateModel {
        return entity
            .asSequence()
            .sortedBy { it.createdAt }
            .flatMap { it.changes.asSequence() }
            .mapNotNull { convertInvoiceChange(it) }
            .lastOrNull() ?: CheckoutStateModel.Pending
    }

    private fun convertInvoiceChange(invoiceChange: InvoiceChange): CheckoutStateModel? =

        when (invoiceChange) {

            is InvoiceChange.InvoiceCreated -> CheckoutStateModel.Pending

            is InvoiceChange.InvoiceStatusChanged -> invoiceChange.process()

            is InvoiceChange.PaymentStarted -> invoiceChange.process()

            is InvoiceChange.PaymentStatusChanged -> invoiceChange.process()

            is InvoiceChange.PaymentInteractionRequested -> invoiceChange.process()

            is InvoiceChange.Refund -> invoiceChange.process()

        }

    private fun InvoiceChange.PaymentStarted.process(): CheckoutStateModel? {
        lastPayment = payment

        val externalPaymentId = checkoutRepository.externalPaymentId
        if (externalPaymentId != null && payment.externalID == externalPaymentId) {
            checkoutRepository.paymentId = payment.id
            checkoutRepository.externalPaymentId = null
        }

        if (payment.id != checkoutRepository.paymentId) return null
        return CheckoutStateModel.PaymentProcessing
    }

    private fun InvoiceChange.Refund.process(): CheckoutStateModel? {
        if (paymentID != checkoutRepository.paymentId) return null

        return CheckoutStateModel.Warning(R.string.rbk_label_payment_refund,
            R.string.rbk_error_payment_in_refund_state)
    }

    private fun InvoiceChange.PaymentStatusChanged.process(): CheckoutStateModel? {
        if (paymentID != checkoutRepository.paymentId) return null

        return when (status) {
            PaymentStatus.pending,
            PaymentStatus.processed,
            PaymentStatus.captured -> null
            PaymentStatus.refunded -> CheckoutStateModel.Warning(R.string.rbk_label_payment_refund,
                R.string.rbk_error_payment_in_refund_state)
            PaymentStatus.cancelled -> CheckoutStateModel.PaymentFailed(R.string.rbk_error_payment_cancelled,
                false)
            PaymentStatus.failed -> CheckoutStateModel.PaymentFailed(error.errorText(),
                error.canRetry())
            PaymentStatus.unknown -> CheckoutStateModel.PaymentFailed(R.string.rbk_error_unknown_payment,
                true)
        }
    }

    private fun InvoiceChange.PaymentInteractionRequested.process(): CheckoutStateModel? {
        if (paymentID != checkoutRepository.paymentId) return null
        return when (userInteraction) {
            is UserInteraction.Redirect ->
                CheckoutStateModel.BrowserRedirectInteraction(redirectConverter(userInteraction))
        }
    }

    private fun InvoiceChange.InvoiceStatusChanged.process(): CheckoutStateModel {
        val paymentToolInfo = lastPayment?.payer?.paymentToolInfo.orEmpty()
        val email = lastPayment?.payer?.email.orEmpty()
        return when (status) {
            InvoiceStatus.unpaid -> CheckoutStateModel.Pending
            InvoiceStatus.cancelled -> CheckoutStateModel.InvoiceFailed(R.string.rbk_error_invoice_cancelled)
            InvoiceStatus.paid -> CheckoutStateModel.Success(paymentToolInfo, email)
            InvoiceStatus.fulfilled -> CheckoutStateModel.Success(paymentToolInfo, email)
            InvoiceStatus.unknown -> CheckoutStateModel.InvoiceFailed(R.string.rbk_error_unknown_invoice)
        }
    }

    private fun PaymentError?.canRetry(): Boolean =
        when (this?.code) {
            PaymentError.Code.RejectedByIssuer,
            PaymentError.Code.AccountLimitsExceeded,
            PaymentError.Code.PaymentRejected,
            PaymentError.Code.InvalidPaymentTool,
            PaymentError.Code.Unknown,
            PaymentError.Code.PreauthorizationFailed,
            PaymentError.Code.InsufficientFunds,
            null -> true
        }

    @StringRes
    private fun PaymentError?.errorText(): Int =
        when (this?.code) {
            PaymentError.Code.InvalidPaymentTool -> R.string.rbk_error_invalid_payment_tool
            PaymentError.Code.AccountLimitsExceeded -> R.string.rbk_error_account_limits_exceeded
            PaymentError.Code.InsufficientFunds -> R.string.rbk_error_insufficient_funds
            PaymentError.Code.PreauthorizationFailed -> R.string.rbk_error_preauthorization_failed
            PaymentError.Code.RejectedByIssuer -> R.string.rbk_error_rejected_by_issuer
            PaymentError.Code.PaymentRejected -> R.string.rbk_error_payment_rejected
            PaymentError.Code.Unknown -> R.string.rbk_error_payment_failed
            null -> R.string.rbk_error_payment_failed
        }

}
