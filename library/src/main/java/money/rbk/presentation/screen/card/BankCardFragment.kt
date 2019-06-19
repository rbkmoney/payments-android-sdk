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

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.whiteelephant.monthpicker.MonthPickerDialog
import kotlinx.android.synthetic.main.fmt_card.*
import money.rbk.R
import money.rbk.data.CreditCardType
import money.rbk.presentation.activity.checkout.CheckoutActivity
import money.rbk.presentation.activity.web.WebViewActivity
import money.rbk.presentation.model.BrowserRequestModel
import money.rbk.presentation.screen.base.BaseFragment
import money.rbk.presentation.utils.hideKeyboard
import money.rbk.presentation.utils.setRightDrawable
import money.rbk.presentation.utils.setValid
import money.rbk.presentation.utils.toDozenString
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.util.Calendar
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR

class BankCardFragment : BaseFragment<BankCardView>(), BankCardView,
    MonthPickerDialog.OnDateSetListener {

    override val presenter: BankCardPresenter by lazy { BankCardPresenter(navigator) }

    companion object {
        fun newInstance() = BankCardFragment()
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fmt_card, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnPay.text =
            getString(R.string.label_pay_f, (activity as? CheckoutActivity)?.getCost().orEmpty())
        (activity as? CheckoutActivity)?.setBackButtonVisibility(true)

        btnPay.setOnClickListener {
            activity?.hideKeyboard()
            presenter.onPerformPayment(
                cardNumber = edCardNumber.text.toString(),
                expDate = edCardDate.text.toString(),
                cvv = edCardCvv.text.toString(),
                cardHolder = edCardName.text.toString(),
                email = edEmail.text.toString()
            )
        }

        edCardDate.setOnClickListener {
            setUpDateDialog().show()
        }
        setUpWatchers()

    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(selectedMonth: Int, selectedYear: Int) {
        val selectedMonthString = (selectedMonth + 1).toDozenString()

        edCardDate.setText("$selectedMonthString/${selectedYear % 100}")
        presenter.onDate(edCardDate.text.toString())
    }

    private fun setUpDateDialog(): MonthPickerDialog {

        val currentDate = Calendar.getInstance()
        val currentMonth = currentDate.get(MONTH)
        val currentYear = currentDate.get(YEAR)
        return MonthPickerDialog.Builder(
            activity,
            this,
            currentYear,
            currentMonth
        )
            .setActivatedMonth(currentMonth)
            .setActivatedYear(currentYear)
            .setMaxYear(3000)
            .setMinYear(currentYear)
            .build()
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

    override fun showNumberValid(isValid: Boolean, cardType: CreditCardType?) {
        edCardNumber.setValid(isValid, CreditCardType.getDrawable(cardType))
    }

    private fun onCardDetected(cardType: CreditCardType?) {
        val cardDrawableId: Int? = CreditCardType.getDrawable(cardType)
        edCardNumber.setRightDrawable(cardDrawableId)
    }

    private fun setUpWatchers() {

        edEmail.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus.not()) {
                presenter.onEmail(edEmail.text.toString())
            }
        }

        edCardName.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus.not()) {
                presenter.onName(edCardName.text.toString())
            }
        }

        edCardCvv.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus.not()) {
                presenter.onCcv(edCardCvv.text.toString())
            }
        }

        edCardNumber.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus.not()) {
                presenter.onNumber(edCardNumber.text.toString())
            }
        }

        // var watcher: MaskFormatWatcher

        val cardNumberMask = MaskImpl.createNonTerminated(PredefinedSlots.CARD_NUMBER_STANDARD)
        MaskFormatWatcher(cardNumberMask)
            .apply {
                setCallback(CardChangeListener(::onCardDetected))
                installOn(edCardNumber)
            }

    }

}