package money.rbk.data.methods

import money.rbk.data.methods.base.GetRequest
import money.rbk.domain.entity.Payment

/**
 * @author Arthur Korchagin (arth.korchagin@gmail.com)
 * @since 15.07.19
 */
internal class GetPayments(
    override val invoiceAccessToken: String,
    invoiceId: String
) : GetRequest<List<Payment>> {

    override val endpoint = "/processing/invoices/$invoiceId/payments"
}
