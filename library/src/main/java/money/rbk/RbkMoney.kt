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
import androidx.annotation.Keep
import money.rbk.presentation.activity.checkout.CheckoutActivity

object RbkMoney {

    @[JvmStatic JvmOverloads Keep]
    fun buildCheckoutIntent(
        activity: Activity,
        invoiceId: String,
        invoiceAccessToken: String,
        shopName: String,
        useTestEnvironment: Boolean = true,
        email: String? = null): Intent =
        CheckoutActivity.buildIntent(activity,
            invoiceId,
            invoiceAccessToken,
            shopName,
            useTestEnvironment,
            email)
}
