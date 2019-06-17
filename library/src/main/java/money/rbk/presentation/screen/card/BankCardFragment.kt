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
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import kotlinx.android.synthetic.main.fmt_card.*
import money.rbk.R
import money.rbk.data.CreditCardType
import money.rbk.data.CreditCardType.*
import money.rbk.presentation.activity.CheckoutActivity
import money.rbk.presentation.screen.base.BaseFragment
import money.rbk.presentation.screen.base.BasePresenter
import money.rbk.presentation.utils.extensions.setValid
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher


class BankCardFragment : BaseFragment<BankCardView>(), BankCardView {


    companion object {
        fun newInstance() = BankCardFragment()
    }

    override fun buildPresenter(): BasePresenter<BankCardView> = BankCardPresenter()

    override fun onCreateView(
        inflater: LayoutInflater,
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
            (presenter as BankCardPresenter).onBuyClick(
                cardNumber = edCardNumber.text.toString(),
                cardDate = edCardDate.text.toString(),
                cardCcv = edCardCvv.text.toString(),
                cardName = edCardName.text.toString(),
                cardEmail = edEmail.text.toString()
            )
        }

        setUpWatchers()

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun onDetach() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        super.onDetach()
    }

    override fun showProgress() {
        pbLoading.visibility = View.VISIBLE
    }

    override fun hideProgress() {
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

    override fun showNumberValid(isValid: Boolean, cardType: CreditCardType) {
        val cardDrawableId: Int = when (cardType) {
            VISA -> R.drawable.selector_logo_visa
            MASTERCARD -> R.drawable.ic__mastercard_logo
            MIR -> R.drawable.ic_mir
            else -> R.drawable.ic_unkwon_card
        }

        edCardNumber.setValid(isValid, cardDrawableId)
    }

    private fun setUpWatchers() {

        edEmail.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus.not()) {
                (presenter as BankCardPresenter).onEmail(edEmail.text.toString())
            }
        }

        edCardName.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus.not()) {
                (presenter as BankCardPresenter).onName(edCardName.text.toString())
            }
        }

        edCardDate.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus.not()) {
                (presenter as BankCardPresenter).onDate(edCardDate.text.toString())
            }
        }

        edCardCvv.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus.not()) {
                (presenter as BankCardPresenter).onCcv(edCardCvv.text.toString())
            }
        }

        edCardNumber.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus.not()) {
                (presenter as BankCardPresenter).onNumber(edCardNumber.text.toString())
            }
        }


        var watcher: MaskFormatWatcher

        val cardNumberMask = MaskImpl.createTerminated(PredefinedSlots.CARD_NUMBER_STANDARD)
        watcher = MaskFormatWatcher(cardNumberMask)
        watcher.setCallback(CardChangeListener())
        watcher.installOn(edCardNumber)
//        edCardNumber.addTextChangedListener(CardNumberTextWatcher(watcher))


        val dataSlots = UnderscoreDigitSlotsParser().parseSlots("__/__")
        val cardDataMask = MaskImpl.createTerminated(dataSlots)
        watcher = MaskFormatWatcher(cardDataMask)
        watcher.installOn(edCardDate)


    }


}