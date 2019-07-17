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

    //TODO: Обработка не только интернет соединения
    fun onError(url: String?) {
        if (!handleUrl(url)) {
            navigator.showAlert(
                R.string.rbc_label_error,
                R.string.rbc_error_connection,
                R.string.rbc_label_retry to {
                    view?.loadPage() ?: Unit
                },
                R.string.rbc_label_cancel to {
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
