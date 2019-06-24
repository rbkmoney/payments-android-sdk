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

package money.rbk.presentation.screen.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fmt_payment_results.*
import money.rbk.R
import money.rbk.presentation.screen.base.BaseFragment
import money.rbk.presentation.screen.base.BasePresenter
import money.rbk.presentation.utils.makeGone
import money.rbk.presentation.utils.makeVisible

class ResultFragment : BaseFragment<ResultView>(), ResultView {

    companion object {

        private const val KEY_RESULT_TYPE = "key_result_type"
        private const val KEY_MESSAGE = "key_message"
        const val REQUEST_ERROR = 0
        const val REQUEST_SUCCESS = 1


        fun newInstance(resultType: ResultType, message: String?): ResultFragment {
            val fragment = ResultFragment()
            fragment.arguments = Bundle().apply {
                putSerializable(KEY_RESULT_TYPE, resultType)
                putString(KEY_MESSAGE, message)
            }
            return fragment
        }
    }


    override val presenter: BasePresenter<ResultView> by lazy { ResultPresenter(navigator) }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? =
            inflater.inflate(R.layout.fmt_payment_results, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (arguments?.get(KEY_RESULT_TYPE)) {
            ResultType.SUCCESS -> showSuccess()
            ResultType.ERROR -> showError()
        }

    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun showSuccess() {
        clSuccessful.makeVisible()
        clUnsuccessful.makeGone()
        tvPaidWith.text = arguments?.getString(KEY_MESSAGE)
    }

    override fun showError() {
        clSuccessful.makeGone()
        clUnsuccessful.makeVisible()
        tvCause.text = arguments?.getString(KEY_MESSAGE)

        btnTryAgain.setOnClickListener {

        }
    }

}