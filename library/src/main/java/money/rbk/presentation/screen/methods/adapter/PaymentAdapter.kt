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

package money.rbk.presentation.screen.methods.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent.*
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.item_payment_methods.view.*
import money.rbk.R
import money.rbk.presentation.model.PaymentMethodModel
import money.rbk.presentation.screen.methods.adapter.PaymentAdapter.PaymentHolder
import money.rbk.presentation.utils.setTextColorResource

class PaymentAdapter(
        private val onItemClickListener: (PaymentMethodModel) -> Unit,
        private var payments: List<PaymentMethodModel>) : RecyclerView.Adapter<PaymentHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHolder =
            PaymentHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.item_payment_methods, parent, false),
                    onItemClickListener
            )

    override fun getItemCount(): Int = payments.size

    override fun onBindViewHolder(holder: PaymentHolder, position: Int) =
            holder.bind(payments[position])

    class PaymentHolder(
            private val view: View,
            private val onItemClickListener: (PaymentMethodModel) -> Unit) : ViewHolder(view) {

        private lateinit var paymentItem: PaymentMethodModel

        init {
            view.setOnClickListener { onItemClickListener(paymentItem) }
        }

        fun bind(payment: PaymentMethodModel) =
                with(payment) {
                    paymentItem = this

                    if (name == null) {
                        view.tvPaymentType.visibility = View.GONE
                    } else {
                        view.tvPaymentType.setText(name)
                    }

                    if (description == null) {
                        view.tvDescription.visibility = View.GONE
                    } else {
                        view.tvDescription.setText(description)
                    }

                    view.ivPaymentIcon.setImageResource(icon)

                    view.setOnTouchListener { v, event ->
                        when (event.action) {
                            ACTION_UP -> {
                                ripple(v, false)
                                onItemClickListener(paymentItem)
                            }
                            ACTION_CANCEL  -> {
                                ripple(v, false)
                            }
                            ACTION_DOWN -> {
                                ripple(v, true)
                            }
                        }
                        return@setOnTouchListener true

                    }
                }

        private fun ripple(view: View, isRipple: Boolean) =
                if (isRipple) {
                    view.setBackgroundResource(R.drawable.background_payment_pressed)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        view.tvDescription.setTextAppearance(R.style.TextAppearance_RBKMoney_Description_White)
                        view.tvPaymentType.setTextAppearance(R.style.TextAppearance_RBKMoney_Headline_White)
                    } else {
                        view.tvDescription.setTextColorResource(R.color.white)
                        view.tvPaymentType.setTextColorResource(R.color.white)
                    }
                    view.ivPaymentIcon.isActivated = true
                } else {
                    view.setBackgroundResource(R.drawable.background_payment)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        view.tvDescription.setTextAppearance(R.style.TextAppearance_RBKMoney_Description)
                        view.tvPaymentType.setTextAppearance(R.style.TextAppearance_RBKMoney_Headline_BlueLight)
                    } else {
                        view.tvDescription.setTextColorResource(R.color.blue_light)
                        view.tvPaymentType.setTextColorResource(R.color.white)
                    }
                    view.ivPaymentIcon.isActivated = false
                }

    }
}
