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
import kotlinx.android.synthetic.main.fmt_payment_methods.*
import money.rbk.R
import money.rbk.presentation.model.PaymentMethodModel
import money.rbk.presentation.screen.base.BaseFragment
import money.rbk.presentation.screen.common.MarginItemDecoration
import money.rbk.presentation.screen.methods.adapter.PaymentAdapter

class PaymentMethodsFragment : BaseFragment<PaymentMethodsView>(), PaymentMethodsView {

    override val presenter: PaymentMethodsPresenter by lazy { PaymentMethodsPresenter(navigator) }

    companion object {
        fun newInstance() = PaymentMethodsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fmt_payment_methods, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setPaymentMethods(paymentMethods: List<PaymentMethodModel>) {
        rvPaymentMethods.adapter = PaymentAdapter(presenter::onPaymentClick, paymentMethods)
        rvPaymentMethods.layoutManager = LinearLayoutManager(activity)
        rvPaymentMethods.addItemDecoration(MarginItemDecoration(resources.getDimension(R.dimen.spacing_small).toInt()))
    }

    override fun showProgress() {
        pbLoading.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        pbLoading.visibility = View.GONE
    }

}
