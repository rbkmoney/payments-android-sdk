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

package money.rbk

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import money.rbk.presentation.activity.CheckoutActivity

object RbkMoney {

    const val CHECKOUT_REQUEST_CODE = 123

    fun startCheckout(fragment: Fragment, shopId: String, invoiceId: String,
        invoiceAccessToken: String) {
        fragment.startActivityForResult(buildIntent(fragment.requireActivity(),
            shopId,
            invoiceId,
            invoiceAccessToken),
            CHECKOUT_REQUEST_CODE)
    }

    fun startCheckout(activity: Activity, shopId: String, invoiceId: String,
        invoiceAccessToken: String) {
        activity.startActivityForResult(buildIntent(activity,
            shopId,
            invoiceId,
            invoiceAccessToken),
            CHECKOUT_REQUEST_CODE)
    }

    private fun buildIntent(activity: Activity, shopId: String, invoiceId: String,
        invoiceAccessToken: String): Intent =
        Intent(activity, CheckoutActivity::class.java).apply {
            putExtra(CheckoutActivity.KEY_SHOP_ID, shopId)
            putExtra(CheckoutActivity.KEY_INVOICE_ID, invoiceId)
            putExtra(CheckoutActivity.KEY_INVOICE_ACCESS_TOKEN, invoiceAccessToken)
        }

}