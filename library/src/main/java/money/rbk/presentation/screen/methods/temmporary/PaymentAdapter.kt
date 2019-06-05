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

package money.rbk.presentation.screen.methods.temmporary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.item_payment_methods.view.*
import money.rbk.R
import money.rbk.presentation.screen.methods.temmporary.PaymentAdapter.PaymentHolder
import java.util.*

class PaymentAdapter : RecyclerView.Adapter<PaymentHolder>() {

    companion object {
        private const val TYPE_NORMAL = 0
        private const val TYPE_DESCRIPTION = 1
        private const val TYPE_ICON = 2
    }

    var payments: List<PaymentTestItem> = Collections.emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHolder =
            PaymentHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.item_payment_methods, parent, false)
            )


    override fun getItemCount(): Int = payments.size

    override fun onBindViewHolder(holder: PaymentHolder, position: Int) =
            holder.bind(payments[position])


    class PaymentHolder(private val view: View) : ViewHolder(view) {

        fun bind(payment: PaymentTestItem) =
                with(payment) {

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
                }

    }


}
