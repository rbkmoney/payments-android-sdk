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

package money.rbk.sample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_invoice_template.view.*
import money.rbk.sample.R
import money.rbk.sample.activity.utils.formatPrice
import money.rbk.sample.network.model.InvoiceTemplateResponse

class InvoiceTemplatesAdapter(
    private val items: List<InvoiceTemplateResponse>,
    private val onTemplateBuyListener: OnTemplateBuyListener
) : RecyclerView.Adapter<InvoiceTemplatesAdapter.InvoiceTemplatesVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceTemplatesVH =
        InvoiceTemplatesVH(LayoutInflater.from(parent.context).inflate(R.layout.item_invoice_template,
            parent, false), onTemplateBuyListener)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: InvoiceTemplatesVH, position: Int) =
        holder.bind(items[position])

    class InvoiceTemplatesVH(
        override val containerView: View,
        private val onTemplateBuyListener: OnTemplateBuyListener) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        private lateinit var item: InvoiceTemplateResponse

        private val context = itemView.context

        init {
            itemView.btnBuy.setOnClickListener { onTemplateBuyListener(item) }
        }

        fun bind(invoiceTemplate: InvoiceTemplateResponse) {

            item = invoiceTemplate

            itemView.tvProductName.text =
                invoiceTemplate.details.product.ifBlank { context.getString(R.string.label_there_is_no_name) }
            itemView.tvProductDescription.text = invoiceTemplate.description
                ?: context.getString(R.string.label_there_is_no_description)

            val emoji = invoiceTemplate.metadata?.emoji
            if (emoji != null) {
                itemView.tvIcon.text = emoji
                itemView.tvIcon.visibility = View.VISIBLE
            } else {
                itemView.tvIcon.visibility = View.GONE
            }

            itemView.btnBuy.text = context.getString(R.string.label_cost,
                invoiceTemplate.details.price.amount.formatPrice(),
                invoiceTemplate.details.price.currency.symbol)
        }
    }
}

typealias OnTemplateBuyListener = (InvoiceTemplateResponse) -> Unit