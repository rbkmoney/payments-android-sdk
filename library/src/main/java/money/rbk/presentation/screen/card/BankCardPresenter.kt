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

import androidx.fragment.app.FragmentActivity
import com.whiteelephant.monthpicker.MonthPickerDialog
import money.rbk.domain.entity.CreditCardType
import money.rbk.domain.entity.getCardType
import money.rbk.domain.interactor.CancelPaymentUseCase
import money.rbk.domain.interactor.CheckoutStateUseCase
import money.rbk.domain.interactor.CreatePaymentUseCase
import money.rbk.domain.interactor.RepeatPaymentUseCase
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.CheckoutStateInputModel
import money.rbk.domain.interactor.input.EmptyInputModel
import money.rbk.domain.interactor.input.PaymentInputModel
import money.rbk.presentation.model.CheckoutInfoModel
import money.rbk.presentation.model.EmptyIUModel
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.screen.base.BasePaymentPresenter
import money.rbk.presentation.screen.result.RepeatAction
import money.rbk.presentation.screen.result.ResultAction
import money.rbk.presentation.utils.DateUtils
import money.rbk.presentation.utils.ValidationUtils
import money.rbk.presentation.utils.isEmailValid
import money.rbk.presentation.utils.isValidCvv
import java.util.Calendar

class BankCardPresenter(

    /* Buttons */

    navigator: Navigator,
    private val paymentUseCase: UseCase<PaymentInputModel, CheckoutInfoModel> = CreatePaymentUseCase(),
    private val invoiceEventsUseCase: UseCase<CheckoutStateInputModel, CheckoutInfoModel> = CheckoutStateUseCase(),
    private val repeatPaymentUseCase: UseCase<EmptyInputModel, CheckoutInfoModel> = RepeatPaymentUseCase(),
    private val cancelPaymentUseCase: UseCase<EmptyInputModel, EmptyIUModel> = CancelPaymentUseCase()
) : BasePaymentPresenter<BankCardView>(navigator),
    MonthPickerDialog.OnDateSetListener {

    override val canUseAnotherCard = true

    /* Presenter lifecycle */

    override fun onViewAttached(view: BankCardView) =
        when (navigator.getPendingActionAndClean() ?: ResultAction.UPDATE_CHECKOUT) {
            ResultAction.USE_ANOTHER_CARD -> clearPayment()
            ResultAction.RETRY_PAYMENT -> retryPayment()
            ResultAction.UPDATE_CHECKOUT -> updateCheckout(false)
        }

    fun onDateSelect() {
        val currentDate = Calendar.getInstance()
        val currentMonth = currentDate.get(Calendar.MONTH)
        val currentYear = currentDate.get(Calendar.YEAR)
        navigator.showDateDialog(this,
            currentYear,
            currentYear + ValidationUtils.MAX_YEARS_CARD_VALIDITY,
            currentMonth)
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

            /* Double cardType checking for smart cast, until smart contracts is not ready yet */

            val cardPaymentInputModel = PaymentInputModel.buildForCard(
                cardNumber = cardNumber,
                expDate = expDate,
                cvv = cvv,
                cardHolder = cardHolder,
                email = email)

            performPayment(cardPaymentInputModel)
        }
    }

    fun on3DsPerformed(resultCode: Int) {
        if (resultCode == FragmentActivity.RESULT_OK) {
            updateCheckout(ignoreBrowserRequest = true)
        } else {
            navigator.finishWithCancel()
        }
    }

    override fun onDateSet(month: Int, year: Int) {
        view?.setCardDate(DateUtils.formatMonthYear(month, year))
        validateDate(month, year)
    }

    fun validateEmail(email: String): Boolean =
        email.isEmailValid()
            .also { view?.showEmailValid(it) }

    fun validateCardholder(name: String): Boolean =
        name.isNotEmpty().also {
            view?.showNameValid(it)
        }

    fun validateCcv(cvv: String) =
        cvv.isValidCvv().also {
            view?.showCcvValid(it)
        }

    fun validateNumber(number: String) =
        number.getCardType().also {
            view?.showNumberValid(it)
        }

    private fun validateDate(expDate: String) =
        ValidationUtils.isValidYearMonth(expDate).also {
            view?.showDateValid(it)
        }

    private fun validateDate(month: Int, year: Int) =
        ValidationUtils.isValidYearMonth(month, year).also {
            view?.showDateValid(it)
        }

    /* Actions */

    private fun updateCheckout(ignoreBrowserRequest: Boolean) {
        view?.showProgress()
        invoiceEventsUseCase(CheckoutStateInputModel(ignoreBrowserRequest),
            { onCheckoutUpdated(it, RepeatAction.CHECKOUT) },
            { onCheckoutUpdateError(it) }
        )
    }

    private fun performPayment(cardPaymentInputModel: PaymentInputModel) {
        view?.showProgress()
        paymentUseCase(cardPaymentInputModel,
            { onCheckoutUpdated(it, RepeatAction.PAYMENT) },
            { onPaymentError(it) })
    }

    private fun retryPayment() {
        // TODO: When payment is started, should I cancel it?
        cancelPayment()
        updateCheckout(false)
    }

    private fun clearPayment() {
        view?.clearPayment()
        cancelPayment()
        updateCheckout(false)
    }

    private fun cancelPayment() {
        cancelPaymentUseCase(EmptyInputModel, {}, {})
    }

    /* Callbacks */

    override fun onCheckoutUpdated(checkoutInfo: CheckoutInfoModel, action: RepeatAction) {
        super.onCheckoutUpdated(checkoutInfo, action)
        view?.setCost(checkoutInfo.formattedPriceAndCurrency)
    }

}
