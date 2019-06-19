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

package money.rbk.presentation.screen.card

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import kotlinx.android.synthetic.main.fmt_card.*
import money.rbk.R
import money.rbk.presentation.activity.checkout.CheckoutActivity
import money.rbk.presentation.activity.web.WebViewActivity
import money.rbk.presentation.model.BrowserRequestModel
import money.rbk.presentation.screen.base.BaseFragment
import money.rbk.presentation.utils.hideKeyboard

class BankCardFragment : BaseFragment<BankCardView>(), BankCardView {

    override val presenter: BankCardPresenter by lazy { BankCardPresenter(navigator) }

    companion object {
        fun newInstance() = BankCardFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fmt_card, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnPay.text =
            getString(R.string.label_pay_f, (activity as? CheckoutActivity)?.getCost().orEmpty())
        (activity as? CheckoutActivity)?.setBackButtonVisibility(true)

        btnPay.setOnClickListener {
            activity?.hideKeyboard()
            presenter.onPerformPayment(
                edCardNumber.text.toString(),
                edCardDate.text.toString(),
                edCardCvv.text.toString(),
                edCardName.text.toString(),
                edEmail.text.toString()
            )
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun onDetach() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        super.onDetach()
    }

    override fun showRedirect(request: BrowserRequestModel) {
        startActivityForResult(WebViewActivity.buildIntent(activity!!, request),
            WebViewActivity.REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == WebViewActivity.REQUEST_CODE) {
            presenter.on3DsPerformed()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun clear() =
        sequenceOf(edCardNumber, edCardDate, edCardCvv, edCardName, edEmail)
            .forEach { it.setText(R.string.empty) }

    override fun showProgress() {
        btnPay.isEnabled = false
        pbLoading.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        btnPay.isEnabled = true
        pbLoading.visibility = View.GONE
    }
}