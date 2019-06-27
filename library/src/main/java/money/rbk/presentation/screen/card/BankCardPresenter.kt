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

package money.rbk.presentation.screen.card

import money.rbk.R
import money.rbk.domain.entity.CreditCardType
import money.rbk.domain.entity.getCardType
import money.rbk.domain.exception.UseCaseException
import money.rbk.domain.interactor.CancelPaymentUseCase
import money.rbk.domain.interactor.CheckoutStateUseCase
import money.rbk.domain.interactor.CreatePaymentUseCase
import money.rbk.domain.interactor.RepeatPaymentUseCase
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.EmptyInputModel
import money.rbk.domain.interactor.input.PaymentInputModel
import money.rbk.presentation.exception.UnknownActionException
import money.rbk.presentation.model.CheckoutInfoModel
import money.rbk.presentation.model.CheckoutStateModel
import money.rbk.presentation.model.EmptyIUModel
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.screen.base.BasePresenter
import money.rbk.presentation.utils.isDateValid
import money.rbk.presentation.utils.isEmailValid
import money.rbk.presentation.utils.isValidCvv

class BankCardPresenter(

    /* Buttons */

    navigator: Navigator,
    private val paymentUseCase: UseCase<PaymentInputModel, CheckoutInfoModel> = CreatePaymentUseCase(),
    private val invoiceEventsUseCase: UseCase<EmptyInputModel, CheckoutInfoModel> = CheckoutStateUseCase(),
    private val repeatPaymentUseCase: UseCase<EmptyInputModel, CheckoutInfoModel> = RepeatPaymentUseCase(),
    private val cancelPaymentUseCase: UseCase<EmptyInputModel, EmptyIUModel> = CancelPaymentUseCase()
) : BasePresenter<BankCardView>(navigator) {

    /* Presenter lifecycle */

    override fun onViewAttached(view: BankCardView) {
        updateCheckout()
    }

    override fun onViewDetached() {
        invoiceEventsUseCase.destroy()
        paymentUseCase.destroy()
    }

    /* Public methods for view */

    fun onPerformPayment(
        cardNumber: String,
        expDate: String,
        cvv: String,
        cardHolder: String,
        email: String) {
        val cardType: CreditCardType? = validateNumber(cardNumber)
        if (validateDate(expDate) and validateCcv(cvv) and validateCardholder(
                cardHolder) and validateEmail(email) and (cardType != null) && (cardType != null)) {
            // Double cardType for smart cast

            val cardPaymentInputModel = PaymentInputModel.buildForCard(
                cardNumber = cardNumber,
                expDate = expDate,
                cvv = cvv,
                cardHolder = cardHolder,
                email = email)

            performPayment(cardPaymentInputModel)
        }
    }

    fun on3DsPerformed() {
        updateCheckout()
    }

    fun validateEmail(email: String): Boolean =
        email.isEmailValid()
            .also { view?.showEmailValid(it) }

    fun validateCardholder(name: String): Boolean =
        name.isNotEmpty().also {
            view?.showNameValid(it)
        }

    fun validateDate(date: String) =
        date.isDateValid().also {
            view?.showDateValid(it)
        }

    fun validateCcv(cvv: String) =
        cvv.isValidCvv().also {
            view?.showCcvValid(it)
        }

    fun validateNumber(number: String) =
        number.getCardType().also {
            view?.showNumberValid(it)
        }

    /* Actions */

    private fun updateCheckout() {
        view?.showProgress()
        invoiceEventsUseCase(EmptyInputModel, ::onCheckoutUpdated, ::onCheckoutUpdateError)
    }

    private fun retryPayment() {
        view?.showProgress()
        repeatPaymentUseCase(EmptyInputModel, ::onPaymentCreated, ::onPaymentError)
    }

    private fun performPayment(cardPaymentInputModel: PaymentInputModel) {
        view?.showProgress()
        paymentUseCase(cardPaymentInputModel, ::onPaymentCreated, ::onPaymentError)
    }

    /* Callbacks */

    private fun onPaymentCreated(checkoutInfo: CheckoutInfoModel) {
        onCheckoutUpdated(checkoutInfo)
    }

    private fun onPaymentError(error: Throwable) {

        if (error is UseCaseException.UnableRepeatPaymentException) {
            onError(error) // TODO: Add use another card
        } else {
            onError(error, ACTION_RETRY_PAYMENT) // TODO: Add use another card
        }
    }

    private fun onCheckoutUpdated(checkoutInfo: CheckoutInfoModel) {
        val view = view ?: return
        view.hideProgress()

        when (val checkoutState = checkoutInfo.checkoutState) {
            is CheckoutStateModel.Success ->
                navigator.openSuccessFragment(R.string.label_payed_by_card_f,
                    checkoutState.paymentToolName)

            is CheckoutStateModel.PaymentFailed ->
                navigator.openErrorFragment(
                    messageRes = checkoutState.reasonResId
                    //                    , positiveButtonPair = retryPaymentButton
                )
            is CheckoutStateModel.InvoiceFailed ->
                navigator.openErrorFragment(
                    messageRes = checkoutState.reasonResId)

            is CheckoutStateModel.Warning ->
                navigator.openWarningFragment(checkoutState.titleId,
                    checkoutState.messageResId)

            CheckoutStateModel.Pending -> Unit

            CheckoutStateModel.PaymentProcessing ->
                navigator.openErrorFragment(messageRes = R.string.error_polling_time_exceeded)

            is CheckoutStateModel.BrowserRedirectInteraction ->
                view.showRedirect(checkoutState.request)
        }

        view.setCost(checkoutInfo.cost)
    }

    private fun onCheckoutUpdateError(error: Throwable) {
        error.printStackTrace()

        if (error is UseCaseException) {
            when (error) {
                is UseCaseException.PollingTimeExceededException ->
                    navigator.openErrorFragment(
                        parent = view as BankCardFragment,
                        messageRes = R.string.error_polling_time_exceeded,
                        positiveAction = ACTION_UPDATE_CHECKOUT,
                        negativeAction = ACTION_USE_ANOTHER_CARD)
            }
        } else {
            onError(error, ACTION_UPDATE_CHECKOUT)
        }
    }

    private fun clearPayment() {
        cancelPaymentUseCase(EmptyInputModel, {}, {})
        view?.clear()
    }

    fun onErrorTest(action: Int?) =
        when (action) {
            ACTION_RETRY_PAYMENT -> retryPayment()
            ACTION_USE_ANOTHER_CARD -> clearPayment()
            ACTION_UPDATE_CHECKOUT -> updateCheckout()
            else -> throw UnknownActionException("unknown action with code : $action")
        }
}
