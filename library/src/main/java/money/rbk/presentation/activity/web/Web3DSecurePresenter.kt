package money.rbk.presentation.activity.web

import money.rbk.R
import money.rbk.domain.converter.TERMINATION_URI
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.screen.base.BasePresenter

internal class Web3DSecurePresenter(
    navigator: Navigator
) : BasePresenter<Web3DSecureView>(navigator) {

    fun onError(url: String?) {
        if (!handleUrl(url)) {
            navigator.showAlert(
                R.string.rbk_label_error,
                R.string.rbk_error_connection,
                R.string.rbk_label_retry to {
                    view?.loadPage() ?: Unit
                },
                R.string.rbk_label_cancel to {
                    navigator.finishWithResult(Web3DSecureActivity.RESULT_NETWORK_ERROR)
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
