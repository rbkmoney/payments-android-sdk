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

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import money.rbk.R
import money.rbk.presentation.screen.base.BaseFragment
import money.rbk.presentation.screen.base.BasePresenter

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
        Log.d(javaClass.name,"xxx-> onViewCreated ->")
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun onDetach() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        super.onDetach()
    }

    override fun buildPresenter(): BasePresenter<PaymentMethodsView> = PaymentMethodsPresenter()

}