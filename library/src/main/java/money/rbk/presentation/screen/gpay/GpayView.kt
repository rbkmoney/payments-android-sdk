package money.rbk.presentation.screen.gpay

import money.rbk.domain.interactor.input.GpayLoadPaymentDataInputModel
import money.rbk.presentation.screen.base.BasePaymentView

internal interface GpayView : BasePaymentView {

    fun onReadyToPay(gpayLoadPaymentDataInputModel : GpayLoadPaymentDataInputModel)

    fun showEmailValid(isValid: Boolean)
}
