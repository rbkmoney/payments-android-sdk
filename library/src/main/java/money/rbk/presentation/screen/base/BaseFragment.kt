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

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import money.rbk.presentation.activity.checkout.CheckoutActivity

abstract class BaseFragment<T : BaseView> : Fragment(), BaseView {

    protected abstract val presenter: BasePresenter<T>

    val navigator by lazy {
        (activity as? CheckoutActivity)?.navigator
            ?: throw TODO("BaseFragment can be attached only to CheckoutActivity")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this as? T ?: throw RuntimeException("")) //TODO: Check it
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }

}
