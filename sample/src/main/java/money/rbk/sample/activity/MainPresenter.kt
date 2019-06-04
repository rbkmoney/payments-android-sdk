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

import money.rbk.sample.BuildConfig
import money.rbk.sample.network.NetworkService
import money.rbk.sample.network.model.InvoiceResponse
import money.rbk.sample.network.model.InvoiceTemplateResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 04.06.19
 */
class MainPresenter {

    private var mainView: MainView? = null
    private var call: Call<*>? = null

    fun attachView(view: MainView) {
        mainView = view

        NetworkService.getInvoiceTemplateByID(
            BuildConfig.INVOICE_TEMPLATE_ID,
            BuildConfig.INVOICE_TEMPLATE_ACCESS_TOKEN)
            .performRequest(::onTemplateLoaded, ::onTemplateLoadError)
    }

    fun detachView() {
        cancelPreviousRequest()
        mainView = null
    }

    fun onBuyClick() {
        mainView?.apply {
            showProgress()
            NetworkService.createInvoiceWithTemplate(
                BuildConfig.INVOICE_TEMPLATE_ID,
                BuildConfig.INVOICE_TEMPLATE_ACCESS_TOKEN)
                .performRequest(::onInvoiceCreated, ::onInvoiceCreateError)

        }
    }

    private fun onInvoiceCreated(response: Response<InvoiceResponse>) {
        mainView?.apply {
            response.body()?.apply(::startCheckout) ?: showInvoiceCreateError()
            hideProgress()
        }
    }

    private fun onInvoiceCreateError(throwable: Throwable) {
        throwable.printStackTrace()
        mainView?.apply {
            showInvoiceCreateError()
            hideProgress()
        }
    }

    private fun onTemplateLoaded(response: Response<InvoiceTemplateResponse>) {
        mainView?.apply {
            response.body()?.apply(::initTemplate) ?: showInvoiceTemplateError()
            hideProgress()
        }
    }

    private fun onTemplateLoadError(throwable: Throwable) {
        throwable.printStackTrace()
        mainView?.apply {
            showInvoiceTemplateError()
            hideProgress()
        }
    }

    private fun <T> Call<T>.performRequest(onResponseCallback: (Response<T>) -> Unit,
        onFailureCallback: (Throwable) -> Unit) {
        cancelPreviousRequest()

        enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) = onFailureCallback(t)
            override fun onResponse(call: Call<T>, response: Response<T>) =
                onResponseCallback(response)
        })

        call = this
    }

    private fun cancelPreviousRequest() {
        call?.takeIf { !it.isCanceled && !it.isExecuted }
            ?.cancel()
    }

}