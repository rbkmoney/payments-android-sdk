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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.rbk_item_payment_methods.view.*
import money.rbk.R
import money.rbk.presentation.model.PaymentMethodModel
import money.rbk.presentation.screen.methods.adapter.PaymentAdapter.PaymentHolder

internal class PaymentAdapter(
    private val onItemClickListener: (PaymentMethodModel) -> Unit,
    private var payments: List<PaymentMethodModel>) : RecyclerView.Adapter<PaymentHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHolder =
        PaymentHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.rbk_item_payment_methods, parent, false),
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
                    view.tvPaymentType.visibility = View.VISIBLE
                    view.tvPaymentType.setText(name)
                }

                if (description == null) {
                    view.tvDescription.visibility = View.GONE
                } else {
                    view.tvDescription.visibility = View.VISIBLE
                    view.tvDescription.setText(description)
                }

                if(icon == null) {
                    view.ivPaymentIcon.visibility = View.GONE
                } else {
                    view.ivPaymentIcon.visibility = View.VISIBLE
                    view.ivPaymentIcon.setImageResource(icon)
                }

            }

    }
}
