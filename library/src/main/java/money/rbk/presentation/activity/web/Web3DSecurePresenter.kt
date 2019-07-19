/*
 *
 * Copyright 2019 RBKmoney
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
