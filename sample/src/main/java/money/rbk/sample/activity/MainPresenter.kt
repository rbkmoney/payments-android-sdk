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

package money.rbk.sample.activity

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import money.rbk.sample.app.RBKSampleApplication
import money.rbk.sample.network.model.InvoiceModel
import money.rbk.sample.network.model.InvoiceTemplateResponse

class MainPresenter {

    private var mainView: MainView? = null

    private val compositeDisposable = CompositeDisposable()

    fun attachView(view: MainView) {
        mainView = view
        view.showProgress()

        Single.zip(
            RBKSampleApplication.networkService.getInvoiceTemplates(),
            RBKSampleApplication.networkService.getInvoices(),
            BiFunction<List<InvoiceTemplateResponse>, List<InvoiceModel>, Pair<List<InvoiceTemplateResponse>, List<InvoiceModel>>> { invoiceTemplates, invoices -> invoiceTemplates to invoices }
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onTemplatesLoaded(it) }, { onTemplatesLoadError(it) })
            .disposeWhenDetach()
    }

    fun detachView() {
        compositeDisposable.clear()
        mainView = null
    }

    fun onBuyFromInvoiceClick(invoice: InvoiceModel) {
        onInvoiceCreated(invoice)
    }

    fun onBuyFromTemplateClick(invoiceTemplate: InvoiceTemplateResponse) {
        mainView?.apply {
            showProgress()
            RBKSampleApplication.networkService.createInvoiceWithTemplate(invoiceTemplate.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onInvoiceCreated(it) }, { onInvoiceCreateError(it) })
                .disposeWhenDetach()
        }
    }

    private fun onInvoiceCreated(invoiceModel: InvoiceModel) {
        mainView?.apply {
            startCheckout(invoiceModel)
            hideProgress()
        }
    }

    private fun onInvoiceCreateError(throwable: Throwable) {
        throwable.printStackTrace()
        mainView?.apply {
            showInvoiceCreateError(throwable.message ?: throwable.cause?.message)
            hideProgress()
        }
    }

    private fun onTemplatesLoaded(data: Pair<List<InvoiceTemplateResponse>, List<InvoiceModel>>) {
        val (invoiceTemplates, invoices) = data
        mainView?.apply {
            setTemplates(invoiceTemplates, invoices)
            hideProgress()
        }
    }

    private fun onTemplatesLoadError(throwable: Throwable) {
        throwable.printStackTrace()
        mainView?.apply {
            showInvoiceTemplateError(throwable.message ?: throwable.cause?.message)
            hideProgress()
        }
    }

    private fun Disposable.disposeWhenDetach() {
        compositeDisposable.add(this)
    }

}
