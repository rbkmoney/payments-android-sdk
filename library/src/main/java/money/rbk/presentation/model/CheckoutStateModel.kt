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

package money.rbk.presentation.model

import androidx.annotation.StringRes

internal sealed class CheckoutStateModel : BaseIUModel() {

    internal class Success(val paymentToolName: String, val email: String) : CheckoutStateModel()

    internal object Pending : CheckoutStateModel()

    internal data class PaymentFailed(@StringRes val reasonResId: Int, val canRetry: Boolean) : CheckoutStateModel()

    internal class InvoiceFailed(@StringRes val reasonResId: Int) : CheckoutStateModel()

    internal class Warning(@StringRes val titleId: Int, @StringRes val messageResId: Int) :
        CheckoutStateModel()

    internal object PaymentProcessing : CheckoutStateModel()

    internal class BrowserRedirectInteraction(val request: BrowserRequestModel) : CheckoutStateModel()

}
