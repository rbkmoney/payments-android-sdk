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

package money.rbk.presentation.screen.methods

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fmt_payment_methods.*
import money.rbk.R
import money.rbk.presentation.screen.base.BaseFragment
import money.rbk.presentation.screen.base.BasePresenter
import money.rbk.presentation.screen.card.BankCardFragment
import money.rbk.presentation.utils.replaceFragmentInActivity

class PaymentMethodsFragment : BaseFragment<PaymentMethodsView>(), PaymentMethodsView {

    companion object {
        fun newInstance() = PaymentMethodsFragment() //TODO: Add Args
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fmt_payment_methods, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO: Make proper navigation
        btnTestPaymentMethod.setOnClickListener {
            activity?.replaceFragmentInActivity(BankCardFragment.newInstance(), R.id.container)
        }
    }

    override fun buildPresenter(): BasePresenter<PaymentMethodsView> = PaymentMethodsPresenter()

}