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
import money.rbk.domain.converter.EntityConverter
import money.rbk.domain.converter.InvoiceChangesConverter
import money.rbk.domain.entity.InvoiceEvent
import money.rbk.domain.exception.UseCaseException
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.EmptyInputModel
import money.rbk.domain.repository.CheckoutRepository
import money.rbk.presentation.model.CheckoutState
import money.rbk.presentation.model.PaymentStateModel

internal class CheckoutStateUseCase(
    private val checkoutRepository: CheckoutRepository = Injector.checkoutRepository,
    private val invoiceChangesConverter: EntityConverter<List<InvoiceEvent>, CheckoutState> = InvoiceChangesConverter()
) : UseCase<EmptyInputModel, CheckoutState>() {

    private var startTime: Long = 0

    override fun invoke(inputModel: EmptyInputModel,
        onResultCallback: (CheckoutState) -> Unit,
        onErrorCallback: (Throwable) -> Unit) {
        startTime = System.currentTimeMillis()
        requestCheckoutState(onResultCallback, onErrorCallback)
    }

    private fun requestCheckoutState(
        onResultCallback: (CheckoutState) -> Unit,
        onErrorCallback: (Throwable) -> Unit) {

        bgExecutor(onErrorCallback) {

            val checkoutState = invoiceChangesConverter(checkoutRepository.loadInvoiceEvents())

            if (checkoutState.paymentStateModel == PaymentStateModel.Pending) {
                if (System.currentTimeMillis() - startTime > UseCaseConstants.MAX_POLLING_TIME) {
                    throw UseCaseException.PollingTimeExceededException(UseCaseConstants.MAX_POLLING_TIME)
                }

                delayedUiExecutor(UseCaseConstants.POLLING_DELAY) {
                    requestCheckoutState(onResultCallback, onErrorCallback)
                }
            } else {
                uiExecutor {
                    onResultCallback(checkoutState)
                }
            }
        }
    }

}

