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
import money.rbk.domain.interactor.CheckoutStateUseCase
import money.rbk.domain.interactor.CreatePaymentResourceUseCase
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.CardPaymentInputModel
import money.rbk.domain.interactor.input.EmptyInputModel
import money.rbk.presentation.dialog.AlertButton
import money.rbk.presentation.model.CheckoutInfoModel
import money.rbk.presentation.model.InvoiceStateModel
import money.rbk.presentation.model.PaymentResourceCreated
import money.rbk.presentation.model.PaymentStateModel
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.screen.base.BasePresenter
import money.rbk.presentation.utils.cardMask
import money.rbk.presentation.utils.isDateValid
import money.rbk.presentation.utils.isEmailValid
import money.rbk.presentation.utils.isValidCvv

class BankCardPresenter(
    navigator: Navigator,
    private val paymentUseCase: UseCase<CardPaymentInputModel, PaymentResourceCreated> = CreatePaymentResourceUseCase(),
    private val invoiceEventsUseCase: UseCase<EmptyInputModel, CheckoutInfoModel> = CheckoutStateUseCase()
) : BasePresenter<BankCardView>(navigator) {

    private var cardPaymentInputModel: CardPaymentInputModel? = null

    /* Buttons */

    private val retryPaymentButton: AlertButton? by lazy {
        R.string.label_try_again to { performPayment() }
    }

    private val updateCheckoutButton: AlertButton? by lazy {
        R.string.label_try_again to { updateCheckout() }
    }

    private val useAnotherCardButton: AlertButton? by lazy {
        R.string.label_use_another_card to { clearPayment() }
    }

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
                cardHolder) and validateEmail(email) and (cardType != null) && (cardType != null)) { // Double cardType for smart cast
            cardPaymentInputModel =
                CardPaymentInputModel(cardNumber,
                    expDate,
                    cvv,
                    cardHolder,
                    cardType,
                    email)
            performPayment()
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

    private fun performPayment() {
        view?.showProgress()
        cardPaymentInputModel?.let {
            paymentUseCase(it, ::onPaymentCreated, ::onPaymentError)
        }
    }

    /* Callbacks */

    private fun onPaymentCreated(paymentResourceCreated: PaymentResourceCreated) {
        updateCheckout()
    }

    private fun onPaymentError(error: Throwable) {
        if (error is UseCaseException.UnsupportedCardTypeForInvoiceException) {
            navigator.openErrorFragment(
                messageRes = R.string.error_unsupported_card_type_for_invoice,
                negativeButtonPair = useAnotherCardButton)
        } else {
            onError(error, retryPaymentButton)
        }
    }

    private fun onCheckoutUpdated(checkoutInfo: CheckoutInfoModel) {
        val view = view ?: return
        view.hideProgress()
        val checkoutState = checkoutInfo.checkoutState

        checkoutState.invoiceStateModel
            ?: return navigator.openErrorFragment(messageRes = R.string.error_unknown_payment)


        if (!checkoutState.invoiceStateModel.handle()) {
            checkoutState.paymentStateModel?.handle()
        }
        view.setCost(checkoutInfo.cost)
    }

    private fun onCheckoutUpdateError(error: Throwable) {
        error.printStackTrace()

        if (error is UseCaseException) {
            when (error) {
                is UseCaseException.PollingTimeExceededException ->
                    navigator.openErrorFragment(
                        messageRes = R.string.error_polling_time_exceeded,
                        positiveButtonPair = updateCheckoutButton,
                        negativeButtonPair = useAnotherCardButton)
            }
        } else {
            onError(error, updateCheckoutButton)
        }
    }

    /* Handling methods */

    private fun PaymentStateModel.handle() = when (this) {
        PaymentStateModel.Cancelled ->
            navigator.openErrorFragment(
                messageRes = R.string.error_payment_cancelled,
                positiveButtonPair = retryPaymentButton,
                negativeButtonPair = useAnotherCardButton)

        is PaymentStateModel.BrowserRedirectInteraction ->
            view?.showRedirect(request)

        is PaymentStateModel.Failed ->
            navigator.openErrorFragment(
                messageRes = messageRes,
                positiveButtonPair = retryPaymentButton,
                negativeButtonPair = useAnotherCardButton)

        PaymentStateModel.Unknown ->
            navigator.openErrorFragment(
                messageRes = R.string.error_unknown_payment,
                positiveButtonPair = retryPaymentButton,
                negativeButtonPair = useAnotherCardButton)

        PaymentStateModel.Success ->
            navigator.openSuccessFragment(R.string.label_payed_by_card_f, cardName())

        PaymentStateModel.Pending ->
            navigator.openErrorFragment(
                messageRes = R.string.error_polling_time_exceeded,
                positiveButtonPair = updateCheckoutButton,
                negativeButtonPair = useAnotherCardButton)
    }

    private fun InvoiceStateModel.handle(): Boolean = when (this) {

        is InvoiceStateModel.Success -> {
            navigator.openSuccessFragment(R.string.label_payed_by_card_f, cardName())
        }

        is InvoiceStateModel.Cancelled ->
            navigator.openErrorFragment(messageRes = R.string.error_invoice_cancelled)

        InvoiceStateModel.Unknown ->
            navigator.openErrorFragment(
                messageRes = R.string.error_unknown_invoice,
                positiveButtonPair = retryPaymentButton,
                negativeButtonPair = useAnotherCardButton)

        InvoiceStateModel.Pending -> null

    } != null

    private fun cardName() =
        cardPaymentInputModel?.let {
            "${it.cardType.cardName} ${it.cardNumber.cardMask}"
        }.orEmpty()

    private fun clearPayment() {
        cardPaymentInputModel = null
        view?.clear()
    }

}