package money.rbk.presentation.screen.gpay

import money.rbk.presentation.model.BrowserRequestModel
import money.rbk.presentation.screen.base.BaseView

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 27.06.19
 */
interface GpayView : BaseView {

    fun onReadyToPay()

    fun showRedirect(request: BrowserRequestModel)

    fun showEmailValid(isValid: Boolean)
}
