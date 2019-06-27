package money.rbk.sample.network.model

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 24.06.19
 */
data class InvoiceModel(
    val id: String,
    val shopId: String,
    val shopName: String,
    val invoiceAccessToken: String,
    val description: String
)
