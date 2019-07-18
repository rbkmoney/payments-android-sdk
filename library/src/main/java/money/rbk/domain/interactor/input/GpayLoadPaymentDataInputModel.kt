package money.rbk.domain.interactor.input

import money.rbk.domain.entity.Currency

internal class GpayLoadPaymentDataInputModel(
    val price: String,
    val currency: Currency
) : BaseInputModel()
