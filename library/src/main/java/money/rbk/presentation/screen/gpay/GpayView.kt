package money.rbk.presentation.screen.gpay

import money.rbk.presentation.screen.base.BasePaymentView

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 27.06.19
 */
interface GpayView : BasePaymentView {

    fun onReadyToPay()

    fun showEmailValid(isValid: Boolean)
}
