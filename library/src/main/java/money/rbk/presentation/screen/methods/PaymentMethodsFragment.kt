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
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.rbk_fmt_payment_methods.*
import money.rbk.R
import money.rbk.presentation.model.PaymentMethodModel
import money.rbk.presentation.screen.base.BaseFragment
import money.rbk.presentation.screen.common.MarginItemDecoration
import money.rbk.presentation.screen.methods.adapter.PaymentAdapter

internal class PaymentMethodsFragment : BaseFragment<PaymentMethodsView>(), PaymentMethodsView {

    override val presenter: PaymentMethodsPresenter by lazy { PaymentMethodsPresenter(navigator) }

    companion object {
        fun newInstance() = PaymentMethodsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.rbk_fmt_payment_methods, container, false)

    override fun setPaymentMethods(paymentMethods: List<PaymentMethodModel>) {
        rvPaymentMethods.adapter = PaymentAdapter({ presenter.onPaymentClick(it) }, paymentMethods)
        rvPaymentMethods.layoutManager = LinearLayoutManager(activity)
        rvPaymentMethods.addItemDecoration(MarginItemDecoration(resources.getDimension(R.dimen.rbk_spacing_xxsmall).toInt()))
    }

    override fun showProgress() {
        checkoutActivity.showProgress()
    }

    override fun hideProgress() {
        super.hideProgress()
        checkoutActivity.hideProgress()
    }

}
