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

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import money.rbk.R
import money.rbk.presentation.screen.methods.PaymentMethodsFragment
import money.rbk.presentation.utils.replaceFragmentInActivity

class CheckoutActivity : AppCompatActivity() {

    companion object {
        const val KEY_INVOICE_ID = "invoice_id"
        const val KEY_INVOICE_ACCESS_TOKEN = "invoice_access_token"
        const val KEY_SHOP_ID = "shop_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_checkout)
        // TODO: Make Proper Navigation
        replaceFragmentInActivity(PaymentMethodsFragment.newInstance(), R.id.container)
    }

}