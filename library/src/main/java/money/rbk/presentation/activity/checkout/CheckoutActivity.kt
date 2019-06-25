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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.ac_checkout.*
import money.rbk.R
import money.rbk.di.Injector
import money.rbk.presentation.model.InvoiceModel
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.utils.adjustSize
import money.rbk.presentation.utils.extra

class CheckoutActivity : AppCompatActivity(), CheckoutView, InitializeListener {
    override fun initialize() {
        presenter.onInitialize()
    }

    companion object {
        private const val KEY_INVOICE_ID = "invoice_id"
        private const val KEY_INVOICE_ACCESS_TOKEN = "invoice_access_token"
        private const val KEY_SHOP_NAME = "shop_name"

        fun buildIntent(
            activity: Activity,
            invoiceId: String,
            invoiceAccessToken: String,
            shopName: String
        ) = Intent(activity, CheckoutActivity::class.java)
            .apply {
                putExtra(KEY_INVOICE_ID, invoiceId)
                putExtra(KEY_INVOICE_ACCESS_TOKEN, invoiceAccessToken)
                putExtra(KEY_SHOP_NAME, shopName)
            }
    }

    val navigator by lazy { Navigator(this, R.id.container) }

    private val invoiceId by extra<String>(KEY_INVOICE_ID)
    private val invoiceAccessToken by extra<String>(KEY_INVOICE_ACCESS_TOKEN)
    private val shopName by extra<String>(KEY_SHOP_NAME)

    private val presenter by lazy { CheckoutPresenter(navigator) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_checkout)
        adjustSize()

        if (savedInstanceState == null) {
            Injector.init(applicationContext, invoiceId, invoiceAccessToken, shopName)
        }

        presenter.attachView(this)
        initViews()
    }

    override fun showInvoice(invoiceModel: InvoiceModel) {
        tvShopName.text = invoiceModel.shopName
        tvPrice.text = invoiceModel.cost
        tvOrderDetails.text = invoiceModel.orderDetails
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    private fun initViews() {
        ibtnBack.setOnClickListener {
            navigator.back()
        }
        ibtnClose.setOnClickListener {
            finish()
        }
    }

    //TODO: Only for a while
    fun setBackButtonVisibility(isVisible: Boolean) {
        ibtnBack.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    override fun showProgress() {
        lLoader.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        lLoader.visibility = View.INVISIBLE
    }

    override fun onBackPressed() {
        navigator.back()
    }

}
