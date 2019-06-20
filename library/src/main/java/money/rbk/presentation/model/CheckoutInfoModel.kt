package money.rbk.presentation.model

class CheckoutInfoModel(
    val cost: String,
    val checkoutState: CheckoutState
) : BaseIUModel()