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
import money.rbk.sample.network.model.InvoiceModel

class InvoiceTemplatesAdapter(
    invoiceTemplates: List<InvoiceTemplateResponse>,
    invoices: List<InvoiceModel>,
    private val onTemplateBuyListener: OnTemplateBuyListener,
    private val onInvoiceListener: OnInvoiceListener
) : RecyclerView.Adapter<InvoiceTemplatesAdapter.BaseVH>() {

    companion object {
        private const val VIEW_TYPE_INVOICE_TEMPLATE = 0
        private const val VIEW_TYPE_INVOICE = 1
    }

    private val items = invoiceTemplates + invoices

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH =
        when (viewType) {
            VIEW_TYPE_INVOICE_TEMPLATE -> InvoiceTemplatesVH(LayoutInflater.from(parent.context).inflate(
                R.layout.item_invoice_template,
                parent,
                false), onTemplateBuyListener)
            VIEW_TYPE_INVOICE -> InvoiceVH(LayoutInflater.from(parent.context).inflate(R.layout.item_invoice_template,
                parent, false), onInvoiceListener)
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is InvoiceTemplateResponse -> VIEW_TYPE_INVOICE_TEMPLATE
            is InvoiceModel -> VIEW_TYPE_INVOICE
            else -> throw IllegalArgumentException("Unknown item type at position: $position")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseVH, position: Int) {
        holder.bind(items[position])
    }

    abstract class BaseVH(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        abstract fun bind(item: Any)

    }

    class InvoiceTemplatesVH(
        override val containerView: View,
        private val onTemplateBuyListener: OnTemplateBuyListener) :
        BaseVH(containerView), LayoutContainer {

        private lateinit var invoiceTemplateResponse: InvoiceTemplateResponse

        private val context = itemView.context

        init {
            itemView.btnBuy.setOnClickListener { onTemplateBuyListener(invoiceTemplateResponse) }
        }

        override fun bind(item: Any) {
            val invoiceTemplate = item as InvoiceTemplateResponse

            invoiceTemplateResponse = invoiceTemplate

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

    class InvoiceVH(
        override val containerView: View,
        private val onInvoiceListener: OnInvoiceListener) :
        BaseVH(containerView), LayoutContainer {

        private lateinit var invoice: InvoiceModel

        private val context = itemView.context

        init {
            itemView.btnBuy.setOnClickListener { onInvoiceListener(invoice) }
        }

        override fun bind(item: Any) {
            invoice = item as InvoiceModel
            itemView.tvProductName.text = "Тестовый инвойс"
            itemView.tvProductDescription.text = invoice.description
            itemView.tvIcon.visibility = View.GONE
            itemView.btnBuy.text = context.getString(R.string.label_buy)
        }
    }
}

typealias OnTemplateBuyListener = (InvoiceTemplateResponse) -> Unit
typealias OnInvoiceListener = (InvoiceModel) -> Unit
