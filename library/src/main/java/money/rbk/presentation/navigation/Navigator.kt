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

import android.content.Intent
import android.util.SparseArray
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.AutoResolvableResult
import com.google.android.gms.wallet.AutoResolveHelper
import com.whiteelephant.monthpicker.MonthPickerDialog
import money.rbk.R
import money.rbk.presentation.screen.card.BankCardFragment
import money.rbk.presentation.screen.gpay.GpayFragment
import money.rbk.presentation.screen.methods.PaymentMethodsFragment
import money.rbk.presentation.screen.result.RepeatAction
import money.rbk.presentation.screen.result.ResultAction
import money.rbk.presentation.screen.result.ResultFragment

class Navigator(
    private val activity: FragmentActivity,
    @IdRes
    private val containerId: Int
) {

    private var pendingAction: ResultAction? = null

    private val expectedResultFragments = SparseArray<String>()

    fun getPendingActionAndClean(): ResultAction? =
        pendingAction.also {
            pendingAction = null
        }

    fun resolveTask(task: Task<out AutoResolvableResult>, requestCode: Int) {
        expectedResultFragments.put(requestCode, currentFragment?.tag)
        AutoResolveHelper.resolveTask(task, activity, requestCode)
    }

    fun safeOpenPaymentMethods() =
        (activity.supportFragmentManager.findFragmentById(R.id.container) == null).also {
            if (it) {
                replaceFragmentInActivity(PaymentMethodsFragment.newInstance())
            }
        }

    fun openPaymentMethods() {
        replaceFragmentInActivity(PaymentMethodsFragment.newInstance(), isRoot = true)
    }

    fun openGooglePay() {
        replaceFragmentInActivity(GpayFragment.newInstance())
    }

    fun openBankCard() {
        replaceFragmentInActivity(BankCardFragment.newInstance())
    }

    fun openWarningFragment(@StringRes titleRes: Int, @StringRes messageRes: Int) {
        replaceFragmentInActivity(
            ResultFragment.newInstanceUnknown(
                activity.getString(messageRes),
                activity.getString(titleRes)
            ),
            isRoot = true
        )
    }

    fun openSuccessFragment(@StringRes messageRes: Int, vararg formatArgs: String) {
        replaceFragmentInActivity(
            ResultFragment.newInstanceSuccess(
                activity.getString(messageRes, *formatArgs)
            ),
            isRoot = true
        )
    }

    fun openErrorFragment(
        @StringRes messageRes: Int,
        repeatAction: RepeatAction? = null,
        useAnotherCard: Boolean = false,
        allPaymentMethods: Boolean = false
    ) {

        replaceFragmentInActivity(ResultFragment.newInstanceError(
            message = activity.getString(messageRes),
            repeatAction = repeatAction,
            useAnotherCard = useAnotherCard,
            allPaymentMethods = allPaymentMethods
        ))
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean =
        expectedResultFragments[requestCode]?.let {
            activity.supportFragmentManager.findFragmentByTag(it)
                ?.let { fragment ->
                    expectedResultFragments.remove(requestCode)
                    fragment.onActivityResult(requestCode, resultCode, data)
                }

        } != null

    fun backWithAction(retryAction: ResultAction) {
        pendingAction = retryAction
        activity.supportFragmentManager.popBackStackImmediate()
    }

    fun showDateDialog(callback: MonthPickerDialog.OnDateSetListener,
        startYear: Int,
        maxYear: Int,
        startMonth: Int) {

        MonthPickerDialog.Builder(
            activity,
            callback,
            startYear,
            startMonth
        )
            .setYearRange(startYear, maxYear)
            .build()
            .show()
    }

    fun showAlert(
        @StringRes titleRes: Int,
        @StringRes messageRes: Int,
        positiveButtonPair: AlertButton? = null,
        negativeButtonPair: AlertButton? = null): AlertDialog =
        AlertDialog.Builder(activity)
            .setTitle(titleRes)
            .setOnCancelListener {
                negativeButtonPair?.second?.invoke()
            }
            .setMessage(messageRes)
            .apply {
                if (positiveButtonPair != null) {
                    setPositiveButton(positiveButtonPair.first) { _, _ -> positiveButtonPair.second() }
                }
                if (negativeButtonPair != null) {
                    setNegativeButton(negativeButtonPair.first) { _, _ -> negativeButtonPair.second() }
                }
            }
            .show()

    fun finishWithCancel() {
        activity.setResult(FragmentActivity.RESULT_CANCELED)
        activity.finish()
    }

    fun finishWithSuccess() {
        activity.setResult(FragmentActivity.RESULT_OK)
        activity.finish()
    }

    fun finish() {
        activity.finish()
    }

    /*  Private methods */

    private val currentFragment: Fragment?
        get() = activity.supportFragmentManager.findFragmentById(R.id.container)

    private fun replaceFragmentInActivity(fragment: Fragment,
        isRoot: Boolean = false) {
        activity.supportFragmentManager.transact {
            if (isRoot) {
                activity.supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
            }
            replace(containerId, fragment, fragment.javaClass.name)
            addToBackStack(fragment.javaClass.name)
        }
    }

    private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
        beginTransaction().apply {
            action()
        }
            .commitAllowingStateLoss()
    }

}

typealias AlertButton = Pair<Int, () -> Unit>
