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
import kotlinx.android.extensions.CacheImplementation
import kotlinx.android.extensions.ContainerOptions
import kotlinx.android.synthetic.main.fmt_card.*
import money.rbk.R
import money.rbk.di.Injector
import money.rbk.domain.entity.CreditCardType
import money.rbk.presentation.activity.web.Web3DSecureActivity
import money.rbk.presentation.model.BrowserRequestModel
import money.rbk.presentation.screen.base.BaseFragment
import money.rbk.presentation.utils.clearState
import money.rbk.presentation.utils.hideKeyboard
import money.rbk.presentation.utils.removeRightDrawable
import money.rbk.presentation.utils.removeSpaces
import money.rbk.presentation.utils.setRightDrawable
import money.rbk.presentation.utils.setValid
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

@ContainerOptions(cache = CacheImplementation.NO_CACHE)
internal class BankCardFragment : BaseFragment<BankCardView>(), BankCardView {

    companion object {
        fun newInstance() = BankCardFragment()
    }

    override val presenter: BankCardPresenter by lazy { BankCardPresenter(navigator) }

    private val userEmail = Injector.email

    private val maskFormatWatcher by lazy {
        val cardNumberMask = MaskImpl.createNonTerminated(PredefinedSlots.CARD_NUMBER_STANDARD)
        MaskFormatWatcher(cardNumberMask)
            .apply { setCallback(CardChangeListener { onCardDetected(it) }) }
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fmt_card, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.requestFocus()
        btnPay.setOnClickListener {
            activity?.hideKeyboard()
            view.requestFocus()
            presenter.onPerformPayment(
                cardNumber = edCardNumber.text?.removeSpaces() ?: "",
                expDate = edCardDate.text.toString(),
                cvv = edCardCvv.text.toString(),
                cardHolder = edCardName.text.toString(),
                email = edEmail.text.toString()
            )
        }

        edCardDate.setOnClickListener {
            presenter.onDateSelect()
        }
        if (edEmail.text.isBlank()) {
            edEmail.setText(userEmail)
        }
        setUpWatchers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        maskFormatWatcher.removeFromTextView()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun onDetach() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        super.onDetach()
    }

    override fun setCost(cost: String) {
        btnPay.text = getString(R.string.rbc_label_pay_f, cost)
    }

    override fun setCardDate(formatMonthYear: String) {
        edCardDate.setText(formatMonthYear)
    }

    override fun showRedirect(request: BrowserRequestModel) {
        startActivityForResult(
            Web3DSecureActivity.buildIntent(activity!!, request),
            Web3DSecureActivity.REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Web3DSecureActivity.REQUEST_CODE -> presenter.on3DsPerformed(resultCode)
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun showProgress() {
        btnPay.isEnabled = false
        edCardNumber.isEnabled = false
        edCardDate.isEnabled = false
        edCardCvv.isEnabled = false
        edCardName.isEnabled = false
        edEmail.isEnabled = false
        pbLoading.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        super.hideProgress()
        btnPay.isEnabled = true
        edCardNumber.isEnabled = true
        edCardDate.isEnabled = true
        edCardCvv.isEnabled = true
        edCardName.isEnabled = true
        edEmail.isEnabled = true
        pbLoading.visibility = View.GONE
    }

    override fun showEmailValid(isValid: Boolean) {
        edEmail.setValid(isValid)
    }

    override fun showDateValid(isValid: Boolean) {
        edCardDate.setValid(isValid)
    }

    override fun showNameValid(isValid: Boolean) {
        edCardName.setValid(isValid)
    }

    override fun showCcvValid(isValid: Boolean) {
        edCardCvv.setValid(isValid)
    }

    override fun showNumberValid(cardType: CreditCardType?) {
        edCardNumber.setValid(cardType != null, cardType?.iconRes)
    }

    private fun onCardDetected(cardType: CreditCardType?) =
        if (cardType != null) {
            edCardNumber.setRightDrawable(cardType.iconRes)
        } else {
            edCardNumber.removeRightDrawable()
        }

    private fun setUpWatchers() {
        edEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus.not()) {
                presenter.validateEmail(edEmail.text.toString())
            } else {
                edEmail.clearState()
            }
        }

        edCardName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus.not()) {
                presenter.validateCardholder(edCardName.text.toString())
            } else {
                edCardName.clearState()
            }
        }

        edCardCvv.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus.not()) {
                presenter.validateCcv(edCardCvv.text.toString())
            } else {
                edCardCvv.clearState()
            }
        }

        edCardNumber.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                presenter.validateNumber(edCardNumber.text.toString())
            } else {
                edCardNumber.clearState()
            }
        }

        maskFormatWatcher.installOn(edCardNumber)
    }
}
