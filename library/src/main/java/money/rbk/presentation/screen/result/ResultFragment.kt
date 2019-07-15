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
import money.rbk.presentation.utils.getArgIntOrError
import money.rbk.presentation.utils.getArgStringOrError

internal class ResultFragment : BaseFragment<ResultView>(), ResultView {

    companion object {

        private const val KEY_MESSAGE = "message"
        private const val KEY_TITLE = "title"
        private const val KEY_STATE = "state"

        /* Error */
        private const val KEY_REPEAT_ACTION = "repeat_action"
        private const val KEY_USE_ANOTHER_CARD = "use_another_card"
        private const val KEY_ALL_PAYMENT_METHODS = "all_payment_methods"

        fun newInstanceSuccess(message: String
        ) = ResultFragment().apply {

            arguments = Bundle().apply {
                putInt(KEY_STATE, State.SUCCESS.ordinal)
                putString(KEY_MESSAGE, message)
            }
        }

        fun newInstanceError(
            message: String,
            repeatAction: Boolean = false,
            useAnotherCard: Boolean = false,
            allPaymentMethods: Boolean = false

        ) = ResultFragment().apply {

            arguments = Bundle().apply {
                putInt(KEY_STATE, State.ERROR.ordinal)
                putString(KEY_MESSAGE, message)
                putBoolean(KEY_REPEAT_ACTION, repeatAction)
                putBoolean(KEY_USE_ANOTHER_CARD, useAnotherCard)
                putBoolean(KEY_ALL_PAYMENT_METHODS, allPaymentMethods)
            }
        }

        fun newInstanceUnknown(
            message: String,
            title: String
        ) = ResultFragment().apply {

            arguments = Bundle().apply {
                putInt(KEY_STATE, State.UNKNOWN.ordinal)
                putString(KEY_TITLE, title)
                putString(KEY_MESSAGE, message)
            }
        }
    }

    override val presenter: ResultPresenter by lazy { ResultPresenter(navigator) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val state = State.values()[getArgIntOrError(KEY_STATE)]
        val message = getArgStringOrError(KEY_MESSAGE)

        when (state) {
            State.SUCCESS -> showSuccess(message)
            State.ERROR -> showError(
                message = message,
                repeatAction = arguments?.getBoolean(KEY_REPEAT_ACTION) ?: false,
                useAnotherCard = arguments?.getBoolean(KEY_USE_ANOTHER_CARD) ?: false,
                allPaymentMethods = arguments?.getBoolean(KEY_ALL_PAYMENT_METHODS) ?: false

            )
            State.UNKNOWN -> showUnknown(message)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fmt_payment_results, container, false)

    private fun showSuccess(message: String) {
        clSuccessful.visibility = View.VISIBLE
        clUnknown.visibility = View.GONE
        clUnsuccessful.visibility = View.GONE
        tvPaidWith.text = message
        activity?.setResult(Activity.RESULT_OK)
        btnOk.setOnClickListener { finish() }
    }

    private fun showError(
        message: String,
        repeatAction: Boolean,
        useAnotherCard: Boolean,
        allPaymentMethods: Boolean) {

        clSuccessful.visibility = View.GONE
        clUnknown.visibility = View.GONE
        clUnsuccessful.visibility = View.VISIBLE

        tvCause.text = message

        btnAllPaymentMethods.setOnClickListener {
            presenter.onAllPaymentMethods()
        }

        if (repeatAction) {
            btnRetry.visibility = View.VISIBLE
            btnRetry.setOnClickListener {
                presenter.onRepeatAction()
            }
        } else {
            btnRetry.visibility = View.GONE
        }

        if (useAnotherCard) {
            btnUseAnotherCard.visibility = View.VISIBLE
            btnUseAnotherCard.setOnClickListener { presenter.onUseAnotherCard() }
        } else {
            btnUseAnotherCard.visibility = View.GONE
        }

        if (allPaymentMethods) {
            btnAllPaymentMethods.visibility = View.VISIBLE
            btnAllPaymentMethods.setOnClickListener { presenter.onAllPaymentMethods() }
        } else {
            btnAllPaymentMethods.visibility = View.GONE
        }
    }

    private fun showUnknown(message: String) {
        clSuccessful.visibility = View.GONE
        clUnknown.visibility = View.VISIBLE
        clUnsuccessful.visibility = View.GONE
        tvPaymentRefund.text = getArgStringOrError(KEY_TITLE)
        tvRefundMessage.text = message
    }

    private fun finish() {
        activity?.setResult(Activity.RESULT_OK)
        activity?.finish()
    }

    private enum class State {
        SUCCESS, ERROR, UNKNOWN
    }

}
