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

package money.rbk.presentation.navigation

import android.app.Activity
import android.content.Intent
import android.util.SparseArray
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.AutoResolvableResult
import com.google.android.gms.wallet.AutoResolveHelper
import money.rbk.R
import money.rbk.presentation.dialog.showAlert
import money.rbk.presentation.screen.card.BankCardFragment
import money.rbk.presentation.screen.gpay.GpayFragment
import money.rbk.presentation.screen.methods.PaymentMethodsFragment
import money.rbk.presentation.screen.result.ResultFragment
import money.rbk.presentation.screen.result.ResultFragment.Companion.REQUEST_ERROR
import money.rbk.presentation.screen.result.ResultType

class Navigator(
    private val activity: FragmentActivity,
    @IdRes
    private val containerId: Int
) {

    private val expectedResultFragments = SparseArray<String>()

    fun resolveTask(task: Task<out AutoResolvableResult>, requestCode: Int) {
        expectedResultFragments.put(requestCode, currentFragment?.tag)
        AutoResolveHelper.resolveTask(task, activity, requestCode)
    }

    fun openPaymentMethods() {
        if (activity.supportFragmentManager.findFragmentById(R.id.container) == null) {
            replaceFragmentInActivity(PaymentMethodsFragment.newInstance())
        }
    }

    fun openGooglePay() {
        replaceFragmentInActivity(GpayFragment.newInstance())
    }

    fun openBankCard() {
        replaceFragmentInActivity(BankCardFragment.newInstance())
    }

    fun openInvoiceCancelled() {
        replaceFragmentInActivity(
            ResultFragment.newInstance(
                ResultType.ERROR,
                activity.getString(R.string.error_invoice_cancelled)
            )
        )
    }

    fun openWarningFragment(@StringRes titleRes: Int, @StringRes messageRes: Int) {
        val finish = {
            activity.setResult(Activity.RESULT_OK)
            activity.finish()
        }

        activity.showAlert(
            activity.getString(titleRes),
            activity.getString(messageRes),
            positiveButtonPair = R.string.label_ok to finish)
    }

    fun openSuccessFragment(@StringRes messageRes: Int, vararg formatArgs: String) {
        replaceFragmentInActivity(
            ResultFragment.newInstance(
                ResultType.SUCCESS,
                activity.getString(messageRes, *formatArgs)
            )
        )
    }

    //TODO: Make proper back stack
    fun back() {
        when (currentFragment) {
            is BankCardFragment -> replaceFragmentInActivity(PaymentMethodsFragment.newInstance())
            is ResultFragment -> addFragmentToActivity(BankCardFragment.newInstance())
            else -> activity.finish()
        }
    }

    fun openErrorFragment(
        parent: Fragment? = currentFragment,
        @StringRes messageRes: Int,
        positiveAction: Int? = null,
        negativeAction: Int? = null
    ) {

        val fragment = ResultFragment.newInstance(
            ResultType.ERROR,
            activity.getString(messageRes),
            positiveAction,
            negativeAction)
            .apply {
                if (parent != null) {
                    setTargetFragment(parent, REQUEST_ERROR)
                }
            }
        replaceFragmentInActivity(fragment)
    }

    private val currentFragment: Fragment?
        get() = activity.supportFragmentManager.findFragmentById(R.id.container)

    private fun replaceFragmentInActivity(fragment: Fragment) {
        activity.supportFragmentManager.transact {
            replace(containerId, fragment, fragment.javaClass.name)
        }
    }

    private fun addFragmentToActivity(fragment: Fragment) {
        activity.supportFragmentManager.transact {
            add(containerId, fragment, fragment.javaClass.name)
        }
    }

    private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
        beginTransaction().apply {
            action()
        }
            .commitAllowingStateLoss()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean =
        expectedResultFragments[requestCode]?.let {
            activity.supportFragmentManager.findFragmentByTag(it)
                ?.let { fragment ->
                    expectedResultFragments.remove(requestCode)
                    fragment.onActivityResult(requestCode, resultCode, data)
                }

        } != null

}
