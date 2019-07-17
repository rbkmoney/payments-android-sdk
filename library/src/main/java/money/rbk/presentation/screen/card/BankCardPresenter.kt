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
import money.rbk.R
import money.rbk.domain.entity.CreditCardType
import money.rbk.domain.entity.getCardType
import money.rbk.domain.interactor.CheckoutStateUseCase
import money.rbk.domain.interactor.CreatePaymentUseCase
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.CheckoutStateInputModel
import money.rbk.domain.interactor.input.PaymentInputModel
import money.rbk.presentation.model.CheckoutInfoModel
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.screen.base.BasePaymentPresenter
import money.rbk.presentation.activity.web.Web3DSecureActivity
import money.rbk.presentation.utils.DateUtils
import money.rbk.presentation.utils.ValidationUtils
import money.rbk.presentation.utils.isEmailValid
import money.rbk.presentation.utils.isValidCvv
import java.util.Calendar

class BankCardPresenter(

    /* Buttons */

    navigator: Navigator,
    private val paymentUseCase: UseCase<PaymentInputModel, CheckoutInfoModel> = CreatePaymentUseCase(),
    private val invoiceEventsUseCase: UseCase<CheckoutStateInputModel, CheckoutInfoModel> = CheckoutStateUseCase()
) : BasePaymentPresenter<BankCardView>(navigator),
    MonthPickerDialog.OnDateSetListener {

    override val canUseAnotherCard = true

    /* Presenter lifecycle */

    override fun onViewAttached(view: BankCardView) =
        updateCheckout(false)

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

            val cardPaymentInputModel = PaymentInputModel.PaymentCard(
                cardNumber = cardNumber,
                expDate = expDate,
                cvv = cvv,
                cardHolder = cardHolder,
                email = email)

            performPayment(cardPaymentInputModel)
        }
    }

    fun on3DsPerformed(resultCode: Int) {
        when (resultCode) {
            FragmentActivity.RESULT_OK -> updateCheckout(ignoreBrowserRequest = true)
            FragmentActivity.RESULT_CANCELED -> navigator.finishWithCancel()
            Web3DSecureActivity.RESULT_NETWORK_ERROR ->
                navigator.openErrorFragment(
                    messageRes = R.string.rbc_error_connection,
                    repeatAction = true,
                    useAnotherCard = canUseAnotherCard,
                    allPaymentMethods = true)
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
            { onCheckoutUpdated(it) },
            { onCheckoutUpdateError(it) { updateCheckout(ignoreBrowserRequest) } }
        )
    }

    private fun performPayment(cardPaymentInputModel: PaymentInputModel) {
        view?.showProgress()
        paymentUseCase(cardPaymentInputModel,
            { onCheckoutUpdated(it) },
            { onPaymentError(it) { performPayment(cardPaymentInputModel) } })
    }

    /* Callbacks */

    override fun onCheckoutUpdated(checkoutInfo: CheckoutInfoModel) {
        super.onCheckoutUpdated(checkoutInfo)
        view?.setCost(checkoutInfo.formattedPriceAndCurrency)
    }

}
