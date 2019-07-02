package money.rbk.presentation.model

class GpayPrepareInfoModel(
    val gatewayMerchantId: String,
    val checkoutInfoModel: CheckoutInfoModel
) : BaseIUModel()
