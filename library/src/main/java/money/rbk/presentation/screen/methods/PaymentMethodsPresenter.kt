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

package money.rbk.presentation.screen.methods

import money.rbk.R
import money.rbk.data.exception.NetworkException
import money.rbk.domain.exception.UseCaseException
import money.rbk.domain.interactor.PaymentMethodsUseCase
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.EmptyInputModel
import money.rbk.presentation.model.PaymentMethodModel
import money.rbk.presentation.model.PaymentMethodsModel
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.screen.base.BasePresenter

class PaymentMethodsPresenter(
    navigator: Navigator,
    private val paymentMethodsUseCase: UseCase<EmptyInputModel, PaymentMethodsModel> = PaymentMethodsUseCase()
) : BasePresenter<PaymentMethodsView>(navigator) {

    override fun onViewAttached(view: PaymentMethodsView) {
        view.showProgress()
        paymentMethodsUseCase(EmptyInputModel,
            { onPaymentMethodsLoaded(it) },
            { onPaymentMethodsError(it) })
    }

    fun onPaymentClick(payment: PaymentMethodModel) =
        when (payment) {
            PaymentMethodModel.BankCard -> navigator.openBankCard()
            PaymentMethodModel.GooglePay -> navigator.openGooglePay()
        }

    private fun onPaymentMethodsLoaded(paymentMethods: PaymentMethodsModel) {
        view?.apply {
            setPaymentMethods(paymentMethods.paymentMethods)
            hideProgress()
        }
    }

    private fun onPaymentMethodsError(throwable: Throwable) {
        (view ?: return).hideProgress()
        return when (throwable) {
            is UseCaseException.NoSupportedPaymentMethodsException ->
                navigator.openErrorFragment(R.string.error_no_supported_payment_methods)
            is NetworkException ->
                navigator.openErrorFragment(R.string.error_connection)
            else ->
                navigator.openErrorFragment(R.string.error_busines_logic)
        }
    }

}
