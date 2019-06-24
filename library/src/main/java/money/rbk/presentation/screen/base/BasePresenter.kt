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

package money.rbk.presentation.screen.base

import money.rbk.R
import money.rbk.data.exception.NetworkServiceException
import money.rbk.data.exception.ParseException
import money.rbk.presentation.dialog.AlertButton
import money.rbk.presentation.navigation.Navigator

abstract class BasePresenter<View : BaseView>(protected val navigator: Navigator) {

    protected var view: View? = null

    fun attachView(view: View) {
        this.view = view
        onViewAttached(view)
    }

    fun detachView() {
        this.view = null
        onViewDetached()
    }

    fun onError(error: Throwable, retryButton: AlertButton? = null) {
        when (error) {
            is NetworkServiceException -> error.process(retryButton)
            is ParseException -> error.process(retryButton)
        }

        view?.hideProgress()
        error.printStackTrace()
    }

    open fun onViewAttached(view: View) = Unit

    open fun onViewDetached() = Unit

    //TODO: Make different handling this branches
    private fun NetworkServiceException.process(retryButton: AlertButton?) =
        when (this) {

            NetworkServiceException.NoInternetException ->
                navigator.openErrorFragment(
                    messageRes = R.string.error_connection,
                    positiveButtonPair = retryButton)

            is NetworkServiceException.RequestExecutionException,
            is NetworkServiceException.ResponseReadingException,
            is NetworkServiceException.ApiException,
            is NetworkServiceException.InternalServerException ->
                navigator.openErrorFragment(
                    messageRes = R.string.error_busines_logic,
                    positiveButtonPair = retryButton)
        }

    private fun ParseException.process(retryButton: AlertButton?) {
        navigator.openErrorFragment(R.string.error, R.string.error_busines_logic,
            retryButton)
    }

}