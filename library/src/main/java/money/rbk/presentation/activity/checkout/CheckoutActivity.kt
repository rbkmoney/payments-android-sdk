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
import money.rbk.presentation.dialog.showAlert
import money.rbk.presentation.model.InvoiceModel
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.utils.adjustSize

class CheckoutActivity : AppCompatActivity(), CheckoutView {

    companion object {
        private const val KEY_INVOICE_ID = "invoice_id"
        private const val KEY_INVOICE_ACCESS_TOKEN = "invoice_access_token"
        private const val KEY_SHOP_NAME = "shop_name"
        private const val KEY_EMAIL = "email"
        private const val KEY_USE_TEST_ENVIRONMENT = "use_test_environment"

        fun buildIntent(
            activity: Activity,
            invoiceId: String,
            invoiceAccessToken: String,
            shopName: String,
            useTestEnvironment: Boolean,
            email: String?
        ) = Intent(activity, CheckoutActivity::class.java)
            .apply {
                putExtra(KEY_INVOICE_ID, invoiceId)
                putExtra(KEY_INVOICE_ACCESS_TOKEN, invoiceAccessToken)
                putExtra(KEY_SHOP_NAME, shopName)
                putExtra(KEY_USE_TEST_ENVIRONMENT, useTestEnvironment)
                putExtra(KEY_EMAIL, email)
            }
    }

    val navigator by lazy { Navigator(this, R.id.container) }

    private val presenter by lazy { CheckoutPresenter(navigator) }

    private val isRootFragment
        get() = supportFragmentManager.backStackEntryCount <= 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_checkout)
        adjustSize()

        if (savedInstanceState == null) {

            val invoiceId = intent?.getStringExtra(KEY_INVOICE_ID)!!
            val invoiceAccessToken = intent?.getStringExtra(KEY_INVOICE_ACCESS_TOKEN)!!
            val shopName = intent?.getStringExtra(KEY_SHOP_NAME)!!
            val useTestEnvironment = intent?.getBooleanExtra(KEY_USE_TEST_ENVIRONMENT, false)!!
            val email = intent?.getStringExtra(KEY_EMAIL)!!

            Injector.init(applicationContext,
                invoiceId,
                invoiceAccessToken,
                shopName,
                useTestEnvironment,
                email)
        }

        presenter.attachView(this)
        initViews()

        supportFragmentManager.addOnBackStackChangedListener { onBackStackChanged() }
        onBackStackChanged()
    }

    override fun showInvoice(invoiceModel: InvoiceModel) {
        groupInvoiceInfo.visibility = View.VISIBLE
        tvShopName.text = invoiceModel.shopName
        tvPrice.text = invoiceModel.cost
        tvOrderDetails.text = invoiceModel.orderDetails
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    override fun showError() {
        showAlert(
            getString(R.string.error),
            getString(R.string.error_cant_load_invoice),
            R.string.label_try_again to {
                presenter.initializeInvoice()
            },
            R.string.cancel to {
                finish()
            })
    }

    private fun initViews() {
        ibtnBack.setOnClickListener {
            onBackPressed()
        }
        ibtnClose.setOnClickListener {
            finish()
        }
    }

    private fun onBackStackChanged() {
        ibtnBack.visibility = if (isRootFragment) View.INVISIBLE else View.VISIBLE
    }

    override fun showProgress() {
        lLoader.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        lLoader.visibility = View.INVISIBLE
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!navigator.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
