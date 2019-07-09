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

import money.rbk.BuildConfig
import money.rbk.R
import money.rbk.data.exception.NetworkException
import money.rbk.presentation.navigation.Navigator
import money.rbk.presentation.screen.result.RepeatAction

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

    fun onError(error: Throwable, repeatAction: RepeatAction? = null) {
        if (BuildConfig.DEBUG) {
            error.printStackTrace()
        }

        view?.hideProgress()
        return when (error) {

            is NetworkException -> navigator.openErrorFragment(
                messageRes = R.string.error_connection,
                repeatAction = repeatAction,
                useAnotherCard = true,
                allPaymentMethods = true)

            else -> navigator.openErrorFragment(
                messageRes = R.string.error_busines_logic,
                repeatAction = repeatAction,
                useAnotherCard = true,
                allPaymentMethods = true)
        }
    }

    open fun onViewAttached(view: View) = Unit

    open fun onViewDetached() = Unit

}
