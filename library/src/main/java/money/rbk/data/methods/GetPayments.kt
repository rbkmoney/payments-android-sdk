package money.rbk.data.methods

import money.rbk.data.methods.base.GetRequest
import money.rbk.domain.entity.Payment

internal class GetPayments(
    override val invoiceAccessToken: String,
    invoiceId: String
) : GetRequest<List<Payment>> {

    override val endpoint = "/processing/invoices/$invoiceId/payments"
}
