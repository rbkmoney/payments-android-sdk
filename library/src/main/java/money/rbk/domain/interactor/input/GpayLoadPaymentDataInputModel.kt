package money.rbk.domain.interactor.input

import money.rbk.domain.entity.Currency

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 02.07.19
 */
class GpayLoadPaymentDataInputModel(
    val price: String,
    val currency: Currency,
    val gatewayMerchantId: String
) : BaseInputModel()
