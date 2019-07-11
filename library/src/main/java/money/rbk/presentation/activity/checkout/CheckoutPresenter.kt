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

package money.rbk.presentation.activity.checkout

import money.rbk.R
import money.rbk.domain.interactor.InvoiceUseCase
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.InvoiceInitializeInputModel
import money.rbk.presentation.model.InvoiceModel
import money.rbk.presentation.model.InvoiceStateModel
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.screen.base.BasePresenter

class CheckoutPresenter(
    navigator: Navigator,
    private val invoiceUseCase: UseCase<InvoiceInitializeInputModel, InvoiceModel> = InvoiceUseCase()
) : BasePresenter<CheckoutView>(navigator) {

    override fun onViewAttached(view: CheckoutView) {
        super.onViewAttached(view)
        initializeInvoice()
    }

    /* Private Actions */

    private fun initializeInvoice() {
        view?.showProgress()
        invoiceUseCase(InvoiceInitializeInputModel,
            { onInvoiceLoaded(it) },
            { onInvoiceLoadError(it) })
    }

    /* Callbacks */

    private fun onInvoiceLoadError(throwable: Throwable) {
        throwable.printStackTrace()
        navigator.showAlert(
            R.string.error,
            R.string.error_cant_load_invoice,
            R.string.label_try_again to {
                initializeInvoice()
            },
            R.string.cancel to {
                navigator.finish()
            })
    }

    private fun onInvoiceLoaded(invoice: InvoiceModel) {
        view?.apply {
            showInvoice(invoice)
            return when (val invoiceState = invoice.invoiceState) {

                is InvoiceStateModel.Success -> {
                    hideProgress()
                    navigator.openWarningFragment(R.string.label_invoice_already_payed,
                        R.string.error_invalid_invoice_status)
                }

                is InvoiceStateModel.Failed -> {
                    hideProgress()
                    navigator.openWarningFragment(invoiceState.reasonResId,
                        R.string.error_invalid_invoice_status)
                }

                InvoiceStateModel.Pending -> {
                    if (!navigator.safeOpenPaymentMethods()) {
                        hideProgress()
                    } else Unit
                }

            }
        }
    }
}
