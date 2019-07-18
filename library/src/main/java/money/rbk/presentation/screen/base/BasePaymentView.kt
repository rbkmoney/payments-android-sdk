package money.rbk.presentation.screen.base

import money.rbk.presentation.model.BrowserRequestModel

internal interface BasePaymentView : BaseView {
    fun showRedirect(request: BrowserRequestModel)
}
