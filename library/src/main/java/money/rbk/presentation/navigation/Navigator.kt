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
import android.util.Log
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import money.rbk.R
import money.rbk.presentation.dialog.AlertButton
import money.rbk.presentation.dialog.showAlert
import money.rbk.presentation.screen.card.BankCardFragment
import money.rbk.presentation.screen.methods.PaymentMethodsFragment

// TODO: Activity Scope
class Navigator(
    private val activity: FragmentActivity,
    @IdRes
    private val containerId: Int) {

    fun openPaymentMethods() {
        if (activity.supportFragmentManager.findFragmentById(R.id.container) == null) {
            replaceFragmentInActivity(PaymentMethodsFragment.newInstance())
        }
    }

    fun openGooglePay() = inProgress()

    private fun inProgress() {
        Toast.makeText(activity, "Данный функционал находится в стадии разработки",
            Toast.LENGTH_LONG)
            .show()
    }

    fun openBankCard() {
        replaceFragmentInActivity(BankCardFragment.newInstance())
    }

    fun openInvoiceCancelled() {
        openErrorFragment(message = activity.getString(R.string.error_invoice_cancelled))
    }

    fun openSuccessFragment(@StringRes messageRes: Int, vararg formatArgs: String) {
        val finish = {
            activity.setResult(Activity.RESULT_OK)
            activity.finish()
        }

        activity.showAlert(
            activity.getString(R.string.label_successful_payment),
            activity.getString(messageRes, *formatArgs),
            positiveButtonPair = R.string.label_ok to finish)
    }

    //TODO: Make proper back stack
    fun back() {
        if (activity.supportFragmentManager.findFragmentById(R.id.container) is BankCardFragment) {
            replaceFragmentInActivity(PaymentMethodsFragment.newInstance())
        } else {
            activity.finish()
        }
    }

    fun openErrorFragment(
        @StringRes titleRes: Int = R.string.error_unpaid,
        @StringRes messageRes: Int,
        positiveButtonPair: AlertButton? = null,
        negativeButtonPair: AlertButton? = null) =
        openErrorFragment(
            titleRes,
            activity.getString(messageRes),
            positiveButtonPair,
            negativeButtonPair)

    fun openErrorFragment(
        @StringRes titleRes: Int = R.string.error_unpaid,
        message: CharSequence,
        positiveButtonPair: AlertButton? = null,
        negativeButtonPair: AlertButton? = null) {
        activity.showAlert(
            activity.getString(titleRes),
            message,
            positiveButtonPair = positiveButtonPair,
            negativeButtonPair = negativeButtonPair)
    }

    private fun replaceFragmentInActivity(fragment: Fragment) {
        activity.supportFragmentManager.transact {
            replace(containerId, fragment)
        }
    }

    private fun addFragmentToActivity(fragment: Fragment, tag: String) {
        activity.supportFragmentManager.transact {
            add(fragment, tag)
        }
    }

    private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
        beginTransaction().apply {
            action()
        }
            .commit()
    }

}
