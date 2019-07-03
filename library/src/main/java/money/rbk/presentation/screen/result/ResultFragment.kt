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

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fmt_payment_results.*
import money.rbk.R
import money.rbk.presentation.screen.base.BaseFragment
import money.rbk.presentation.utils.arg
import money.rbk.presentation.utils.makeGone
import money.rbk.presentation.utils.makeVisible

class ResultFragment : BaseFragment<ResultView>(), ResultView {

    companion object {
        private const val KEY_RESULT_TYPE = "key_result_type"

        private const val KEY_MESSAGE = "key_message"

        private const val KEY_POSITIVE_ACTION = "key_positive_action"
        private const val KEY_NEGATIVE_ACTION = "key_negative_action"

        const val REQUEST_ERROR = 0x0987

        fun newInstance(
            resultType: ResultType,
            message: String?,
            positiveAction: ResultAction?,
            negativeAction: ResultAction?
        ) = ResultFragment().apply {

            arguments = Bundle().apply {
                putInt(KEY_RESULT_TYPE, resultType.ordinal)
                putString(KEY_MESSAGE, message)

                putInt(KEY_POSITIVE_ACTION, positiveAction?.ordinal ?: -1)
                putInt(KEY_NEGATIVE_ACTION, negativeAction?.ordinal ?: -1)
            }
        }
    }

    override val presenter: ResultPresenter by lazy { ResultPresenter(navigator) }

    private val resultTypeOrdinal by arg<Int>(KEY_RESULT_TYPE)
    private val message by arg<String>(KEY_MESSAGE)
    private val positiveActionOrdinal by arg<Int>(KEY_POSITIVE_ACTION)
    private val negativeActionOrdinal by arg<Int>(KEY_NEGATIVE_ACTION)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fmt_payment_results, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (ResultType.values()[resultTypeOrdinal]) {

            ResultType.SUCCESS -> showSuccess()

            ResultType.ERROR -> showError()

            ResultType.UNKNOWN -> showUnknown()
        }
    }

    override fun showProgress() = Unit

    override fun hideProgress() = Unit

    override fun showSuccess() {
        clSuccessful.makeVisible()
        clUnknown.makeGone()
        clUnsuccessful.makeGone()
        tvPaidWith.text = message

        btnOk.setOnClickListener {
            finish()
        }
    }

    override fun showError() {
        clSuccessful.makeGone()
        clUnknown.makeGone()
        clUnsuccessful.makeVisible()
        tvCause.text = message

        btnAllPaymentMethods.setOnClickListener {
            navigator.clearOpenPaymentMethods()
        }

        val positiveAction = ResultAction.values()
            .getOrNull(positiveActionOrdinal)
        if (positiveAction != null) {
            btnPositiveAction.setText(positiveAction.buttonTitle)
            btnPositiveAction.makeVisible()
            btnPositiveAction.setOnClickListener { presenter.onAction(positiveAction) }
        } else {
            btnPositiveAction.makeGone()
        }

        val negativeAction = ResultAction.values()
            .getOrNull(negativeActionOrdinal)
        if (negativeAction != null) {
            btnNegativeAction.setText(negativeAction.buttonTitle)
            btnNegativeAction.makeVisible()
            btnNegativeAction.setOnClickListener { presenter.onAction(negativeAction) }
        } else {
            btnNegativeAction.makeGone()
        }
    }

    override fun showUnknown() {
        clSuccessful.makeGone()
        clUnknown.makeVisible()
        clUnsuccessful.makeGone()

        tvRefundMessage.text = message
    }

    private fun finish() {
        activity?.setResult(Activity.RESULT_OK)
        activity?.finish()
    }

}