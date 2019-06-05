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
import kotlinx.android.synthetic.main.item_payment_method.view.ivPaymentIcon
import kotlinx.android.synthetic.main.item_payment_method.view.tvPaymentType
import kotlinx.android.synthetic.main.item_payment_method_description.view.*
import money.rbk.R
import java.util.*

class PaymentAdapter : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        private const val TYPE_NORMAL = 0
        private const val TYPE_DESCRIPTION = 1
        private const val TYPE_ICON = 2
    }

    var payments: List<PaymentTestItem> = Collections.emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            when (viewType) {
                TYPE_DESCRIPTION -> PaymentDescriptionHolder(
                        LayoutInflater
                                .from(parent.context)
                                .inflate(R.layout.item_payment_method_description, parent, false)
                )
                TYPE_NORMAL -> PaymentHolder(
                        LayoutInflater
                                .from(parent.context)
                                .inflate(R.layout.item_payment_method, parent, false)
                )
                TYPE_ICON -> PaymentHolder(
                        LayoutInflater
                                .from(parent.context)
                                .inflate(R.layout.item_payment_method_icon, parent, false)
                )
                else -> throw IllegalArgumentException()
            }

    override fun getItemCount(): Int = payments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            when (holder) {
                is PaymentHolder -> holder.bind(payments[position])
                is PaymentDescriptionHolder -> holder.bind(payments[position])
                is PaymentIconHolder -> holder.bind(payments[position])
                else -> throw IllegalArgumentException()
            }


    override fun getItemViewType(position: Int): Int =
            when{
                payments[position].name == null -> TYPE_ICON
                payments[position].description == null -> TYPE_NORMAL
                else -> TYPE_DESCRIPTION
            }

    class PaymentDescriptionHolder(private val view: View) : ViewHolder(view) {

        fun bind(payment: PaymentTestItem) =
                with(payment) {
                    name?.let {
                        view.tvPaymentType.setText(name)
                    }
                    description?.let {
                        view.tvDescription.setText(description)
                    }
                    view.ivPaymentIcon.setImageResource(icon)
                }

    }

    class PaymentIconHolder(private val view: View) : ViewHolder(view) {

        fun bind(payment: PaymentTestItem) =
                with(payment) {
                    view.ivPaymentIcon.setImageResource(icon)
                }
    }

    class PaymentHolder(private val view: View) : ViewHolder(view) {

        fun bind(payment: PaymentTestItem) =
                with(payment) {
                    name?.let {
                        view.tvPaymentType.setText(name)
                    }
                    view.ivPaymentIcon.setImageResource(icon)
                }
    }


}
