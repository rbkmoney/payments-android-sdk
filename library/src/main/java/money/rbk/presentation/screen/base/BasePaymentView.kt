package money.rbk.presentation.screen.base

import money.rbk.presentation.model.BrowserRequestModel

/**
 * @author Arthur Korchagin (artur.korchagin@simbirsoft.com)
 * @since 09.07.19
 */
interface BasePaymentView : BaseView {
    fun showRedirect(request: BrowserRequestModel)
}
