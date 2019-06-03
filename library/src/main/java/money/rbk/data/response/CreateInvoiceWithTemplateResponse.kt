package money.rbk.data.response

import money.rbk.data.serialization.Deserializer
import money.rbk.domain.entity.Invoice
import org.json.JSONObject

internal data class CreateInvoiceWithTemplateResponse(
    val invoice: Invoice,
    val invoiceAccessToken: String
) {
    companion object :
        Deserializer<JSONObject, CreateInvoiceWithTemplateResponse> {

        override fun fromJson(json: JSONObject): CreateInvoiceWithTemplateResponse =
            CreateInvoiceWithTemplateResponse(
                invoice = Invoice.fromJson(json.getJSONObject("invoice")),
                invoiceAccessToken = json.getJSONObject("invoiceAccessToken").getString("payload")
            )
    }
}
