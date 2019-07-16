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

import money.rbk.data.exception.ClientError
import money.rbk.di.Injector
import money.rbk.domain.entity.ApiError
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.CheckoutStateInputModel
import money.rbk.domain.interactor.input.PaymentInputModel
import money.rbk.domain.repository.CheckoutRepository
import money.rbk.domain.repository.GpayRepository
import money.rbk.presentation.model.CheckoutInfoModel
import money.rbk.presentation.model.CheckoutStateModel

internal class CreatePaymentUseCase(
    private val checkoutRepository: CheckoutRepository = Injector.checkoutRepository,
    private val gpayRepository: GpayRepository = Injector.gpayRepository,
    private val checkoutStateUseCase: UseCase<CheckoutStateInputModel, CheckoutInfoModel> = CheckoutStateUseCase()
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
                checkoutRepository.externalPaymentId = null
            }
            onResultCallback(checkoutInfo)
        }

        bgExecutor(onErrorCallbackProxy) {

            checkExternalId()

            if (checkoutRepository.paymentId == null) {

                val paymentTool = when (inputModel) {
                    is PaymentInputModel.PaymentCard -> inputModel.getPaymentTool()
                    is PaymentInputModel.PaymentGpay -> inputModel.getPaymentTool(gpayRepository.gatewayMerchantId)
                }

                try {
                    checkoutRepository.createPayment(
                        paymentTool,
                        inputModel.getContactInfo())

                } catch (error: Throwable) {
                    error.printStackTrace()

                    if (error !is ClientError || error.error.code != ApiError.Code.invalidInvoiceStatus) {
                        throw error
                    }
                }

            }

            uiExecutor {
                checkoutStateUseCase.invoke(CheckoutStateInputModel(),
                    onResultCallbackProxy,
                    onErrorCallbackProxy)
            }

        }
    }

    private fun checkExternalId() {
        if (checkoutRepository.paymentId != null) return
        val externalId = checkoutRepository.externalPaymentId
        if (externalId != null) {
            val payment = checkoutRepository.loadPayments()
                .find { it.externalID == externalId }
            checkoutRepository.paymentId = payment?.id
        }
    }
}
