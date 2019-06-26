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
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fmt_payment_results.*
import money.rbk.R
import money.rbk.presentation.activity.checkout.InitializeListener
import money.rbk.presentation.screen.base.BaseFragment
import money.rbk.presentation.screen.base.BasePresenter
import money.rbk.presentation.screen.card.ACTION_INITIALIZE
import money.rbk.presentation.screen.card.ACTION_UNKNOWN
import money.rbk.presentation.utils.makeGone
import money.rbk.presentation.utils.makeVisible

class ResultFragment : BaseFragment<ResultView>(), ResultView {


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is InitializeListener) {
            initializeListener = context
        }
    }

    private var initializeListener: InitializeListener? = null

    companion object {
        const val KEY_ACTION_RESULT = "key_action_result"

        private const val KEY_RESULT_TYPE = "key_result_type"
        private const val KEY_MESSAGE = "key_message"
        private const val KEY_ACTION_POSITIVE = "key_action_positive"
        private const val KEY_ACTION_NEGATIVE = "key_action_negative"
        const val REQUEST_ERROR = 0x0987


        fun newInstance(
                resultType: ResultType,
                message: String?,
                positiveAction: Int? = null,
                negativeAction: Int? = null
        ): ResultFragment {
            val fragment = ResultFragment()
            fragment.arguments = Bundle().apply {
                putSerializable(KEY_RESULT_TYPE, resultType)
                putString(KEY_MESSAGE, message)
                positiveAction?.let {
                    putInt(KEY_ACTION_POSITIVE, it)
                }
                negativeAction?.let {
                    putInt(KEY_ACTION_NEGATIVE, it)
                }

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
            ResultType.UNKNOWN -> showUnknown()
        }

    }

    override fun showProgress() = Unit

    override fun hideProgress() = Unit

    override fun showSuccess() {
        clSuccessful.makeVisible()
        clUnknown.makeGone()
        clUnsuccessful.makeGone()
        tvPaidWith.text = arguments?.getString(KEY_MESSAGE)

        btnOk.setOnClickListener {
            finish()
        }
    }

    override fun showError() {
        clSuccessful.makeGone()
        clUnknown.makeGone()
        clUnsuccessful.makeVisible()
        tvCause.text = arguments?.getString(KEY_MESSAGE)

        val actionPositive = arguments?.getInt(KEY_ACTION_POSITIVE, ACTION_UNKNOWN)
        val actionNegative = arguments?.getInt(KEY_ACTION_NEGATIVE, ACTION_UNKNOWN)

        if (actionNegative != ACTION_UNKNOWN) {
            btnUseAnotherCard.makeVisible()
        } else {
            btnUseAnotherCard.makeGone()
        }

        if (actionPositive != ACTION_UNKNOWN) {
            btnTryAgain.makeVisible()
        } else {
            btnTryAgain.makeGone()
        }

        btnTryAgain.setOnClickListener {
            (presenter as ResultPresenter).onTryAgain(actionPositive)
        }

        btnUseAnotherCard.setOnClickListener {
            sendResult(actionNegative!!)
        }
    }

    override fun showUnknown() {
        clSuccessful.makeGone()
        clUnknown.makeVisible()
        clUnsuccessful.makeGone()

        tvRefundMessage.text = arguments?.getString(KEY_MESSAGE)
    }

    override fun sendResult(action: Int) {
        if (action == ACTION_INITIALIZE) {
            initializeListener?.initialize()
        } else {

            val intent = Intent()
            targetFragment?.onActivityResult(REQUEST_ERROR, Activity.RESULT_OK, intent.apply {
                putExtra(KEY_ACTION_RESULT, action)
            })
        }
    }

    private fun finish() {
        activity?.setResult(Activity.RESULT_OK)
        activity?.finish()
    }

}