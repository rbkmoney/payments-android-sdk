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

package money.rbk.presentation.activity

import money.rbk.domain.interactor.BaseUseCase
import money.rbk.domain.interactor.InvoiceUseCase
import money.rbk.presentation.model.InvoiceModel
import money.rbk.presentation.screen.base.BasePresenter

class CheckoutPresenter(
    private val invoiceUseCase: BaseUseCase<InvoiceModel> = InvoiceUseCase()
) : BasePresenter<CheckoutView>() {

    override fun onViewAttached(view: CheckoutView) {
        view.showProgress()
        invoiceUseCase(::onInvoiceLoaded, ::onInvoiceLoadError)
    }

    private fun onInvoiceLoadError(throwable: Throwable) {
        view?.apply {
            hideProgress()
        }
        throwable.printStackTrace()
    }

    private fun onInvoiceLoaded(invoice: InvoiceModel) {
        view?.apply {
            hideProgress()
            showInvoice(invoice)
        }
    }

}