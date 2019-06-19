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

package money.rbk.presentation.screen.base

import androidx.annotation.StringRes
import money.rbk.R
import money.rbk.data.exception.NetworkServiceException
import money.rbk.data.exception.ParseException
import money.rbk.domain.entity.ApiError
import money.rbk.presentation.dialog.AlertButton
import money.rbk.presentation.navigation.Navigator

abstract class BasePresenter<View : BaseView>(protected val navigator: Navigator) {

    protected var view: View? = null

    fun attachView(view: View) {
        this.view = view
        onViewAttached(view)
    }

    fun detachView() {
        this.view = null
        onViewDetached()
    }

    fun onError(error: Throwable, retryButton: AlertButton? = null) {
        when (error) {
            is NetworkServiceException -> error.process(retryButton)
            is ParseException -> error.process(retryButton)
        }

        view?.hideProgress()
        error.printStackTrace()
    }

    open fun onViewAttached(view: View) = Unit

    open fun onViewDetached() = Unit

    //TODO: Make different handling this branches
    private fun NetworkServiceException.process(retryButton: AlertButton?) =
        when (this) {

            NetworkServiceException.NoInternetException ->
                navigator.openErrorFragment(
                    messageRes = R.string.error_connection,
                    positiveButtonPair = retryButton)

            is NetworkServiceException.RequestExecutionException -> // TODO: Make another message
                navigator.openErrorFragment(
                    messageRes = R.string.error_connection,
                    positiveButtonPair = retryButton)

            is NetworkServiceException.ResponseReadingException -> // TODO: Make another message
                navigator.openErrorFragment(
                    messageRes = R.string.error_connection,
                    positiveButtonPair = retryButton)

            is NetworkServiceException.InternalServerException ->
                navigator.openErrorFragment(
                    messageRes = errorMessage(),
                    positiveButtonPair = retryButton)

            is NetworkServiceException.ApiException ->
                navigator.openErrorFragment(
                    messageRes = errorMessage(),
                    positiveButtonPair = retryButton)
        }

    //TODO: Make different handling this branches
    private fun ParseException.process(retryButton: AlertButton?) =
        when (this) {
            is ParseException.ResponseParsingException ->
                navigator.openErrorFragment(R.string.error,
                    R.string.error_unknown_error,
                    retryButton)

            is ParseException.UnknownFlowTypeException ->
                navigator.openErrorFragment(R.string.error,
                    R.string.error_unknown_error,
                    retryButton)

            is ParseException.UnknownPayerTypeException ->
                navigator.openErrorFragment(R.string.error,
                    R.string.error_unknown_error,
                    retryButton)

            is ParseException.UnsupportedPaymentToolDetails ->
                navigator.openErrorFragment(R.string.error,
                    R.string.error_unknown_error,
                    retryButton)

            is ParseException.UnsupportedUserInteractionTypeException ->
                navigator.openErrorFragment(R.string.error,
                    R.string.error_unknown_error,
                    retryButton)

            is ParseException.UnsupportedPaymentMethodException ->
                navigator.openErrorFragment(R.string.error,
                    R.string.error_unknown_error,
                    retryButton)
        }

    @StringRes
    private fun NetworkServiceException.ApiException.errorMessage(): Int = when (error.code) {
        ApiError.Code.operationNotPermitted -> R.string.error_operation_not_permitted
        ApiError.Code.invalidPartyStatus -> R.string.error_invalid_party_status
        ApiError.Code.invalidShopStatus -> R.string.error_invalid_shop_status
        ApiError.Code.invalidContractStatus -> R.string.error_invalid_contract_status
        ApiError.Code.invalidShopID -> R.string.error_invalid_shop_id
        ApiError.Code.invalidInvoiceCost -> R.string.error_invalid_invoice_cost
        ApiError.Code.invalidInvoiceCart -> R.string.error_invalid_invoice_cart
        ApiError.Code.invalidInvoiceStatus -> R.string.error_invalid_invoice_status
        ApiError.Code.invoicePaymentPending -> R.string.error_invoice_payment_pending
        ApiError.Code.invalidPaymentStatus -> R.string.error_invalid_payment_status
        ApiError.Code.invalidPaymentResource -> R.string.error_invalid_payment_resource
        ApiError.Code.invalidPaymentToolToken -> R.string.error_invalid_payment_tool_token
        ApiError.Code.invalidPaymentSession -> R.string.error_invalid_payment_session
        ApiError.Code.invalidRecurrentParent -> R.string.error_invalid_recurrent_parent
        ApiError.Code.insufficentAccountBalance -> R.string.error_insufficent_account_balance
        ApiError.Code.invoicePaymentAmountExceeded -> R.string.error_invoice_payment_amount_exceeded
        ApiError.Code.inconsistentRefundCurrency -> R.string.error_inconsistent_refund_currency
        ApiError.Code.changesetConflict -> R.string.error_changeset_conflict
        ApiError.Code.invalidChangeset -> R.string.error_invalid_changeset
        ApiError.Code.invalidClaimStatus -> R.string.error_invalid_claim_status
        ApiError.Code.invalidClaimRevision -> R.string.error_invalid_claim_revision
        ApiError.Code.limitExceeded -> R.string.error_limit_exceeded
        ApiError.Code.invalidDeadline -> R.string.error_invalid_deadline
        ApiError.Code.invalidRequest -> R.string.error_invalid_request
        null -> R.string.error_unknown_error
    }

    @StringRes
    private fun NetworkServiceException.InternalServerException.errorMessage(): Int = when (code) {
        500 -> R.string.error_internal_server
        503 -> R.string.error_temporary_unavailable
        504 -> R.string.error_request_time_exceeded
        else -> R.string.error_unknown_error
    }
}