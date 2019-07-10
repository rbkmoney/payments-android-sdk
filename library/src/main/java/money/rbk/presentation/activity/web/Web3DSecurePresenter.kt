package money.rbk.presentation.activity.web

import money.rbk.R
import money.rbk.domain.converter.TERMINATION_URI
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.screen.base.BasePresenter

/**
 * @author Arthur Korchagin (arth.korchagin@gmail.com)
 * @since 10.07.19
 */

class Web3DSecurePresenter(
    navigator: Navigator
) : BasePresenter<Web3DSecureView>(navigator) {

    fun onError(url: String?) {
        if (!handleUrl(url)) {
            navigator.showAlert(
                R.string.error,
                R.string.error_connection,
                R.string.label_try_again to {
                    view?.loadPage() ?: Unit
                },
                R.string.label_cancel to {
                    navigator.finishWithCancel()
                }
            )
        }
    }

    fun onRequest(url: String?) {
        handleUrl(url)
    }

    fun onCancel() {
        navigator.finishWithCancel()
    }
    
    private fun handleUrl(url: String?) = url.equals(TERMINATION_URI, ignoreCase = true)
        .also {
            if (it) {
                navigator.finishWithSuccess()
            }
        }

}
