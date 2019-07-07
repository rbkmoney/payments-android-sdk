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

package money.rbk.domain.interactor

import money.rbk.di.Injector
import money.rbk.domain.entity.PaymentMethod
import money.rbk.domain.entity.TokenProvider
import money.rbk.domain.exception.UseCaseException
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.EmptyInputModel
import money.rbk.domain.repository.CheckoutRepository
import money.rbk.presentation.model.PaymentMethodModel
import money.rbk.presentation.model.PaymentMethodsModel

internal class PaymentMethodsUseCase(
    private val repository: CheckoutRepository = Injector.checkoutRepository) :
    UseCase<EmptyInputModel, PaymentMethodsModel>() {

    override fun invoke(
        inputModel: EmptyInputModel,
        onResultCallback: (PaymentMethodsModel) -> Unit,
        onErrorCallback: (Throwable) -> Unit) {

        try {
            val paymentMethods = repository.getPaymentMethodsSync()
            if (paymentMethods != null) {
                onResultCallback(PaymentMethodsModel(paymentMethods.mapPaymentMethods()))
                return
            }
        } catch (error: Throwable) {
            onErrorCallback(error)
        }

        bgExecutor(onErrorCallback) {

            val paymentMethods = repository.loadPaymentMethods()
                .mapPaymentMethods()

            uiExecutor {
                onResultCallback(PaymentMethodsModel(paymentMethods))
            }
        }
    }

    private fun List<PaymentMethod>.mapPaymentMethods() =
        mapNotNull { paymentMethod ->
            (paymentMethod as? PaymentMethod.PaymentMethodBankCard)?.let { paymentMethodBankCard ->
                when {
                    paymentMethodBankCard.tokenProviders.isNullOrEmpty() -> PaymentMethodModel.BankCard
                    paymentMethodBankCard.tokenProviders.contains(TokenProvider.googlepay) -> PaymentMethodModel.GooglePay
                    else -> null
                }
            }
        }
            .distinct()
            .also {
                if (it.isEmpty()) {
                    throw UseCaseException.NoSupportedPaymentMethodsException
                }
            }
}
