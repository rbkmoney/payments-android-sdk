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
import money.rbk.data.utils.log
import money.rbk.domain.entity.InvoiceStatus
import money.rbk.domain.exception.UseCaseException
import money.rbk.domain.interactor.InvoiceUseCase
import money.rbk.domain.interactor.base.UseCase
import money.rbk.domain.interactor.input.InvoiceInitializeInputModel
import money.rbk.presentation.model.InvoiceModel
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.screen.base.BasePresenter

internal class CheckoutPresenter(
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
        log(throwable)

        if (throwable is UseCaseException.VulnerableDeviceException) {
            navigator.showAlert(
                R.string.rbk_label_error,
                R.string.rbk_error_vulnerable_device,
                null,
                R.string.rbk_label_ok to {
                    navigator.finish()
                })
        } else {
            navigator.showAlert(
                R.string.rbk_label_error,
                R.string.rbk_error_cant_load_invoice,
                R.string.rbk_label_retry to {
                    initializeInvoice()
                },
                R.string.rbk_label_cancel to {
                    navigator.finish()
                })
        }
    }

    private fun onInvoiceLoaded(invoice: InvoiceModel) {
        view?.apply {
            showInvoice(invoice)

            return when (invoice.status) {
                InvoiceStatus.unpaid -> {
                    if (!navigator.safeOpenPaymentMethods()) {
                        hideProgress()
                    } else Unit
                }
                InvoiceStatus.cancelled -> {
                    hideProgress()
                    navigator.openWarningFragment(R.string.rbk_error_invoice_cancelled,
                        R.string.rbk_error_invalid_invoice_status)
                }
                InvoiceStatus.paid -> {
                    hideProgress()
                    navigator.openWarningFragment(R.string.rbk_error_invoice_already_payed,
                        R.string.rbk_error_invalid_invoice_status)
                }
                InvoiceStatus.fulfilled,
                InvoiceStatus.unknown -> {
                    hideProgress()
                    navigator.openWarningFragment(R.string.rbk_error_unknown_invoice,
                        R.string.rbk_error_invalid_invoice_status)
                }
            }
        }
    }
}
