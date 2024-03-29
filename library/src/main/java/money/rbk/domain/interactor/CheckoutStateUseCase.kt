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
import money.rbk.domain.converter.InvoiceChangesCheckoutStateConverter
import money.rbk.domain.entity.InvoiceEvent
import money.rbk.domain.exception.UseCaseException
import money.rbk.domain.extension.cost
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.CheckoutStateInputModel
import money.rbk.domain.repository.CheckoutRepository
import money.rbk.presentation.model.CheckoutInfoModel
import money.rbk.presentation.model.CheckoutStateModel
import money.rbk.presentation.utils.formatInternationalPrice

internal class CheckoutStateUseCase(
    private val checkoutRepository: CheckoutRepository = Injector.checkoutRepository,
    private val invoiceChangesConverter: EntityConverter<List<InvoiceEvent>, CheckoutStateModel>
    = InvoiceChangesCheckoutStateConverter()
) : UseCase<CheckoutStateInputModel, CheckoutInfoModel>() {

    private var startTime: Long = 0

    override fun invoke(inputModel: CheckoutStateInputModel,
        onResultCallback: (CheckoutInfoModel) -> Unit,
        onErrorCallback: (Throwable) -> Unit) {
        startTime = System.currentTimeMillis()
        requestCheckoutState(inputModel.ignoreBrowserRequest, onResultCallback, onErrorCallback)
    }

    private fun requestCheckoutState(
        ignoreBrowserRequest: Boolean,
        onResultCallback: (CheckoutInfoModel) -> Unit,
        onErrorCallback: (Throwable) -> Unit) {
        bgExecutor(onErrorCallback) {
            val previousState = checkoutRepository.loadLastInvoiceEvent()?.let { invoiceChangesConverter(listOf(it)) }
            val checkoutState = invoiceChangesConverter(checkoutRepository.loadInvoiceEvents())

            if (checkoutState == CheckoutStateModel.PaymentProcessing
                || (ignoreBrowserRequest
                    && checkoutState is CheckoutStateModel.BrowserRedirectInteraction)) {
                if (System.currentTimeMillis() - startTime > UseCaseConstants.MAX_POLLING_TIME) {
                    throw UseCaseException.PollingTimeExceededException(UseCaseConstants.MAX_POLLING_TIME)
                }

                delayedUiExecutor(UseCaseConstants.POLLING_DELAY) {
                    requestCheckoutState(ignoreBrowserRequest, onResultCallback, onErrorCallback)
                }
            } else {
                val resetPayment = checkoutState is CheckoutStateModel.PaymentFailed && checkoutState == previousState
                checkoutRepository.resetPayment = resetPayment
                val invoice = checkoutRepository.loadInvoice()
                val checkoutInfo = CheckoutInfoModel(
                    price = invoice.amount.formatInternationalPrice(),
                    currency = invoice.currency,
                    formattedPriceAndCurrency = invoice.cost,
                    checkoutState = checkoutState,
                    resetPayment = resetPayment
                )
                uiExecutor {
                    onResultCallback(checkoutInfo)
                }
            }
        }
    }

}
