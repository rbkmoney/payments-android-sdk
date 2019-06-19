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
import money.rbk.domain.entity.ContactInfo
import money.rbk.domain.entity.PaymentStatus
import money.rbk.domain.entity.PaymentTool
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.CardPaymentInputModel
import money.rbk.domain.repository.CheckoutRepository
import money.rbk.presentation.model.PaymentResourceCreated

internal class CreatePaymentResourceUseCase(
    private val checkoutRepository: CheckoutRepository = Injector.checkoutRepository
) : UseCase<CardPaymentInputModel, PaymentResourceCreated>() {

    override fun invoke(
        inputModel: CardPaymentInputModel,
        onResultCallback: (PaymentResourceCreated) -> Unit,
        onErrorCallback: (Throwable) -> Unit) {

        bgExecutor(onErrorCallback) {

            val payment = checkoutRepository.createPayment(
                paymentTool = PaymentTool.CardData(
                    cardNumber = inputModel.cardNumber,
                    cardHolder = inputModel.cardHolder,
                    cvv = inputModel.cvv,
                    expDate = inputModel.expDate
                ),
                contactInfo = ContactInfo(
                    email = inputModel.email
                )
            )

            uiExecutor {
                onResultCallback(PaymentResourceCreated(payment.status == PaymentStatus.pending))
            }

        }
    }
}
