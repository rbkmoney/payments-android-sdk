package money.rbk.presentation.screen.gpay

import money.rbk.domain.interactor.input.GpayLoadPaymentDataInputModel
import money.rbk.presentation.screen.base.BasePaymentView

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 27.06.19
 */
interface GpayView : BasePaymentView {

    fun onReadyToPay(gpayLoadPaymentDataInputModel : GpayLoadPaymentDataInputModel)

    fun showEmailValid(isValid: Boolean)
}
