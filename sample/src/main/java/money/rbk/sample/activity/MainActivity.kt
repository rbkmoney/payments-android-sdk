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

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import money.rbk.RbkMoney
import money.rbk.sample.R
import money.rbk.sample.adapter.InvoiceTemplatesAdapter
import money.rbk.sample.dialog.showAlert
import money.rbk.sample.network.model.InvoiceResponse
import money.rbk.sample.network.model.InvoiceTemplateResponse

class MainActivity : AppCompatActivity(), MainView {

    companion object {
        const val CHECKOUT_REQUEST_CODE = 123
    }

    private val presenter by lazy { MainPresenter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvTemplates.visibility = View.GONE
        presenter.attachView(this)
    }

    override fun setTemplates(invoiceTemplates: List<InvoiceTemplateResponse>) {
        rvTemplates.layoutManager = LinearLayoutManager(this)
        rvTemplates.adapter = InvoiceTemplatesAdapter(invoiceTemplates, presenter::onBuyClick)
        rvTemplates.visibility = View.VISIBLE
    }

    override fun showInvoiceTemplateError(error: String?) {
        showAlert(R.string.label_error,
            getString(R.string.error_cant_load_invoice_template,
                error?.let {
                    getString(R.string.error_f, error) ?: getString(R.string.error_unknown)
                }))
    }

    override fun showInvoiceCreateError(error: String?) {
        showAlert(R.string.label_error,
            getString(R.string.error_cant_load_invoice,
                error?.let {
                    getString(R.string.error_f, error) ?: getString(R.string.error_unknown)
                }))
    }

    override fun startCheckout(invoiceResponse: InvoiceResponse) {
        startActivityForResult(RbkMoney.buildCheckoutIntent(this,
            invoiceResponse.invoice.shopID,
            invoiceResponse.invoice.id,
            invoiceResponse.invoiceAccessToken.payload),
            CHECKOUT_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHECKOUT_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> onPaymentSuccess()
                RESULT_CANCELED -> onPaymentCanceled()
            }
        }
    }

    override fun hideProgress() {
        lLoader.visibility = View.GONE
    }

    override fun showProgress() {
        lLoader.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    private fun onPaymentSuccess() {
        showAlert(R.string.label_success, getString(R.string.label_payment_success))
    }

    private fun onPaymentCanceled() {
        showAlert(R.string.label_cancel, getString(R.string.label_payment_canceled))
    }

}
