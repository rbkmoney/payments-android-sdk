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

import android.util.Log
import money.rbk.di.Injector
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.EmptyInputModel
import money.rbk.domain.interactor.input.PaymentInputModel
import money.rbk.domain.repository.CheckoutRepository
import money.rbk.presentation.model.CheckoutInfoModel
import money.rbk.presentation.model.CheckoutStateModel

internal class CreatePaymentUseCase(
    private val checkoutRepository: CheckoutRepository = Injector.checkoutRepository,
    private val checkoutStateUseCase: UseCase<EmptyInputModel, CheckoutInfoModel> = CheckoutStateUseCase()
) : UseCase<PaymentInputModel, CheckoutInfoModel>() {

    override fun invoke(
        inputModel: PaymentInputModel,
        onResultCallback: (CheckoutInfoModel) -> Unit,
        onErrorCallback: (Throwable) -> Unit) {

        val onErrorCallbackProxy = { error: Throwable ->
            checkoutRepository.paymentId = null
            onErrorCallback(error)
        }

        val onResultCallbackProxy = { checkoutInfo: CheckoutInfoModel ->
            if (checkoutInfo.checkoutState is CheckoutStateModel.PaymentFailed) {
                checkoutRepository.paymentId = null
            }
            onResultCallback(checkoutInfo)
        }

        bgExecutor(onErrorCallbackProxy) {

            checkoutRepository.createPayment(
                inputModel.paymentTool,
                inputModel.contactInfo)

            uiExecutor {
                checkoutStateUseCase.invoke(EmptyInputModel,
                    onResultCallbackProxy,
                    onErrorCallbackProxy)
            }

        }
    }
}
